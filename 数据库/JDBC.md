### JDBC的四种驱动类型
* JDBC-ODBC桥--这种驱动是最开始实现的JDBC驱动程序，主要目的是为了快速推广JDBC，将JDBC API映射到ODBC API中
* 本地API驱动-- 将JDBC的调用给转换为数据库的标准调用再去访问数据库
* 网络协议驱动-- 支持三层结构的JDBC的访问方式[ 网络协议驱动----中间件服务器----数据库Server]
* 本地协议驱动-- 直接把JDBC调用转换为符合相关数据库系统规范的请求，直接和数据库实例进行交互是目前最流行的JDBC驱动

### JDBC的作用
* 与数据库建立连接
* 向数据库发送操作数据的请求，
* 返回数据库处理结果

### JDBC的六大编程步骤
* 创建驱动
* 获取数据库连接
* 创建语句块对象
* 处理结果集
* 关闭资源

### JDBC常用接口和类
```
>>>DriverManager：用于管理JDBC驱动的服务类，程序中使用该类的主要功能时获取Connection对象
	包含的方法：public static Connection getConnection(String url,String user, String password) throws SQLException
    
>>>Connection：代表数据库连接对象，每个Connection代表一个物理连接会话，要想连接数据库，必须先获得数据库连接
	包含的常用方法：
    Statement createStatement() throws SQLException;   返回一个新的Statement对象
	PreparedStatement prepareStatement(String sql) throws SQLException; 返回预编译的Statement对象，即将SQL语句提交到数据库进行预编译
    CallableStatement prepareCall(String sql) throws SQLException; 返回CallableStatement对象，该对象用于调用存储过程
    //PreparedStatement、CallableStatement是Statement的子类，只有获得了Statement才可执行SQL语句

>>>Statement：用于执行SQL语句的工具接口，该对象既可用于执行DDL,DCL语句，也可以用于执行DML语句，还可以用于执行SQL查询，当执行SQL查询时，
		返回查询到的结果集，
    常用方法：
    	ResultSet executeQuery(String sql) throws SQLException; 返回查询结果对应的ResultSet对象，只能用于执行查询语句
        int executeUpdate(String sql) throws SQLException; 返回受影响的行数，执行DDL语句返回0
        boolean execute(String sql) throws SQLException;如果执行后第一个结果为ResultSet对象，则返回true，
        												如果执行后第一个结果为受影响的行数或没有任何结果，则返回false
                                                        
>>>PreparedStatement：预编译的Statement对象，PreparedStatement是Statement的子接口，允许数据库预编译SQL语句，以后每次只改变SQL命令的
					参数，避免数据库每次都需要编译SQL语句，因此性能更好，相对于Statement而言，使用PreparedStatement执行SQL语句时，无需
                    再传入SQL语句，只要为预编译的SQL语句传入参数值即可
                    
>>>ResultSet：结果集对象，该对象包含访问查询结果的方法，ResultSet可以通过列索引或列名获得列数据
		常用方法：
        boolean next() throws SQLException; 如果下一条记录有效，返回true，否则返回false
        void close() throws SQLException; 释放ResultSet对象
        String getString(int columnIndex) throws SQLException;返回某列的所有记录
        int getInt(int columnIndex) throws SQLException;返回某列的记录，如果记录为null，返回0
```

### JDBC的statement和preparedment、callableStatement的区别
* Statement用于通用查询，PreparedStatement用于参数查询，CallableStatement用于存储过程
* Statement继承自Wrapper，PreparedStatement继承自Statement，CallableStatement继承自PreparedStatement；Statement接口提供了执行语句和获取结果的基本方法；

### JDBC的statement和PreparedStatement的区别
* PreparedStatement会提前将预编译好的SQL语句发送的数据库的服务端进行编译，Statement对象是每次执行st.executeUpdate(sql),都会将sql语句发送数据库端进行编译.
* Statement会造成SQL的注入，而PreparedStatement不会造成这个问题

### excuted 、 excutedUpdate、 excutedQuery区别
* excuteQuery产生单个结果集的语句
* executeUpdate返回值是一个整数，指示受影响的行数，
* execute返回多个结果集

### ResultSet的本质是什么，类型是什么？
* 本质上是一个游标
* ResultSet.TYPE_FORWARD_ONLY  光标只能向前移动。
* ResultSet.TYPE_SCROLL_INSENSITIVE  光标可以向前和向后滚动。 ResultSet对在创建ResultSet后对数据库所做的更改不敏感。
* ResultSet.TYPE_SCROLL_SENSITIVE   光标可以向前和向后滚动。并且ResultSet对在创建结果集后对数据库所做的更改很敏感。
* ResultSet.CONCUR_READ_ONLY    只读结果集。 这是默认值
* ResultSet.CONCUR_UPDATABLE    可更新结果集。


### 什么是JDBC的批处理，好处是什么？
* 批处理具体指的是一次性执行多条SQL语句，允许多条语句一次性提交给数据库批量处理。
* 使用批处理比单个执行SQL语句效率要高

### 实现JDBC批处理的方式
* 使用Statement完成批处理
    * 使用Statement的addBatch添加要批量处理的SQL语句
    * 使用Statement的executeBatch来执行批处理语句
    * 使用Statement的clearBatch来请除批处理命名
* 使用PreparedStatement实现批处理
    * 使用PreparedStatement的addBatch来实现批处理。
    * 使用PreparedStatement的executeBatch来执行批处理命名
    * 使用PreparedStatement的clearBatch清除批处理命令。

### 连接池的工作原理
```
第一、连接池的建立。一般在系统初始化时，连接池会根据系统配置建立，并在池中创建了几个连接对象，以便使用时能从连接池中获取。
连接池中的连接不能随意创建和关闭，这样避免了连接随意建立和关闭造成的系统开销。Java中提供了很多容器类可以方便的构建连接池，例如Vector、Stack等。
第二、连接池的管理。连接池管理策略是连接池机制的核心，连接池内连接的分配和释放对系统的性能有很大的影响。其管理策略是：
当客户请求数据库连接时，首先查看连接池中是否有空闲连接，如果存在空闲连接，则将连接分配给客户使用；如果没有空闲连接，
则查看当前所开的连接数是否已经达到最大连接数，如果没达到就重新创建一个连接给请求的客户；如果达到就按设定的最大等待时间进行等待，
如果超出最大等待时间，则抛出异常给客户。 当客户释放数据库连接时，先判断该连接的引用次数是否超过了规定值，
如果超过就从连接池中删除该连接，否则保留为其他客户服务。该策略保证了数据库连接的有效复用，避免频繁的建立、
释放连接所带来的系统资源开销。
第三、连接池的关闭。当应用程序退出时，关闭连接池中所有的连接，释放连接池相关的资源，该过程正好与创建相反。
```

### JDBC是怎么处理分页的

### JDBC是怎么处理事务的
* Connection提供了事务处理的方法，通过调用setAutoCommit(false)可以设置手动提交事务;当事务完成 后用 commit()显式提交事务;如果在事务处理过程中发生异常则通过 rollback() 进行事务回滚。

