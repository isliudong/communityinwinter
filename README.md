##社区

##资料
[文档](https://spring.io/guides)

[文档](https://v3.bootcss.com/components/#progress)

[git授权](https://developer.github.com/apps/building-oauth-apps/authorizing-oauth-apps/)
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
##快捷方式
显示文件所在位置
文件搜索：ctrl+shift+n

编辑最大化：ctrl+shift+f12

非截断换行：shift+enter

浏览器匿名无个人信息：ctrl+shift+n

快速匿名转显,抽取变量：ctrl+alt+v

重载参数查看：ctrl+p

移除无用依赖ctrl+alt+o

重命名shift+f6

##问题
[OKHttp异常java.lang.IllegalStateException: closed](https://blog.csdn.net/u012587005/article/details/78504925)
500报错：服务器异常
Whitelabel Error Page
This application has no explicit mapping for /error, so you are seeing this as a fallback.
Thu Jan 16 16:09:13 CST 2020
There was an unexpected error (type=Internal Server Error, status=500).
nested exception is org.apache.ibatis.exceptions.PersistenceException: ### Error updating database. Cause: java.lang.RuntimeException: Driver org.h2.Driver claims to not accept jdbcUrl, jdbc:mysql://localhost/test ### The error may exist in life/liudong/community/mapper/UserMapper.java (best guess) ### The error may involve life.liudong.community.mapper.UserMapper.insert ### The error occurred while executing an update ### Cause: java.lang.RuntimeException: Driver org.h2.Driver claims to not accept jdbcUrl, jdbc:mysql://localhost/test

h2创建用户create user sa { password '123' } admin