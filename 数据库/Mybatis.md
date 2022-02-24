### 什么是ORM?
* ORM(Object Relational Mapper)对象关系映射是将面向对象中类的属性和数据库表中的字段相互对应的一种思想，ORM是通过描述对象和数据库之间映射的元数据，将程序中的对象持久化到关系型数据库中
* ORM提供了持久化层的另外一种方式，采用映射元数据来描述对象关系的映射，使得ORM中间件可以在程序的service业务逻辑层和dao数据层之间充当桥梁。

### Mybatis的优势
* Mybatis将SQL语句从Java程序中分离出来，单独放在XML中进行编写，给程序的维护带来很大遍历
* Mybatis封装了底层JDBC API的调用细节，并将结果集自动转换为JavaBean对象，大大简化了Java数据库编程的重复工作
* 由于Mybatis可以灵活控制SQL语句，因此可以实现更高的查询效率和复杂查询

### 为什么使用Mybatis而不使用JDBC？
* MyBatis是一个优秀的持久层框架，它对JDBC操作数据库的过程进行了封装，使开发者只需要关注SQL本身，而不需要花费精力去处理例如注册驱动、创建连接、创建执行语句、手动设置参数和结果集检索等繁杂的过程。
* Mybatis通过封装JDBC解决了原生jdbc在编程中出现的问题 数据库连接池。我们可以在MyBatis的全局配置文件SqlMapConfig.xml中配置数据库连接池，使用它来管理数据库连接，这样就可以避免由于频繁地创建和释放数据库连接而造成的资源浪费。
* MyBatis使用动态SQL技术解决了JDBC编程中存在的SQL硬编码的问题。
* MyBatis通过输入映射技术将Java对象映射至SQL语句，SQL语句可以通过使用该Java对象取出查询参数值，简化了JDBC中设置查询参数的过程。
* MyBatis通过输出映射技术将SQL的执行结果映射至Java对象，省去了JDBC编程中对结果集检索的过程。


### #{}和${}符号的区别
* #{}表示一个占位符号，通过#{}可以实现preparedStatement向占位符中设置值，自动进行java类型和jdbc类型转换，${}表示拼接sql串，通过${}可以将parameterType 传入的内容拼接在sql中且不进行jdbc类型转换,
* #{}括号中可以是value或其它名称。${}括号中只能是value。
* ${}会导致SQL注入,#{}可以有效防止sql注入。


### Mybatis的缓存
* 一级缓存
    * SqlSession级别的缓存，在操作数据库时需要构造SqlSession对象，在对象中有一个HashMap用于存储缓存数据，不同的SqlSession之间缓存数据区域（HashMap）是互相不影响的。如果SqlSession执行了DML操作（insert、update、delete），并执行commit（）操作，mybatis则会清空SqlSession中的一级缓存，这样做的目的是为了保证缓存数据中存储的是最新的信息，避免出现脏读现象。Mybatis默认开启一级缓存，不需要进行任何配置。
* 二级缓存
    *  mapper级别的缓存，二级缓存是多个SqlSession共享的。它同样是使用HashMap进行数据存储，相比一级缓存SqlSession，二级缓存的范围更大，多个SqlSession可以共用二级缓存，二级缓存是跨SqlSession的。mybatis通过缓存机制减轻数据库压力，提高查询性能。Mybatis默认没有开启二级缓存，需要在setting全局参数中配置开启二级缓存。
```java
<setting name="cacheEnabled" value="true"></setting>
```

### Mybatis缓存失效的情况
* 不在同一个SQLSession对象中
* 执行语句的参数不同，缓存中也不存在数据
* 执行增，删，改，语句，会清空掉缓存
* 手动清空缓存数据

### 什么是延迟加载，如何开启延迟加载，怎么做到延迟加载
* Mybatis中的延迟加载是使用动态代理完成的，代理的对象往往是多的一方。
* 当查询订单时，每个订单都会有对应的用户信息，但是目前的查询到的订单信息已经可以满足业务需要，而对于对应的用户信息，当我们需要的时候再进行查询，通过按需查询用户的订单信息所进行的操作就是延迟加载
* 在mybatis中，只要对应的对象中包含equals，hashCode,clone,toString方法时，延迟加载就会失效。
```xml
<!-- 改变默认配置,使只有调用clone方法的时候才会触发完全加载
         默认情况下,当对象调用了hashCode,equals,clone,toString()都会是延迟加载失效-->
<setting name="lazyLoadTriggerMethods" value="clone"/>
```

* 开启延迟加载：
    * 在Mybaits的全局配置文件里面的settings标签里添加配置
```xml
<setting name="lazyLoadingEnable" value="true" />
```
* 延迟加载的条件
    * resultMap可以实现高级映射（使用association、collection实现一对一及一对多映射），association、collection具备延迟加载功能。
* 延迟加载的好处
    * 先从单表进程查询，等到需要时再从关联表去关联查询，大大提高了查询性能，因为单表查询效率要比多表查询效率高的

### Mybatis的分页方法
* 使用SQL语句中的limit进行分页，可以传入两个参数，一个是当前页，还有一个是每页显示的条数
* 使用Mybatis内置的分页插件PageHelper进行分页

### PageHelper分页插件原理
* PageHelper的底层采用的是动态代理和拦截器实现分页。
* PageHelper拦截的是Executor的query方法，传参的核心原理是通过ThreadLocal进行的
* 当我们需要对某个查询进行分页的时候，可以在这个查询之前调用一次PageHelper.startPage()方法，这样PageHelper会把分页信息存储进一个ThreadLocal变量中，
* 在拦截到Executor的query方法执行时，会从对应的ThreadLocal中获取分页信息，获取到之后，则进程分页处理。然后在清空ThreadLocal中的分页信息。
* 因此当使用PageHelper.startPage()后，每次都是对最近的一次查询进行分页查询，如果下次还需要进行分页查询，需要重新进行一次PageHelper.startPage()方法，这样就可以做到对之前的查询代码没有任何的侵入性
* 此外进行分页查询时，返回的结果一般是一个List，PageHelper分页查询的结果会变成PageInfo，这个PageInfo中包含分页所需要的所有信息，包含每页显示的页数，当前页数，分页结果等。


### ResultMap和ResultType区别
* 在一对一查询中,如果查询出来的列名与某个类中的属性相对应,或者某个实体类中的属性包含查询出来的列名,则使用resultType实现较为简单
* 如果查询出来的列名和实体类名称不对应可以使用resultMap
* resultType无法实现延迟加载,但是resultMap可以实现


### Mybatis动态sql是做什么的？都有哪些动态sql？能简述一下动态sql的执行原理不？
* Mybatis动态sql可以让我们在Xml映射文件内，以标签的形式编写动态sql，完成逻辑判断和动态拼接sql的功能。

* 动态SQL标签：
where,if,set,foreach,otherwise,choose,when,trim

* 其执行原理为，使用OGNL从sql参数对象中计算表达式的值，根据表达式的值动态拼接sql，以此来完成动态sql的功能。

### MyBatis里面的动态Sql是怎么设定的?用什么语法?
* MyBatis里面的动态Sql一般是通过if节点来实现,通过OGNL语法来实现,但是如果要写的完整,必须配合where,trim节点,where节点是判断包含节点有内容就插入where,否则不插入,trim节点是用来判断如果动态语句是以and 或or开始,那么会自动把这个and或者or取掉。

### 什么是Mybatis的接口绑定(mapper代理的开发方式)，有哪些注意点，有什么好处
* 接口映射就是在MyBatis中任意定义接口,然后把接口里面的方法和SQL语句绑定,我们直接调用接口方法就可以,这样比起原来了SqlSession提供的方法我们可以有更加灵活的选择和设置.

### 接口绑定有几种实现方式,分别是怎么实现的?
* 接口绑定有两种实现方式,一种是通过注解绑定,就是在接口的方法上面加上@Select@Update等注解里面包含Sql语句来绑定,另外一种就是通过xml里面写SQL来绑定,在这种情况下,要指定xml映射文件里面的namespace必须为接口的全路径名.


### Mybatis接口中参数传递方式(三种)
* @Param注解，第三方实体类，map集合

### 查询出来的列名和实体类名称不一致怎么办
* 使用ResultMap映射，取别名


### Mybatis有几种执行器
* SimpleExecutor：每执行一次update或select，就开启一个Statement对象，用完立刻关闭Statement对象。
* ReuseExecutor：执行update或select，以sql作为key查找Statement对象，存在就使用，不存在就创建，用完后，不关闭Statement对象，而是放置于Map
* BatchExecutor：完成批处理。
