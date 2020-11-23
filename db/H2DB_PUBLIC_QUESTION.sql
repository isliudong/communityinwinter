INSERT INTO PUBLIC.QUESTION (TITLE, DESCRIPTION, GMT_CREATE, GMT_MODIFIED, CREATOR, COMMENT_COUNT, VIEW_COUNT, LIKE_COUNT, TAG, ID) VALUES ('hashmap并发问题详解', '# 线程不安全的HashMap

众所周知，HashMap是非线程安全的。而HashMap的线程不安全主要体现在resize时的死循环及使用迭代器时的fast-fail上。

注：本章的代码均基于JDK 1.7.0_67

## HashMap工作原理

### HashMap数据结构

常用的底层数据结构主要有数组和链表。数组存储区间连续，占用内存较多，寻址容易，插入和删除困难。链表存储区间离散，占用内存较少，寻址困难，插入和删除容易。

HashMap要实现的是哈希表的效果，尽量实现O(1)级别的增删改查。它的具体实现则是同时使用了数组和链表，可以认为最外层是一个数组，数组的每个元素是一个链表的表头。

### HashMap寻址方式

对于新插入的数据或者待读取的数据，HashMap将Key的哈希值对数组长度取模，结果作为该Entry在数组中的index。在计算机中，取模的代价远高于位操作的代价，因此HashMap要求数组的长度必须为2的N次方。此时将Key的哈希值对2^N-1进行与运算，其效果即与取模等效。HashMap并不要求用户在指定HashMap容量时必须传入一个2的N次方的整数，而是会通过Integer.highestOneBit算出比指定整数大的最小的2^N值，其实现方法如下。

```
public static int highestOneBit(int i) {
  i |= (i >>  1);
  i |= (i >>  2);
  i |= (i >>  4);
  i |= (i >>  8);
  i |= (i >> 16);
  return i - (i >>> 1);
}
```



由于Key的哈希值的分布直接决定了所有数据在哈希表上的分布或者说决定了哈希冲突的可能性，因此为防止糟糕的Key的hashCode实现（例如低位都相同，只有高位不相同，与2^N-1取与后的结果都相同），JDK 1.7的HashMap通过如下方法使得最终的哈希值的二进制形式中的1尽量均匀分布从而尽可能减少哈希冲突。

```
int h = hashSeed;
h ^= k.hashCode();
h ^= (h >>> 20) ^ (h >>> 12);
return h ^ (h >>> 7) ^ (h >>> 4);
```



## resize死循环

### transfer方法

当HashMap的size超过Capacity*loadFactor时，需要对HashMap进行扩容。具体方法是，创建一个新的，长度为原来Capacity两倍的数组，保证新的Capacity仍为2的N次方，从而保证上述寻址方式仍适用。同时需要通过如下transfer方法将原来的所有数据全部重新插入（rehash）到新的数组中。

```
void transfer(Entry[] newTable, boolean rehash) {
  int newCapacity = newTable.length;
  for (Entry<K,V> e : table) {
    while(null != e) {
      Entry<K,V> next = e.next;
      if (rehash) {
        e.hash = null == e.key ? 0 : hash(e.key);
      }
      int i = indexFor(e.hash, newCapacity);
      e.next = newTable[i];
      newTable[i] = e;
      e = next;
    }
  }
}
```



该方法并不保证线程安全，而且在多线程并发调用时，可能出现死循环。其执行过程如下。从步骤2可见，转移时链表顺序反转。

1. 遍历原数组中的元素
2. 对链表上的每一个节点遍历：用next取得要转移那个元素的下一个，将e转移到新数组的头部，使用头插法插入节点
3. 循环2，直到链表节点全部转移
4. 循环1，直到所有元素全部转移

### 单线程rehash

单线程情况下，rehash无问题。下图演示了单线程条件下的rehash过程
[![HashMap rehash single thread](C:\Users\liudong\Documents\mdDocument\Java\并发编程\hashmap并发问题详解.assets\single_thread_rehash.png)](http://www.jasongj.com/img/java/concurrenthashmap/single_thread_rehash.png)

### 多线程并发下的rehash

这里假设有两个线程同时执行了put操作并引发了rehash，执行了transfer方法，并假设线程一进入transfer方法并执行完next = e.next后，因为线程调度所分配时间片用完而“暂停”，此时线程二完成了transfer方法的执行。此时状态如下。

[![HashMap rehash multi thread step 1](C:\Users\liudong\Documents\mdDocument\Java\并发编程\hashmap并发问题详解.assets\multi_thread_rehash_1.png)](http://www.jasongj.com/img/java/concurrenthashmap/multi_thread_rehash_1.png)

接着线程1被唤醒，继续执行第一轮循环的剩余部分

```
e.next = newTable[1] = null
newTable[1] = e = key(5)
e = next = key(9)
```



结果如下图所示
[![HashMap rehash multi thread step 2](C:\Users\liudong\Documents\mdDocument\Java\并发编程\hashmap并发问题详解.assets\multi_thread_rehash_2.png)](http://www.jasongj.com/img/java/concurrenthashmap/multi_thread_rehash_2.png)

接着执行下一轮循环，结果状态图如下所示
[![HashMap rehash multi thread step 3](C:\Users\liudong\Documents\mdDocument\Java\并发编程\hashmap并发问题详解.assets\multi_thread_rehash_3.png)](http://www.jasongj.com/img/java/concurrenthashmap/multi_thread_rehash_3.png)

继续下一轮循环，结果状态图如下所示
[![HashMap rehash multi thread step 4](C:\Users\liudong\Documents\mdDocument\Java\并发编程\hashmap并发问题详解.assets\multi_thread_rehash_4.png)](http://www.jasongj.com/img/java/concurrenthashmap/multi_thread_rehash_4.png)

此时循环链表形成，并且key(11)无法加入到线程1的新数组。在下一次访问该链表时会出现死循环。

## Fast-fail

### 产生原因

在使用迭代器的过程中如果HashMap被修改，那么`ConcurrentModificationException`将被抛出，也即Fast-fail策略。

当HashMap的iterator()方法被调用时，会构造并返回一个新的EntryIterator对象，并将EntryIterator的expectedModCount设置为HashMap的modCount（该变量记录了HashMap被修改的次数）。

```
HashIterator() {
  expectedModCount = modCount;
  if (size > 0) { // advance to first entry
  Entry[] t = table;
  while (index < t.length && (next = t[index++]) == null)
    ;
  }
}
```



在通过该Iterator的next方法访问下一个Entry时，它会先检查自己的expectedModCount与HashMap的modCount是否相等，如果不相等，说明HashMap被修改，直接抛出`ConcurrentModificationException`。该Iterator的remove方法也会做类似的检查。该异常的抛出意在提醒用户及早意识到线程安全问题。

### 线程安全解决方案

单线程条件下，为避免出现`ConcurrentModificationException`，需要保证只通过HashMap本身或者只通过Iterator去修改数据，不能在Iterator使用结束之前使用HashMap本身的方法修改数据。因为通过Iterator删除数据时，HashMap的modCount和Iterator的expectedModCount都会自增，不影响二者的相等性。如果是增加数据，只能通过HashMap本身的方法完成，此时如果要继续遍历数据，需要重新调用iterator()方法从而重新构造出一个新的Iterator，使得新Iterator的expectedModCount与更新后的HashMap的modCount相等。

多线程条件下，可使用`Collections.synchronizedMap`方法构造出一个同步Map，或者直接使用线程安全的ConcurrentHashMap。

# Java 7基于分段锁的ConcurrentHashMap

注：本章的代码均基于JDK 1.7.0_67

## 数据结构

Java 7中的ConcurrentHashMap的底层数据结构仍然是数组和链表。与HashMap不同的是，ConcurrentHashMap最外层不是一个大的数组，而是一个Segment的数组。每个Segment包含一个与HashMap数据结构差不多的链表数组。整体数据结构如下图所示。
[![JAVA 7 ConcurrentHashMap](C:\Users\liudong\Documents\mdDocument\Java\并发编程\hashmap并发问题详解.assets\concurrenthashmap_java7.png)](http://www.jasongj.com/img/java/concurrenthashmap/concurrenthashmap_java7.png)

## 寻址方式

在读写某个Key时，先取该Key的哈希值。并将哈希值的高N位对Segment个数取模从而得到该Key应该属于哪个Segment，接着如同操作HashMap一样操作这个Segment。为了保证不同的值均匀分布到不同的Segment，需要通过如下方法计算哈希值。

```
private int hash(Object k) {
  int h = hashSeed;
  if ((0 != h) && (k instanceof String)) {
    return sun.misc.Hashing.stringHash32((String) k);
  }
  h ^= k.hashCode();
  h += (h <<  15) ^ 0xffffcd7d;
  h ^= (h >>> 10);
  h += (h <<   3);
  h ^= (h >>>  6);
  h += (h <<   2) + (h << 14);
  return h ^ (h >>> 16);
}
```



同样为了提高取模运算效率，通过如下计算，ssize即为大于concurrencyLevel的最小的2的N次方，同时segmentMask为2^N-1。这一点跟上文中计算数组长度的方法一致。对于某一个Key的哈希值，只需要向右移segmentShift位以取高sshift位，再与segmentMask取与操作即可得到它在Segment数组上的索引。

```
int sshift = 0;
int ssize = 1;
while (ssize < concurrencyLevel) {
  ++sshift;
  ssize <<= 1;
}
this.segmentShift = 32 - sshift;
this.segmentMask = ssize - 1;
Segment<K,V>[] ss = (Segment<K,V>[])new Segment[ssize];
```



## 同步方式

Segment继承自ReentrantLock，所以我们可以很方便的对每一个Segment上锁。

对于读操作，获取Key所在的Segment时，需要保证可见性(请参考[如何保证多线程条件下的可见性](http://www.jasongj.com/java/thread_safe/#Java如何保证可见性))。具体实现上可以使用volatile关键字，也可使用锁。但使用锁开销太大，而使用volatile时每次写操作都会让所有CPU内缓存无效，也有一定开销。ConcurrentHashMap使用如下方法保证可见性，取得最新的Segment。

```
Segment<K,V> s = (Segment<K,V>)UNSAFE.getObjectVolatile(segments, u)
```



获取Segment中的HashEntry时也使用了类似方法

```
HashEntry<K,V> e = (HashEntry<K,V>) UNSAFE.getObjectVolatile
  (tab, ((long)(((tab.length - 1) & h)) << TSHIFT) + TBASE)
```



对于写操作，并不要求同时获取所有Segment的锁，因为那样相当于锁住了整个Map。它会先获取该Key-Value对所在的Segment的锁，获取成功后就可以像操作一个普通的HashMap一样操作该Segment，并保证该Segment的安全性。
同时由于其它Segment的锁并未被获取，因此理论上可支持concurrencyLevel（等于Segment的个数）个线程安全的并发读写。

获取锁时，并不直接使用lock来获取，因为该方法获取锁失败时会挂起（参考[可重入锁](http://www.jasongj.com/java/multi_thread/#重入锁)）。事实上，它使用了自旋锁，如果tryLock获取锁失败，说明锁被其它线程占用，此时通过循环再次以tryLock的方式申请锁。如果在循环过程中该Key所对应的链表头被修改，则重置retry次数。如果retry次数超过一定值，则使用lock方法申请锁。

这里使用自旋锁是因为自旋锁的效率比较高，但是它消耗CPU资源比较多，因此在自旋次数超过阈值时切换为互斥锁。

## size操作

put、remove和get操作只需要关心一个Segment，而size操作需要遍历所有的Segment才能算出整个Map的大小。一个简单的方案是，先锁住所有Sgment，计算完后再解锁。但这样做，在做size操作时，不仅无法对Map进行写操作，同时也无法进行读操作，不利于对Map的并行操作。

为更好支持并发操作，ConcurrentHashMap会在不上锁的前提逐个Segment计算3次size，如果某相邻两次计算获取的所有Segment的更新次数（每个Segment都与HashMap一样通过modCount跟踪自己的修改次数，Segment每修改一次其modCount加一）相等，说明这两次计算过程中无更新操作，则这两次计算出的总size相等，可直接作为最终结果返回。如果这三次计算过程中Map有更新，则对所有Segment加锁重新计算Size。该计算方法代码如下

```
public int size() {
  final Segment<K,V>[] segments = this.segments;
  int size;
  boolean overflow; // true if size overflows 32 bits
  long sum;         // sum of modCounts
  long last = 0L;   // previous sum
  int retries = -1; // first iteration isn''t retry
  try {
    for (;;) {
      if (retries++ == RETRIES_BEFORE_LOCK) {
        for (int j = 0; j < segments.length; ++j)
          ensureSegment(j).lock(); // force creation
      }
      sum = 0L;
      size = 0;
      overflow = false;
      for (int j = 0; j < segments.length; ++j) {
        Segment<K,V> seg = segmentAt(segments, j);
        if (seg != null) {
          sum += seg.modCount;
          int c = seg.count;
          if (c < 0 || (size += c) < 0)
            overflow = true;
        }
      }
      if (sum == last)
        break;
      last = sum;
    }
  } finally {
    if (retries > RETRIES_BEFORE_LOCK) {
      for (int j = 0; j < segments.length; ++j)
        segmentAt(segments, j).unlock();
    }
  }
  return overflow ? Integer.MAX_VALUE : size;
}
```



## 不同之处

ConcurrentHashMap与HashMap相比，有以下不同点

- ConcurrentHashMap线程安全，而HashMap非线程安全
- HashMap允许Key和Value为null，而ConcurrentHashMap不允许
- HashMap不允许通过Iterator遍历的同时通过HashMap修改，而ConcurrentHashMap允许该行为，并且该更新对后续的遍历可见

# Java 8基于CAS的ConcurrentHashMap

注：本章的代码均基于JDK 1.8.0_111

## 数据结构

Java 7为实现并行访问，引入了Segment这一结构，实现了分段锁，理论上最大并发度与Segment个数相等。Java 8为进一步提高并发性，摒弃了分段锁的方案，而是直接使用一个大的数组。同时为了提高哈希碰撞下的寻址性能，Java 8在链表长度超过一定阈值（8）时将链表（寻址时间复杂度为O(N)）转换为红黑树（寻址时间复杂度为O(long(N))）。其数据结构如下图所示


[![JAVA 8 ConcurrentHashMap](C:\Users\liudong\Documents\mdDocument\Java\并发编程\hashmap并发问题详解.assets\concurrenthashmap_java8.png)](http://www.jasongj.com/img/java/concurrenthashmap/concurrenthashmap_java8.png)

## 寻址方式

Java 8的ConcurrentHashMap同样是通过Key的哈希值与数组长度取模确定该Key在数组中的索引。同样为了避免不太好的Key的hashCode设计，它通过如下方法计算得到Key的最终哈希值。不同的是，Java 8的ConcurrentHashMap作者认为引入红黑树后，即使哈希冲突比较严重，寻址效率也足够高，所以作者并未在哈希值的计算上做过多设计，只是将Key的hashCode值与其高16位作异或并保证最高位为0（从而保证最终结果为正整数）。

```
static final int spread(int h) {
  return (h ^ (h >>> 16)) & HASH_BITS;
}
```



## 同步方式

对于put操作，如果Key对应的数组元素为null，则通过[CAS操作](http://www.jasongj.com/java/thread_safe/#CAS（compare-and-swap）)将其设置为当前值。如果Key对应的数组元素（也即链表表头或者树的根元素）不为null，则对该元素使用synchronized关键字申请锁，然后进行操作。如果该put操作使得当前链表长度超过一定阈值，则将该链表转换为树，从而提高寻址效率。

对于读操作，由于数组被volatile关键字修饰，因此不用担心数组的可见性问题。同时每个元素是一个Node实例（Java 7中每个元素是一个HashEntry），它的Key值和hash值都由final修饰，不可变更，无须关心它们被修改后的可见性问题。而其Value及对下一个元素的引用由volatile修饰，可见性也有保障。

```
static class Node<K,V> implements Map.Entry<K,V> {
  final int hash;
  final K key;
  volatile V val;
  volatile Node<K,V> next;
}
```



对于Key对应的数组元素的可见性，由Unsafe的getObjectVolatile方法保证。

```
static final <K,V> Node<K,V> tabAt(Node<K,V>[] tab, int i) {
  return (Node<K,V>)U.getObjectVolatile(tab, ((long)i << ASHIFT) + ABASE);
}
```



## size操作

put方法和remove方法都会通过addCount方法维护Map的size。size方法通过sumCount获取由addCount方法维护的Map的size。', 1593849758545, 1593849758545, 769, 0, 4, 0, 'java', 800);
INSERT INTO PUBLIC.QUESTION (TITLE, DESCRIPTION, GMT_CREATE, GMT_MODIFIED, CREATOR, COMMENT_COUNT, VIEW_COUNT, LIKE_COUNT, TAG, ID) VALUES ('认识多线程', '### 目录

- 线程的简介
- 启动和终止线程
- 线程间通信
- 小结

------

### 线程的简介

* **什么是线程**
  线程是操作系统能够进行运算调度的最小单位。它被包含在进程之中，是进程中的实际运作单位。一条线程指的是进程中一个单一顺序的控制流，一个进程中可以并发多个线程，每条线程并行执行不同的任务。

- **为什么要使用多线程**
  目前的处理器核心越来越多，使用多线程能有更快的响应时间，并能有更好的编程模型。

- **线程优先级**
  现代操作系统基本采用时分的形式调度运行的线程，操作系统分出每一个时间片会根据线程的优先级来分配，优先级越高的最先获取执行资源。

  在Java线程中，通过一个整型成员变量`priority`来控制优先级，优先级的范围从`1~10`，在线程构建的时候可以通过`setPriority(int)`方法来修改优先级，默认优先级是`5`，优先级高的线程分配时间片的数量要多于优先级低的线程。

  线程优先级的设置:

  - **频繁阻塞**（休眠或者I/O操作）的线程需要设置 **较高优先级**，
  - **偏重计算**（需要较多CPU时间或者偏运算）的线程则设置 **较低的优先级**，确保处理器不会被独占。

  在不同的 **JVM** 以及 **操作系统** 上，线程规划会存在差异，有些操作系统甚至会忽略对线程优先级的设定。

  **线程优先级不能作为程序正确性的依赖**，因为操作系统可以完全不用理会 `Java` 线程对于优先级的设定。

- **线程的状态**

  - `NEW`  初始状态
  - `RUNNABLE` 运行状态
  - `BLOCKED` 阻塞状态
  - `WAITING` 等待状态
  - `TIME_WAITING` 超时等待状态
  - `TERMINATED` 终止状态

下图是状态变化的介绍：



![img](https:////upload-images.jianshu.io/upload_images/1709375-ccfe7bc796ff7067.png?imageMogr2/auto-orient/strip|imageView2/2/w/1085/format/webp)

Java线程状态变迁

- **Daemon线程（守护进程）**
  **Daemon** 线程是一种支持型线程，因为它主要被用作程序中后台调度以及支持性工作。
  这意味着，**当一个Java虚拟机中不存在非`Daemon`线程的时候，Java虚拟机将会退出**（`Daemon`线程不一定会执行完）。
  可以通过调用`Thread.setDaemon(true)`将线程设置为`Daemon`线程。需在启动之前设置。

------

### 启动和终止线程

线程随着 `thread.start()` 开始启动  到  `run()` 方法执行完毕 结束。

我们可以通过 `Thread.interrupted()` 方法中断线程。

> 中断可以理解为线程的一个标识位属性，它表示一个运行中的线程是否被其他线程进行了中断操作。
> 线程通过检查自身是否被中断来进行响应，线程通过方法`isInterrupted()`来进行判断是否被中断，也可以调用静态方法`Thread.interrupted()`对当前线程的中断标识位进行复位。如果该线程已经处于终结状态，即使该线程被中断过，在调用该线程对象的`isInterrupted()`时依旧会返回`false`。
> 许多声明抛出`InterruptedException`的方法（例如`Thread.sleep(long millis)`方法）这些方法在抛出`InterruptedException`之前，Java虚拟机会先将该线程的中断标识位清除，然后抛出`InterruptedException`，此时调用`isInterrupted()`方法将会返回`false`。

下面看一个例子：



```csharp
public static void main(String[] args) throws InterruptedException {
    Thread t1 = new Thread(() -> {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(10);
                System.out.println("time = " + System.currentTimeMillis() / 1000 + ", i = " + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }, "t1");
    Thread t2 = new Thread(() -> {
        while (true) {
            i++;
        }
    }, "t2");

    //设置为 daemon 线程 并启动
    t1.setDaemon(true);
    t2.setDaemon(true);
    t1.start();
    t2.start();

    //让t1 t2 运行3s
    TimeUnit.SECONDS.sleep(3);

    //中断线程
    t1.interrupt();
    t2.interrupt();
    //获取中断状态
    System.out.println("time = " + System.currentTimeMillis() / 1000 + ", t1.isInterrupted() = " + t1.isInterrupted());
    System.out.println("time = " + System.currentTimeMillis() / 1000 + ", t2.isInterrupted() = " + t2.isInterrupted());
    //防止 t1 t2 立即退出
    TimeUnit.SECONDS.sleep(15);
}
```

输出结果：



```bash
time = 1560134045, t1.isInterrupted() = false
time = 1560134045, t2.isInterrupted() = true
java.lang.InterruptedException: sleep interrupted
    at java.lang.Thread.sleep(Native Method)
    at java.lang.Thread.sleep(Thread.java:340)
    at java.util.concurrent.TimeUnit.sleep(TimeUnit.java:386)
    at com.tcl.executors.Test.lambda$main$0(Test.java:16)
    at java.lang.Thread.run(Thread.java:745)
time = 1560134055, i = -576615207
```

根据输出结果，我们知道在线程`sleep`的时候，调用 `isInterrupted()` 会导致 `sleep interrupted` 异常，并且中断标记也被清除了。

已经被废弃的 `suspend()`（暂停）、`resume()`（恢复） 和 `stop()`（停止）。
 废弃原因是，在调用方法之后，线程不会保证占用的资源被正常释放。
 示例：



```csharp
public static void main(String[] args) throws InterruptedException {
    Thread t = new Thread(() -> {
        while (true) {
            System.out.println("time = " + System.currentTimeMillis() / 1000);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });
    t.setDaemon(true);
    t.start();
    TimeUnit.SECONDS.sleep(3);

    t.suspend();
    System.out.println("suspend time = " + System.currentTimeMillis() / 1000);
    TimeUnit.SECONDS.sleep(3);

    t.resume();
    System.out.println("resume time = " + System.currentTimeMillis() / 1000);
    TimeUnit.SECONDS.sleep(3);

    t.stop();
    System.out.println("stop time = " + System.currentTimeMillis() / 1000);
    TimeUnit.SECONDS.sleep(3);
}
```

输出结果：



```bash
time = 1560134529
time = 1560134530
time = 1560134531
suspend time = 1560134532
resume time = 1560134535
time = 1560134535
time = 1560134536
time = 1560134537
stop time = 1560134538
```

------



### 线程间通信

##### volatile和synchronized关键字

`volatile`修饰的变量，程序访问时都需要在共享内存中去读取，对它的改变也必须更新共享内存，保证了线程对变量访问的可见性。

`synchronized`：对于 **同步块** 的实现使用了`monitorenter`和`monitorexit`指令，而 **同步方法** 则是依靠方法修饰符上的`ACC_SYNCHRONIZED`来完成的。无论采用哪种方式，其本质是对一个对象的监视器`monitor`进行获取，而这个获取过程是排他的，也就是同一时刻只能有一个线程获取到由`synchronized`所保护对象的监视器。

------

##### 等待/通知机制——wait和notify

指一个`线程A`调用了`对象O`的`wait()`方法进入等待状态，而另一个`线程B`调用了`对象O`的`notify()`或者`notifyAll()`方法，`线程A`收到通知后从`对象O`的`wait()`方法返回，进而执行后续操作。
 等待：`wait()`、`wait(long)`、`wait(long, int)`
 通知：`notify()`、`notifyAll()`
 示例：



```csharp
private static Object object = new Object();

public static void main(String[] args) throws InterruptedException {
    Thread t1 = new Thread(() -> {
        try {
            TimeUnit.SECONDS.sleep(1);
            synchronized (object) {
                System.out.println("t1 start object.wait(), time = " + System.currentTimeMillis() / 1000);
                object.wait();
                System.out.println("t1 after object.wait(), time = " + System.currentTimeMillis() / 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    });
    Thread t2 = new Thread(() -> {
        try {
            TimeUnit.SECONDS.sleep(5);
            synchronized (object) {
                System.out.println("t2 start object.notify(), time = " + System.currentTimeMillis() / 1000);
                object.notify();
                System.out.println("t2 after object.notify(), time = " + System.currentTimeMillis() / 1000);
            }

            synchronized (object) {
                System.out.println("t2  hold lock again, time = " + System.currentTimeMillis() / 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    });
    t1.start();
    t2.start();
}
```

输出结果：



```csharp
t1 start object.wait(), time = 1560138112
t2 start object.notify(), time = 1560138116
t2 after object.notify(), time = 1560138116
t2  hold lock again, time = 1560138116
t1 after object.wait(), time = 1560138116
```

> 1.使用`wait()`、`notify()`和`notifyAll()`时需要先对调用对象加锁,否则会报`java.lang.IllegalMonitorStateException`异常。
> 2.调用`wait()`方法后，线程状态由`RUNNING`变为`WAITING`，并将当前线程放置到对象的等待队列。
> 3.`notify()`或`notifyAll()`方法调用后，等待线程依旧不会从`wait()`返回，需要调用`notify()`或`notifAll()`的线程释放锁之后，等待线程才有机会从`wait()`返回。
> 4.`notify()`方法将等待队列中的一个等待线程从等待队列中移到同步队列中，而`notifyAll()`方法则是将等待队列中所有的线程全部移到同步队列，被移动的线程状态由`WAITING`变为`BLOCKED`。
> 5.从`wait()`方法返回的前提是获得了调用对象的锁。

------

##### 等待/通知的经典范式

包括 **等待方**（消费者）和 **通知方**（生产者）。
 等待方遵循以下原则：

- 获取对象的锁。
- 如果条件不满足，那么调用对象的`wait`方法，被通知后任要检查条件。
- 条件不满足则执行对应的逻辑。

对应代码如下：



```bash
synchronized (对象) {
    while (条件不满足) {
        对象.wait();
    }
    对应的处理逻辑
}
```

通知方遵循以下原则：

- 获取对象的锁。
- 改变条件。
- 通知所有在等待在对象上的线程。



```java
synchronized (对象) {
    改变条件
    对象.notifyAll();
}
```

------

##### 管道输入/输出流

`PipedOutputStream`、`PipedInputStream`、`PipedReader` 和 `PipedWriter`。
 示例代码：



```csharp
private static PipedWriter writer;
private static PipedReader reader;

public static void main(String[] args) throws InterruptedException, IOException {
    writer = new PipedWriter();
    reader = new PipedReader();
    //绑定输入输出
    writer.connect(reader);
    Thread t = new Thread(() -> {
        int res;
        try {
            while ((res = reader.read()) != -1) {
                System.out.print((char) res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
    t.start();

    int res;
    while ((res = System.in.read()) != -1) {
        System.out.println(res);
        writer.write(res);
        //按回车结束
        if (res == 10) {
            break;
        }
    }
    writer.close();
}
```

输出：



```swift
Hi!
72
105
33
10
Hi!
```

------

##### Thread.join()

`thread.join()` 即当前线程需要在 `thread` 线程执行完之后才能继续执行，[Java Thread.join()详解](https://www.jianshu.com/p/595be9eab056)，这里已经做了详细介绍了，就不再赘述了。

------

##### ThreadLocal

`ThreadLocal`,即线程变量，是一个以`ThreadLocal`对象弱引用为 **键** 、`任意对象` 为 **值** 的存储结构。
 这个结构被附带在线程上，也就是说一个线程可以根据一个`ThreadLocal`对象查询到绑定在这个线程上的一个值。
 可以通过 `set(T t)` 设置， `get()` 获取。
 示例如下：



```csharp
private static ThreadLocal<String> threadLocal = new ThreadLocal<>();

public static void main(String[] args) throws InterruptedException {
    String time = String.valueOf(System.currentTimeMillis() / 1000);
    System.out.println("time = " + time);
    threadLocal.set(time);
    Thread t = new Thread(() -> {
        String time1 = String.valueOf(System.currentTimeMillis());
        System.out.println("time1 = " + time1);
        threadLocal.set(time1);
    });
    t.start();
    TimeUnit.SECONDS.sleep(5);
    System.out.println("threadLocal.get() = " + threadLocal.get());
}
```

输出结果：



```csharp
time = 1560146178
time1 = 1560146178263
threadLocal.get() = 1560146178
```

可以看到`线程t`中对`threadLocal`设置的值，并不影响`main线程`中的值。

`set(T value)`方法的源代码：
 可以看到即把 **当前ThreadLocal对象** 为 `key`，**传入的参数** 为`value` 保存在 `ThreadLocalMap`中。



```csharp
public void set(T value) {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null)
        map.set(this, value);
    else
        createMap(t, value);
}
```

------

### 小结

- 什么是线程，为什么使用多线程，线程的优先级、状态变化 以及 `Deamon`线程。
- 线程启动`start()` 和 中断线程`interrupt()`，以及过期的`suspend()`、`resume()`、`stop()`的作用。
- 通过 `volatile` 来 `synchronized`来保证变量在多线程中的可见性，实现线程间通信。
- 用 线程的 **等待/通知** 机制 来 实现线程间通信，使用的注意事项 以及 **等待方** 和 **通知方** 需要遵循的原则。
- 通过管道输入输出流 `PipedOutputStream`、`PipedInputStream`、`PipedWirter`、`PipedReader`的介绍。
- Thread.join() ：阻塞主线程直到被join线程执行结束。
- `ThreadLocal`的使用介绍。', 1593850526368, 1593850526368, 769, 0, 3, 0, 'java', 801);
INSERT INTO PUBLIC.QUESTION (TITLE, DESCRIPTION, GMT_CREATE, GMT_MODIFIED, CREATOR, COMMENT_COUNT, VIEW_COUNT, LIKE_COUNT, TAG, ID) VALUES ('git学习记录', 'git log:日志

git show +commitId；显示某个commit详情

git reset +commId: 撤回commit至某个commId

git add . :添加所有文件

git branch branch1：创建分支branch1

git checkout branch1：切换至分支branch1

git push --set-upstream origin branch1：提交到远程分支，创建远程分支

 git checkout -b branch2：新创建分支branch2并切换至它

git pull:拉取远端更新

 git merge branch1：合并分支branch1

 git merge origin/branch1：从远程合并分支branch1', 1593850604777, 1593850604777, 769, 0, 4, 0, 'git', 802);
INSERT INTO PUBLIC.QUESTION (TITLE, DESCRIPTION, GMT_CREATE, GMT_MODIFIED, CREATOR, COMMENT_COUNT, VIEW_COUNT, LIKE_COUNT, TAG, ID) VALUES ('ReentrantLock类关系', '### ReentrantLock
![](/image_sever/fba59ffa932b4e31959ab39d2ec42478.png)

+ 可重入
+ 调用lockInterruptibly()方法获取锁可打断
+ 锁超时
+ 支持公平锁
+ 条件变量:等待支持分类等待，而不是像synchronized所有等待在一起', 1593850787743, 1593850787743, 769, 0, 7, 0, 'java', 803);
INSERT INTO PUBLIC.QUESTION (TITLE, DESCRIPTION, GMT_CREATE, GMT_MODIFIED, CREATOR, COMMENT_COUNT, VIEW_COUNT, LIKE_COUNT, TAG, ID) VALUES ('数据结构之伐木工', '许多程序要解决的关键问题是：快速定位特定排序项的能力。

第一类：散列

第二类：字符串查找

第三类：树算法

**二叉树:**

二叉树是每个结点最多有两个子树的树结构。

**树的用途：**

* 树：可以在辅助存储器中存储大量的数据。

* 二叉树、红黑树和伸展树：主要适用于内存中的工作。

* B树：打算用于辅助存储器，比如硬盘。

**二叉查找树：**

当使用二叉树存储大量数据时，只需遵循一条简单的规则：当使用中序遍历时，在每个节点中存储的数据的键将具有递增的顺序。

* 二叉查找树的5种基本操作：

  树创建

  树查找

  节点插入

  节点删除

  树遍历

二叉查找树删除考虑的情况要多一些。

 

* 二叉查找树的性能

  1、如果有N个随机分布的节点，平均高度应该为lgN。

  2、通过加载一组数据而得到的二叉查找树的形状不仅依赖于数据项，而且依赖于加载它们的顺序。这种类型的树没有内在的机制用于阻止子树之间的失衡。（分布不够随机）

 

**AVL平衡树**

* 平衡树：

  1、如果知道了树中数据项的使用频率，可以构建加权平均树（weight-balanced tree）。

  2、不过一般都假定数据项的使用频率相同。在这种情况下，将强制树是高度平衡的（height-balanced）；



* AVL树是一种遵守以下规则的二叉查找树：

  1、任何给定节点的子树的高度最多相差1。

* 在插入或删除之后，如果引入了失衡情况，就必须重新平衡树。

  两种修复失衡的操作：单旋转、双旋转（ll型右旋、lr型先左旋成ll再右旋；其他类似）

 

* AVL的规则作用是：

  确保树永远不会在实质上变为失衡状态，并且可以证明具有N个节点的AVL树的高度将和lgN成正比。

**红黑树**（更容易实现的一种平衡树）

* 红黑树（也是二叉查找树）：

  1、数据只存储在叶节点中（leaf）。也就是只有不带子节点的节点才会包含实际的数据，内部节点只用于引用。

  2、将每个节点都视作带有红色或者黑色。

* 颜色的确定规则：

  1、叶节点都是黑色

  2、沿着从根出发的任何路径上都不允许出现2个连续的红色节点。

  3、树的所有叶节点都必须具有相同的黑色深度，它被定义为叶节点与根节点之间的黑色节点的数量减1。

 

**伸展树**

1、相对于AVL，Splay的实现更为简捷。伸展树无需时刻都严格地保持全树的平衡，但却能够在任何足够长的真实操作序列中，保持分摊意义上的高效率。伸展树也不需要对基本的二叉树节点结构，做任何附加的要求或改动，更不需要记录平衡因子或高度之类的额外信息，故适用范围更广。

2、用途：

通常在任意数据结构的生命期内，执行不同操作的概率往往极不均衡，而且各操作之间具有极强的相关性，并在整体上多呈现出极强的规律性。其中最为典型的，就是所谓的“数据局部性”(data locality)，这包括两个方面的含义:

- 刚刚被访问过的元素，极有可能在不久之后再次被访问到
- 将被访问的下一元素，极有可能就处于不久之前被访问过的某个元素的附近

 3、构建思想：

只需将刚被访问的节点，及时地“转移”至树根(附近)，即可加速后续的操作。当然， 转移前后的搜索树必须相互等价，因此使用树相关的“旋转“等价变换的操作。

**B树**

上面描述的几种树算法对于完全可以在内存中维护的数据工作得很好。

否则需要一种适用于磁盘存储器的树算法：

与访问内存中数据的速度比，访问磁盘上的数据非常慢

最好相对于相当大的数据块执行读、写操作。实际上，一次读入越多，就会做得更好。

 

磁盘存储器的限制意味着必须尝试最高效地使用大块磁盘存储器，应围绕这个来组织算法，并且就将多份数据放入每个块中。

 

所有的B树都利用了两种截然不同的块：索引块和数据块。

数据块是B树的叶节点，并且所有的数据都存储于其中。

索引块是上层块，它们只包含允许程序描绘从根到数据块中的想要记录的路径所需的足够信息。

B树的关键特性如下：

所有数据块都位于相同层

所有的索引块和数据块都包含某个最低限度的数据量。

索引块中的数据项只是键。

 

这些特性使得在某种意义上B树至少是平衡的。

由于所有的数据都处于树中的相同深度，对于树的各个部分将具有同样快的访问速度。

通常把B树描述为阶d，其中树中的每个节点都包含d~2d个键或记录。

 

保持B树平衡

当块变得太空或者太满时，就会发生问题。

太满时，必须把块分割成两块，并在块的父块中插入一个新键。一直到根块。

太空时，就会把相邻两个块合并。

 

实现B树的算法

分割和合并数据块与索引块需要做的工作很复杂，并且充斥着一些特殊情况。

不过通过几个简化假设，可以大大减小复杂性，同时实现一个令人满意的B树例程。

允许可变长度的键

允许可变长度的记录

把索引和数据保存在单独的文件中。

为实现这些特性，同时轻松应对特殊情况，应该做以下假设：

在创建数据集时，必须提供具有最大键的单个记录。不能删除这个记录，这个记录的存在意味着树永远不会为空。

树将具有固定高度，并且在创建B树时定义这个高度。

这两条假设的实际效果是：空树将包含多个索引块层以及一个最大的数据记录。

高度为4的树甚至对于最苛刻的应用程序也绰绰有余。

 

B树源代码模块

B树头文件

创建B树

打开现有的数据集

操作数据集

阻塞缓冲

驱动程序头部

示例驱动程序

 

B树的源程序好复杂啊

 

 

**B+树**

![image-20200629111055554](C:\Users\liudong\AppData\Roaming\Typora\typora-user-images\image-20200629111055554.png)

B+树是B树的一个升级版，相对于B树来说B+树更充分的利用了节点的空间，让查询速度更加稳定，其速度完全接近于二分法查找。为什么说B+树查找的效率要比B树更高、更稳定；我们先看看两者的区别

**特点**

1、B+**树的层级更少**：相较于B树B+每个**非叶子**节点存储的关键字数更多，树的层级更少所以查询数据更快；

2、B+**树查询速度更稳定**：B+所有关键字数据地址都存在**叶子**节点上，所以每次查找的次数都相同所以查询速度要比B树更稳定;

3、B+**树天然具备排序功能：**B+树所有的**叶子**节点数据构成了一个有序链表，在查询大小区间的数据时候更方便，数据紧密性很高，缓存的命中率也会比B树高。

4、B+**树全节点遍历更快：**B+树遍历整棵树只需要遍历所有的**叶子**节点即可，，而不需要像B树一样需要对每一层进行遍历，这有利于数据库做全表扫描。

**B树**相对于**B+树**的优点是，如果经常访问的数据离根节点很近，而**B树**的**非叶子**节点本身存有关键字其数据的地址，所以这种数据检索的时候会要比**B+树**快

http://www.cburch.com/cs/340/reading/btree/



但是把所有的的数据按顺序一行接一行的存储在表中是不切实际的，因为在每次删除或者插入的时候要重写整个表。这使得我们想要把数据以树的方式存储。首先考虑到的是像红黑树这样的平衡二叉搜索树（balanced binary search tree），但是这对于存储在磁盘上的数据库而言意义不大。你会发现，磁盘的工作方式是通过一次读写数据的整个块，典型的是一次512字节或者4kb。而二叉搜索树的一个节点只用到这些数据中的很少一部分，因此需要找到一种和磁盘工作方式更为匹配的算法；由于B+树，由于在一个节点中可以存储d个子代和多达d-1个键。每个引用可以看作在节点的两个键之间的值。

下面是一个d=4的一个很小的树。

[![btree-6](https://images2015.cnblogs.com/blog/806053/201605/806053-20160508142320452-941183061.png)](http://images2015.cnblogs.com/blog/806053/201605/806053-20160508142319968-1622578790.png)

一个B+树需要每个叶子和根节点的距离相同，正如图中所示，其中搜索任何11个值中的一个只需涉及到从磁盘中载入3个节点（根块，第二层的块，和叶子）。

实际上，d可以更大，大到可以占满整个块。假设一个块是4kb，我们的键是4个字节的整数，每个引用是6字节文件的偏移量。

那么我们可以选择一个值使得4(d-1) + 6d ≤ 4096；结果是d ≤ 410。

一颗B+树维持下面不变的特性：

- 每个节点的引用比它的键要多1
- 所有的叶子与根节点之间的距离是相同的
- 对于每个有k个键值的非叶节点N：在第一个孩子的子树中所有的键值都要小于N的第一个键；并且在第i个孩子的子树中的键值都是在节点N的第(i-1)个键和第i个键之间。
- 根节点至少有两个娃
- 每个非叶节点和非根节点至少包含floor(d/2)个娃
- 每个叶子至少包含floor(d/2)个键
- 在叶子中出现的每个键，以从左到右有序排列

插入算法

1. 如果节点中有空位置，那么插入键/引用对到节点中
2. 如果节点已经满了，则将节点分割为两个结点，对半分配键值到两个节点。如果这个节点是叶子，取出第二个节点的最小值，并继续插入算法，然后把这个值插入到父节点。如果节点不是叶子，排除分割时中间的值，并重复插入算法，把排出的这个值插入到父节点。

Initial:
![img](http://www.cburch.com/cs/340/reading/btree/btree-0.png)

Insert 20:
![img](http://www.cburch.com/cs/340/reading/btree/btree-1.png)

Insert 13:
![img](http://www.cburch.com/cs/340/reading/btree/btree-2.png)

Insert 15:
![img](http://www.cburch.com/cs/340/reading/btree/btree-3.png)

Insert 10:
![img](http://www.cburch.com/cs/340/reading/btree/btree-4.png)

Insert 11:
![img](http://www.cburch.com/cs/340/reading/btree/btree-5.png)

Insert 12:
![img](http://www.cburch.com/cs/340/reading/btree/btree-6.png)

删除算法

1. 移除节点中需要删除的键和对应的引用
2. 如果节点仍然有足够的键和引用来满足B+树的特征，则可以停止
3. 如果节点的键太少了以至于满足不了不变性，如果它旁边同一层中最接近的节点有多余的，则重新分配这些键给这个节点和邻近节点。修复上一层代表这些节点的键，有不同的分割点。这里仅仅涉及到上一层键的改变，而不用插入或者删除键。
4. 如果节点的键太少了以至于满足不了特性，而旁边的节点也只是恰好够，然后将该节点和它的兄弟节点合并；如果节点是非叶子，我们需要将父节点的分割键整合到一起。在另外的情况下，需要对父节点重复删除算法来移除这些原来用来分隔这些合并的节点的键，直到父节点是根节点，然后从根节点移除最后的键，直到合并的节点成为新的根节点（然后这棵树的高度要比原来的少1层）。

Initial:
![img](http://www.cburch.com/cs/340/reading/btree/btree-6.png)

Delete 13:
![img](http://www.cburch.com/cs/340/reading/btree/btree-7.png)

Delete 15:
![img](http://www.cburch.com/cs/340/reading/btree/btree-8.png)

Delete 1:
![img](http://www.cburch.com/cs/340/reading/btree/btree-9.png)', 1593850970258, 1593850970258, 769, 1, 24, 0, 'java', 804);
INSERT INTO PUBLIC.QUESTION (TITLE, DESCRIPTION, GMT_CREATE, GMT_MODIFIED, CREATOR, COMMENT_COUNT, VIEW_COUNT, LIKE_COUNT, TAG, ID) VALUES ('面试常见问题', '## **01 JAVA基础**

**1.1 java知识点**

- Hashmap 源码级掌握，扩容，红黑树，最小树化容量，hash冲突解决，有些面试官会提出发自灵魂的审问，比如为什么是红黑树，别的树不可以吗；为什么8的时候树化，4不可以吗，等等

- 答：

- concureentHashMap，段锁，如何分段，和hashmap在hash上的区别，性能，等等

- HashTable ，同步锁，这块可能会问你synchronized关键字 1.6之后提升了什么，怎么提升的这些？

  JDK1.6对锁的实现引入了大量的优化，如偏向锁、轻量级锁、自旋锁、适应性自旋锁、锁消除、锁粗化等技术来减少锁操作的开销。

  锁主要存在四种状态，依次是：无锁状态、偏向锁状态、轻量级锁状态、重量级锁状态，他们会随着竞争的激烈而逐渐升级。注意锁可以升级不可降级，这种策略

  轻量级锁自旋锁：

  自旋锁原理非常简单，如果持有锁的线程能在很短时间内释放锁资源，那么那些等待竞争锁的线程就不需要做内核态和用户态之间的切换进入阻塞挂起状态，它们只需要等一等（自旋），等持有锁的线程释放锁后即可立即获取锁，这样就避免用户线程和内核的切换的消耗；但是线程自旋是需要消耗cup的，说白了就是让cup在做无用功，超过自旋等待的最大时间扔没有释放锁，线程会停止自旋进入阻塞状态。适用于竞争不激烈时

  Java偏向锁：

  (Biased Locking)是Java6引入的一项多线程优化

  偏向锁，顾名思义，它会偏向于第一个访问锁的线程，如果在运行过程中，同步锁只有一个线程访问，不存在多线程争用的情况，则线程是不需要触发同步的，这种情况下，就会给线程加一个偏向锁。 
  如果在运行过程中，遇到了其他线程抢占锁，则持有偏向锁的线程会被挂起，JVM会消除它身上的偏向锁，将锁恢复到标准的轻量级锁。

  它通过消除资源无竞争情况下的同步原语，进一步提高了程序的运行性能。

  轻量级锁：

  轻量级锁是由偏向所升级来的，偏向锁运行在一个线程进入同步块的情况下，当第二个线程加入锁争用的时候，偏向锁就会升级为轻量级锁。

  锁粗化：

  大部分情况下我们是要让锁的粒度最小化，锁的粗化则是要增大锁的粒度; 在以下场景下需要粗化锁的粒度： 假如有一个循环，循环内的操作需要加锁，我们应该把锁放到循环外面，否则每次进出循环，都进出一次临界区，效率是非常差的。

  重量级锁Synchronized：

  它可以把任意一个非NULL的对象当作锁。

  1. 作用于方法时，锁住的是对象的实例(this)；

  2. 当作用于静态方法时，锁住的是Class实例，又因为Class的相关数据存储在永久带PermGen（jdk1.8则是metaspace），永久带是全局共享的，因此静态方法锁相当于类的一个全局锁，会锁所有调用该方法的线程；

  3. synchronized作用于一个对象实例时，锁住的是所有以该对象为锁的代码块

     总结图：

     ![image-20200311223154372](C:\Users\liudong\AppData\Roaming\Typora\typora-user-images\image-20200311223154372.png)

- ArrayList 优势，扩容，什么时候用

- LinkedList 优势，什么时候用，和arraylist的区别 等等

- 基本类型和包装类型的区别，涉及自动装箱和拆箱，怎么做的，原理

- String ，StringBuffer，StringBuilder哪个是安全的

- 字符串编码的区别，被问到过，我觉得比较容易被忽视的一个点

- 什么是泛型，怎么用泛型

- static能不能修饰threadLocal，为什么，这道题我当时一听到其实挺懵逼的

- Comparable和Comparator接口是干什么的，其区别

- 多态的原理是什么，感觉这个很容易被问到

- 接口和抽象类，面试官问我是怎么理解的，我说接口对应功能，抽象类对应属性，然后面试官给我说了他的看法，说抽象类更偏向于一种模板~ 然后又交流了一下各自的想法

- 如何通过反射和设置对象私有字段的值

- 快速失败(fail-fast)和安全失败(fail-safe)的区别是什么

- synchronized 的实现原理以及锁优化？

- volatile 的实现原理？

- Java 的信号灯？

- synchronized 在静态方法和普通方法的区别？

- 怎么实现所有线程在等待某个事件的发生才会去执行？

- CAS？CAS 有什么缺陷，如何解决？

- synchronized 和 lock 有什么区别？

- Hashtable 是怎么加锁的 ？

- List，Map，Set接口在取元素师，各有什么特点

- 如何线程安全的实现一个计数器

- 生产者消费者模式，要求手写过代码，还是要知道的

- 单例模式，饿汉式，懒汉式，线程安全的做法，两次判断instance是否为空，每次判断的作用是什么。

- 线程池，这个还是很重要的，在生产中用的挺多，四个线程池类型，其参数，参数的理解很重要，corepoolSize怎么设置，maxpoolsize怎么设置，keep-alive各种的，和美团面试官探讨过阻塞队列在生产中的设置，他说他一般设置为0，防止用户阻塞

- cyclicbarrier 和countdownlatch的区别，个人理解 赛马和点火箭

- 线程回调，这块 被问过让我设计一个RPC，怎么实现，其实用到了回调这块的东西

- sleep 和yeild方法有什么区别

- volatile关键字，可见性。

- 乐观锁和悲观锁的使用场景

- 悲观锁的常见实现方式：lock synchronized retreentlock

- 乐观锁：CAS MVCC

- 读写锁的实现方式，16位int的前八位和后八位分别作为读锁和写锁的标志位

- 死锁的条件，怎么解除死锁，怎么观测死锁。

- 希望大家能够好好看一下反射的原理，怎么确定类，怎么调方法

- RPC框架，同步异步，响应时间，这些都被问到过，还让设计过

- 同步，异步，阻塞，非阻塞 在深信服的面试中遇到过，最好再找一些应用场景加以理解

**1.2 JVM**

- 内存模型以及分区，需要详细到每个区放什么。
- 堆里面的分区：Eden，survival （from+ to），老年代，各自的特点。
- 对象创建方法，对象的内存分配，对象的访问定位。
- GC 的两种判定方法
- GC 的三种收集方法：标记清除、标记整理、复制算法的原理与特点，分别用在什么地方，如果让你优化收集方法，有什么思路？
- GC 收集器有哪些？CMS 收集器与 G1 收集器的特点
- Minor GC 与 Full GC 分别在什么时候发生？
- JVM 内存分哪几个区，每个区的作用是什么?
- 如和判断一个对象是否存活?(或者 GC 对象的判定方法)
- java 中垃圾收集的方法有哪些?
- 类加载器双亲委派模型机制？
- java 内存模型，java 类加载过程?
- 什么是类加载器，类加载器有哪些?
- 简述 java 内存分配与回收策率以及 Minor GC 和Major GC

## **02 数据库**

**2.1 MySQL**

- 事务四大特性（ACID）原子性、一致性、隔离性、持久性？
- 事务的并发？事务隔离级别，每个级别会引发什么问题，MySQL默认是哪个级别？
- MySQL常见的三种存储引擎（InnoDB、MyISAM、MEMORY）的区别？
- MySQL的MyISAM与InnoDB两种存储引擎在，事务、锁级别，各自的适用场景？
- 查询语句不同元素（where、jion、limit、group by、having等等）执行先后顺序
- 索引为什么要用B+树，B+树和B-树的区别是什么
- mysql的默认事务级别，一共有哪些事务级别
- mysql的一些语句，这些肯定需要掌握的
- mysql锁，行锁，表锁 ，什么时候发生锁，怎么锁，原理
- 数据库优化，最左原则啊，水平分表，垂直分表
- 什么是临时表，临时表什么时候删除?
- MySQL B+Tree索引和Hash索引的区别？
- sql查询语句确定创建哪种类型的索引？如何优化查询？
- 聚集索引和非聚集索引区别？
- 有哪些锁（乐观锁悲观锁），select 时怎么加排它锁？
- 非关系型数据库和关系型数据库区别，优势比较？
- 数据库三范式，根据某个场景设计数据表？
- 数据库的读写分离、主从复制，主从复制分析的 7 个问题？
- 使用explain优化sql和索引？
- MySQL慢查询怎么解决？
- 什么是 内连接、外连接、交叉连接、笛卡尔积等？
- mysql都有什么锁，死锁判定原理和具体场景，死锁怎么解决？
- varchar和char的使用场景？
- mysql 高并发环境解决方案？
- 数据库崩溃时事务的恢复机制（REDO日志和UNDO日志）？

## **03 Spring相关**

- spring的两大特性- ioc aop，实现原理
- 如果存在A依赖B，B依赖A，那么是怎么加到IOC中去的
- beanFactory的理解，怎么加载bean
- FactoryBean的理解
- 基于注解的形式，是怎么实现的， 你知道其原理吗，说一下
- 依赖冲突，有碰到过吗，你是怎么解决的~
- bean的生命周期
- spring中的自动装配方式
- BeanFactory 和 FactoryBean
- Spring IOC 的理解，其初始化过程？
- BeanFactory 和 ApplicationContext？
- Spring Bean 的生命周期，如何被管理的？Spring Bean 的加载过程是怎样的？
- 如果要你实现Spring AOP，请问怎么实现？
- 如果要你实现Spring IOC，你会注意哪些问题？
- Spring 是如何管理事务的，事务管理机制？
- Spring 的不同事务传播行为有哪些，干什么用的？
- Spring 中用到了那些设计模式？
- Spring MVC 的工作原理？
- Spring 循环注入的原理？
- Spring 如何保证 Controller 并发的安全？
- 你一般是怎么对mvc项目进行分层的
- dispatch-servlet的工作原理
- 为什么有了springmvc还要在项目中使用spring？
- springmvc的运行机制，dispatch -》 hanldermapping-—》handler -》handlerAdapter-》执行handler-》modelandview -》 返回mv -》 视图解析器-》返回view -》 渲染响应
- 怎么防止依赖注入
- 怎么让mapper 和xml对应
- 如何自动包装对象
- 和spring相比，做了什么改变
- starter你知道哪些
- 如何部署springmvc项目 以及如何部署springboot项目
- springboot的插件，你使用过哪些

## **04 中间件**

### **4.1 redis**

- Redis用过哪些数据数据，以及Redis底层怎么实现
- Redis缓存穿透，缓存雪崩
- 如何使用Redis来实现分布式锁
- Redis的并发竞争问题如何解决
- Redis持久化的几种方式，优缺点是什么，怎么实现的
- Redis的缓存失效策略
- Redis集群，高可用，原理
- Redis缓存分片，Redis的数据淘汰策略
- 为什么选择redis，有什么好处，基于内存，抗压
- redis集群怎么进行数据分配，hash槽
- redis的主从复制是怎么实现的
- redis的数据结构 最常问 hash是什么， sorted set怎么实现的
- 因为项目的原因，问我redis是怎么保证高可用的，主从和集群怎么加在一起
- redis 和memcache的区别
- redis 分布式锁的实现原理 setNX 啥的
- redis模拟session，除了redis你还考虑过别的吗
- redis的缓存击穿，怎么处理这个问题
- redis是基于内存的，那么它有持久化吗，aof rdb
- aof和rdb的优缺点，你在项目中使用的哪一个

### **4.2 MQ**

- 为什么选择rabbitMQ， 社区活跃，高并发
- 别的MQ也要了解，比如RocketMQ(阿里的，java开发，再次开发，并发高，分布式，出错少)
- ActiveMQ， kafka
- topic 和 block
- MQ的作用，同步转异步，消除峰值
- 如何保证数据一致性，即原子性，ack
- 消息队列在项目中的应用

### **4.3 nginx**

- 怎么配置负载均衡
- 怎么限流
- 怎么使用nginx缓存
- 为什么使用nginx，有别的替代品吗
- 请解释 x Nginx 如何处理 P HTTP 请求
- 在 x Nginx 中，如何使用未定义的服务器名称来阻止处理请求? ?
- 使用“ 反向代理服务器 ” 的优点是什么?
- x Nginx 服务器上的 r Master 和 和 r Worker 进程分别是什么?
- nginx的压力测试，你测试过吗，能抗住多少压力
- 你如何通过不同于 0 80 的端口开启 Nginx?
- 是否有可能将 x Nginx 的错误替换为 2 502 错误、 503
- s stub_status 和 和 r sub_filter 指令的作用是什么? ?

### **4.5 dubbo**

- 原理，怎么用
- 和erueka有什么区别
- 为什么要用dubbo，不用行不行？
- 跨域请求的一些知识点
- Dubbo 支持哪些协议，每种协议的应用场景，优缺点？
- Dubbo 超时时间怎样设置？
- Dubbo 集群的负载均衡有哪些策略
- Dubbo 的主要应用场景？
- Dubbo 服务注册与发现的流程？
- Dubbo 中 中 zookeeper 做注册中心，如果注册中心集群都挂掉，发布者和订阅者之间还能通信么？
- dubbo 服务负载均衡策略？

## **05 其他插件**

### **5.1 shiro**

- 怎么做权限控制
- 为什么使用shiro，你直接使用aop不也是一样的吗，shiro还有标签~各种扯
- shiro的两个最重要的函数
- 认证和授权是怎么做的

### **5.2 docker**

- 和vmware的区别
- 你一般是怎么部署的 IDEA，直接把项目部署到docker并打包到云服务器
- docker的好处，小，快

## **06 Linux**

- 常见的命令
- sed 和 awk 感觉linux必考。。
- linux的使用场景，你什么时候会用linux -- 》 布置服务器
- 怎么查看进程和杀死进程
- 打印一个文件夹中的所有文件
- float在计算机中是怎么存储的，当时被问到的时候，我也在问自己，怎么存的~~~ 佛了
- 线程和进程的区别
- 线程的通信方式，进程的通信方式
- 系统线程的数量上限是多少
- 页式存储的概念
- 内存碎片，你有了解过吗，有想过解决方案吗~

## **07 算法**

### 7.1 排序算法

- 八大排序算法真的是面试宠儿
- 最常考 快速排序 和归并排序
- 哪些排序算法是稳定的 哪些是不稳定的
- 堆排 也应该掌握

### 7.2 树

- 根据遍历结果恢复树，递归
- 二叉搜索树第k大
- 树的和为k的路径
- 层次遍历
- 根据层次遍历和后序遍历恢复树
- 镜像树
- 树的深度
- 是不是平衡二叉树

### 7.3 链表

- 反转链表
- 链表环的入口
- 交叉链表的交点
- 复杂链表的复制
- 二叉搜索树变成双向链表

### 7.4 回溯算法

- 走迷宫
- 游戏通关

### 7.5 递推算法

- 走台阶
- 断钢筋

### 7.6 背包问题

- 装最多的东西

### **7.7 贪心算法**

- 覆盖问题
- 时间问题

## **08 设计模式**

面试中设计模式其实也是挺重要的

- Java 中什么叫单例设计模式？请用 Java 写出线程安全的单例模式
- 在 Java 中，什么叫观察者设计模式（observer design pattern）
- 使用工厂模式最主要的好处是什么？在哪里使用
- 举一个用 Java 实现的装饰模式(decorator design pattern) ？它是作用于对象层次还是类层次？
- 在 Java 中，什么时候用重载，什么时候用重写？
- 举例说明什么情况下会更倾向于使用抽象类而不是接口
- 观察者模式
- 适配模式
- 工厂模式
- ...', 1593862288498, 1593862288498, 769, 1, 21, 0, '求职,java', 832);
INSERT INTO PUBLIC.QUESTION (TITLE, DESCRIPTION, GMT_CREATE, GMT_MODIFIED, CREATOR, COMMENT_COUNT, VIEW_COUNT, LIKE_COUNT, TAG, ID) VALUES ('123', '1234', 1593918472284, 1593918498558, 769, 2, 41, 0, 'javascript', 864);
INSERT INTO PUBLIC.QUESTION (TITLE, DESCRIPTION, GMT_CREATE, GMT_MODIFIED, CREATOR, COMMENT_COUNT, VIEW_COUNT, LIKE_COUNT, TAG, ID) VALUES ('Java锁机制', '![](/image_sever/4f81d3f7f5f34d6ea7fa88b1787c1ea7.png)
### ReentrantLock
![](/image_sever/34858e57f9dd4179a2d4c68765849fae.png)

+ 可重入

  可以的
  从名字上理解，ReenTrantLock的字面意思就是再进入的锁，其实synchronized关键字所使用的锁也是可重入的，两者关于这个的区别不大。两者都是同一个线程没进入一次，锁的计数器都自增1，所以要等到锁的计数器下降为0时才能释放锁。

+ 调用lockInterruptibly()方法获取锁可打断

+ 锁超时

+ 支持公平锁

+ 条件变量:等待支持分类等待，而不是像synchronized所有等待在一起

### java锁机制主要在1.6进行了优化

锁膨胀过程：无锁→偏向锁（CAS检查对象头）→轻量级锁（CAS锁）→重量级锁（synchronized）
![](/image_sever/9c6115f66b91453aab0b9685735de7ee.png)', 1601363189734, 1603511899280, 801, 1, 12, 0, 'java', 865);