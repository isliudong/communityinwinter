##西柚社区
2020寒冬哥成长计划
##资料
[文档](https://spring.io/guides)

[文档](https://v3.bootcss.com/components/#progress)

[git授权](https://developer.github.com/apps/building-oauth-apps/authorizing-oauth-apps/)

[Spring Framework Documentation](https://docs.spring.io/spring/docs/5.0.3.RELEASE/spring-framework-reference/index.html)

[Spring注入要点](https://www.cnblogs.com/tootwo2/p/6790754.html)

[Spring事务实现](https://blog.csdn.net/mawenshu316143866/article/details/81281443)

[md插件](https://pandao.github.io/editor.md/)
##工具

##技术点
[OKHttp模拟post请求](https://square.github.io/okhttp/)
h2微型数据库
jpa是一种持久层规范，hibernate实现了它
Hibernate的DAO层开发比MyBatis简单，Mybatis需要维护SQL和结果映射。
Hibernate对对象的维护和缓存要比MyBatis好，对增删改查的对象的维护要方便。
Hibernate数据库移植性很好，MyBatis的数据库移植性不好，不同的数据库需要写不同SQL。
Hibernate有更好的二级缓存机制，可以使用第三方缓存。MyBatis本身提供的缓存机制不佳
 
mybatis是另一种持久方案的实现
MyBatis可以进行更为细致的SQL优化，可以减少查询字段。
MyBatis容易掌握，而Hibernate门槛较高。

Java8重要特性使用：
Lambda表达式也可称为闭包
[Java8Stream处理：项目中多次使用String流处理来处理字符串](https://www.jianshu.com/p/11c925cdba50)
    
数据库连接池使用spring默认的HikariCP连接池

flyway migration可以简化多人数据库操作，提高数据库维护能力给后

maven启动mybatis generator 指令：mvn -Dmybatis.generator.overwrite=true mybatis-generator:generate

调用枚举类的枚举值，即调用枚举值的构造函数

@RequestParam定义接受参数，参数过多可以用@RequestBody封装

localstorage网站本地持久存储数据

##快捷方式
显示文件所在位置

最近文件:ctrl+e

文件搜索：ctrl+shift+n

编辑最大化：ctrl+shift+f12

非截断换行：shift+enter

浏览器匿名无个人信息：ctrl+shift+n

快速匿名转显,抽取变量：ctrl+alt+v

快速抽取方法：ctrl+alt+m

抽取成方法参数：ctrl+alt+p

参数查看：ctrl+p

移除无用依赖ctrl+alt+o

重命名shift+f6

节点扩选ctrl+w

格式化 ctrl+alt+l

当前网页搜索ctrl+f

idea参数配置Shift+Ctrl+Alt+/
[其他详见：](https://blog.csdn.net/zhuwinmin/article/details/72841061)
##问题
[OKHttp异常java.lang.IllegalStateException: closed](https://blog.csdn.net/u012587005/article/details/78504925)
500报错：服务器异常
Whitelabel Error Page
This application has no explicit mapping for /error, so you are seeing this as a fallback.
Thu Jan 16 16:09:13 CST 2020
There was an unexpected error (type=Internal Server Error, status=500).
nested exception is org.apache.ibatis.exceptions.PersistenceException: ### Error updating database. Cause: java.lang.RuntimeException: Driver org.h2.Driver claims to not accept jdbcUrl, jdbc:mysql://localhost/test ### The error may exist in life/liudong/community/mapper/UserMapper.java (best guess) ### The error may involve life.liudong.community.mapper.UserMapper.insert ### The error occurred while executing an update ### Cause: java.lang.RuntimeException: Driver org.h2.Driver claims to not accept jdbcUrl, jdbc:mysql://localhost/test

h2创建用户create user sa { password '123' } admin

@RequestParam可以获取url参数

[使用@Autowired注解警告Field injection is not recommended](https://blog.csdn.net/zhangjingao/article/details/81094529)

前端input，用value、text用text

mybatis _转换驼峰规则(配置mybatis.configuration.map-underscore-to-camel-case=true即可)