# #MyBatis 执行流程学习分享

## 1、JDBC

### JDBC操作数据库

JDBC是用于Java编程语言和数据库之间的数据库无关连接的标准Java API。换句话说，使用JAVA语言连接数据库进行操作，就需要使用JDBC API。统一的JDBC API接口，屏蔽了底层数据库的细节，可以使用一致性的编码（跨数据库）对数据库进行操作。

JDBC操作数据库大致三个步骤:

1. 连接数据库

2. 执行SQL

3. 处理返回结果

   ![JDBC一般流程](.\image\jdbc.png)

   JDBC示例

```
ConnectionImpl conn = null;
PreparedStatement stmt = null;
ResultSetImpl rs = null;
try {
    // 注册驱动程序
    Class.forName("com.mysql.jdbc.Driver");
    // 获取链接
    conn = (ConnectionImpl) DriverManager.getConnection("jdbc:mysql://10.1.32.29:3306/gopher_auth", "gopher_auth", "gopher_auth");
    conn.setAutoCommit(false);
    // 获取 Statement
    stmt = conn.prepareStatement("insert into tbl_sys_role (id, role_name, role_code, system_code) values (?, ?, ?, ?)");
    // 设置参数
    stmt.setString(1, UUID.randomUUID().toString().replace("-", ""));
    stmt.setString(2, "管理员");
    stmt.setString(3, "admin");
    stmt.setString(4, "gims");
    // 执行SQL
    stmt.executeUpdate();
    // 提交事务
    conn.commit();
	// 查询
    stmt = conn.prepareStatement("select * from tbl_sys_role where id = ?");
    stmt.setString(1, "41a68e9895b0437b9d35792c85565a96");
    rs = (ResultSetImpl) stmt.executeQuery();
	// 结果集
    while (rs.next()) {
        System.out.println("id: " + rs.getString("id") + ": roleName: " + rs.getString("role_name"));
    }
} catch (Exception e) {
    e.printStackTrace();
} finally {
    // 清理资源、异常处理
    if (rs != null) {
        try { rs.close(); } 
        catch (SQLException e) { e.printStackTrace(); }
    }
    if (stmt != null) {
        try { stmt.close(); } 
        catch (SQLException e) { e.printStackTrace(); }
    }
    if (conn != null) {
        try { conn.close(); } 
        catch (SQLException e) { e.printStackTrace(); }
    }
}
```

### JDBC存在的问题

**代码冗余**

借助于JDBC编程，有很多模块化的代码在第一个JDBC示例中，所有的步骤都是需要按部就班完成的。而这些步骤很显然，有些是结构化的模式化的，比如连接数据库，关闭连接，异常处理，但是却不得不处理。JDBC功能足够，但是便捷性欠缺，不够灵活。

**对象映射**

Java作为面向对象编程语言，一切皆是对象，但常用的数据库却是关系型数据库，关系模型就像一张二维表格，一个关系型数据库就是由二维表及其之间的联系组成的一个数据组织，这并不是对象型的，JDBC的操作方式是也不是面向对象的。

在对象与关系型数据库的字段之间，缺少用于将字段与对象进行映射对照，只能由程序员借助于JDBC自己手动的将字段组装成对象，JDBC对象的映射全靠自己。

## 2、ORM框架

ORM：对象关系映射（Object Relational Mapping）

JDBC将应用程序开发者与底层数据库驱动程序进行解耦，作为中间层承上启下，而ORM是插入在应用程序与JDBCAPI之间的一个中间层，JDBC并不能很好地支持面向对象的程序设计，ORM解决了这个问题。

ORM工具就是JDBC的封装，用于完成Java对象与关系型数据库的映射，简化了JDBC的使用。

![](.\image\orm.png)

ORM工具框架最大的核心就是封装了JDBC的交互，不再需要处理结果集中的字段或者行或者列，借助于ORM可以快速进行开发，而无需关注JDBC交互细节。

**常见的ORM框架** ：mybatis（ibatis）Hibernate 等



## 3、MyBatis

### 3.1 什么是 MyBatis

MyBatis 是一款优秀的持久层框架，它支持自定义 SQL、存储过程以及高级映射。MyBatis 免除了几乎所有的 JDBC 代码以及设置参数和获取结果集的工作。MyBatis 可以通过简单的 XML 或注解来配置和映射原始类型、接口和 Java POJO（Plain Old Java Objects，普通老式 Java 对象）为数据库中的记录。

[参考文档]: https://mybatis.net.cn/	"MyBatis中文网"

与其他比较标准的 ORM 框架（比如 Hibernate ）不同， mybatis 并没有将 [java](https://www.w3cschool.cn/java/) 对象与数据库关联起来，而是将 [java](https://www.w3cschool.cn/java/) 方法与 [sql](https://www.w3cschool.cn/sql/) 语句关联起来，自己写 [sql](https://www.w3cschool.cn/sql/) 语句的好处是，可以根据自己的需求，写出最优的 [sql](https://www.w3cschool.cn/sql/) 语句。灵活性高。但是，由于是自己写 [sql](https://www.w3cschool.cn/sql/) 语句，导致平台可移植性不高。

**MyBatis操作数据库**

原生MyBatis一般流程

1. 加载配置文件到 Configuration
2. 构建 SQlSessionFactory
3. 获取 SqlSession，创建执行器 Executor
4. 获取 Mapper 对象(动态代理对象)
5. 执行 SQL

```
// 加载 mybatis 全局配置文件
InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
// 创建 SqlSessionFactory 对象
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
// 根据 sqlSessionFactory 获取 session
SqlSession sqlSession = sqlSessionFactory.openSession();
// 获取 mapper 对象，此处返回的是 MapperProxy 动态代理对象
RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
// 执行SQL
TblSysRole role = roleMapper.getRole("111");
System.out.println(role.toString());
```

**全局配置文件**

```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration> <!--配置-->
    <properties/> <!--属性-->
    <settings/> <!--全局配置参数-->
    <typeAliases/> <!--类型别名-->
    <typeHandlers/> <!--类型处理器-->
    <objectFactory/><!--对象工厂-->
    <plugins/><!--创建-->
    <environments default=""><!--环境配置-->
        <environment id=""><!--环境变量-->
            <transactionManager type=""/><!--事务管理器-->
            <dataSource type=""/><!--数据源-->
        </environment>
    </environments>
    <databaseIdProvider type=""/><!--数据库厂商标识-->
    <mappers/><!--映射器-->
</configuration>
```

### 3.2 Mapper接口

使用 mapper 接口的方式，不用写接口实现类，直接完成数据库操作，简单方便。使用 mapper 接口，采用的是面向接口编程的思想。sqlSession.getMapper 方法返回的是代理类，Mybatis是通过JDK代理实现的 mapper 接口代理。

调用session.getMapper方法之后的运行时序图：

![](.\image\Mapper代理.png)

1. 在调用sqlSession.getMapper之后，会去Configuration对象中获取Mapper对象，（在项目启动的时候就会把Mapper接口加载并解析存储到Configuration对象）
2. 通过Configuration对象中的MapperRegistry对象属性，继续调用getMapper方法
3. 根据type类型，从MapperRegistry对象中的knownMappers获取到当前类型对应的代理工厂类，然后通过代理工厂类生成对应Mapper的代理类
4. 最终获取到我们接口对应的代理类MapperProxy对象

**Mapper代理源码**

![image-20221114130230325](.\image\Mapper代理2.png)

#### MapperProxy

RoleMapper接口代理类MapperProxy对象，而MapperProxy可以看到实现了InvocationHandler，使用的就是JDK动态代理

![](.\image\roleMapper是代理对像.png)

![](.\image\MapperProxy.png)

#### SQL执行

获取到的Mapper接口实际上被包装成为了代理对象，所以我们执行查询语句肯定是执行的代理对象 invok 方法。

整个sql执行流程可以分为两大步骤：

- 查找sql
- 执行sql语句

调用`roleMapper.getRole`方法是，会执行`MapperProxy#invok`方法，先判断是否调用Object中通用的方法；

构造一个`MapperMethod`对象，这个对象封装了Mapper接口中对应的方法信息以及对应的sql语句信息；

执行SQL，进入`mapperMethod.execute`方法，判断SQL类型`（INSERT, UPDATE, DELETE, SELECT）`，最终执行的是**SqlSession**的`delete、update、insert、select`等方法；

![](.\image\MapperProxy Invok方法.png)

### 3.3 **SqlSession下的四大对象**

#### 3.3.1 SqlSession 执行过程

![](.\image\Mybatis执行过程.png)

SqlSession的执行过程是通过**Executor**、**StatementHandler**、**ParameterHandler**和**ResultSetHandler**来完成数据库操作和结果返回的

- Executor：代表执行器，由它调度StatementHandler、ParameterHandler、ResultSetHandler等来执行对应的SQL，其中StatementHandler是最重要的。
- StatementHandler：作用是使用数据库的Statement（PreparedStatement）执行操作，它是四大对象的核心，起到承上启下的作用，许多重要的插件都是通过拦截它来实现的。
- ParameterHandler：是用来处理SQL参数的。
- ResultSetHandler：是进行数据集（ResultSet）的封装返回处理的，它非常的复杂，好在不常用。

#### 3.3.1 Executor 执行器接口

Executor是MyBatis执行接口，执行器的功能包括：

- 基本功能：查、改(所有的增删操作都可以归结到改）。
- 缓存维护：这里的缓存主要是为一级缓存服务，功能包括创建缓存Key、清理缓存、判断缓存是否存在。
- 事物管理：提交、回滚、关闭、批处理刷新。

##### **Executor接口**

![](.\image\Executor接口结构图.png)

- BaseExecutor：是一个抽象类，采用模板方法的设计模式。它实现了Executor接口，实现了执行器的基本功能。
- SimpleExecutor：最简单的执行器，根据对应的SQL直接执行即可，不会做一些额外的操作；拼接完SQL之后，直接交给 StatementHandler 去执行。
- BatchExecutor：批处理执行器，用于将多个SQL一次性输出到数据库，通过批量操作来优化性能。通常需要注意的是批量更新操作，由于内部有缓存的实现，使用完成后记得调用flushStatements来清除缓存。
- ReuseExecutor ：可重用的执行器，重用的对象是Statement，也就是说该执行器会缓存同一个sql的Statement，省去Statement的重新创建，优化性能。内部的实现是通过一个HashMap来维护Statement对象的。由于当前Map只在该session中有效，所以使用完成后记得调用flushStatements来清除Map。调用实现的四个抽象方法时会调用 prepareStatement()
- CachingExecutor：启用于二级缓存时的执行器；采用装饰器模式；代理一个 Executor 对象。执行 update 方法前判断是否清空二级缓存；执行 query 方法前先在二级缓存中查询，命中失败再通过被代理类查询

##### **创建Executor**

Executor对象会在MyBatis加载全局配置文件时初始化，它会根据配置的类型去确定需要创建哪一种Executor，我们可以在全局配置文件settings元素中配置Executor类型，setting属性中有个defaultExecutorType，可以配置如下3个参数SIMPLE、REUSE、BATCH，分别对应三种 Executor执行器，默认使用SimpleExecutor。而如果开启了二级缓存，则用CachingExecutor进行包装。

![Executor创建过程](.\image\Executor创建过程.png)

![](E:\idea\mybatis\help\image\Executor创建过程2.png)

##### **Executor执行流程**

以 roleMapper.getRole("111") 为例：

![](.\image\Executor执行流程.png)

- SqlSession会调用CachingExecutor执行器的query()方法，先从二级缓存获取数据，当无法从二级缓存获取数据时，则委托给BaseExecutor的子类进行操作
- 如果没有使用二级缓存并且没有配置其它的执行器，那么MyBatis默认使用SimpleExecutor，调用父类BaseExecutor的query()方法
- BaseExecutor先查询一级缓存，如过缓存没有数据，则调用queryFromDatabase()从数据库中获取数据，在queryFromDatabase()方法中调用 doQuery() 实现类的方法（SimpleExecutor#doQuery）
- SimpleExecutor#doQuery会根据Configuration对象来构建**StatementHandler**，然后使用prepareStatement()方法对SQL编译和参数进行初始化，最后使用 **StatementHandler**的query()方法，把ResultHandler传递进去，执行查询后再通过ResultSetHandler封装结果并将结果返回

#### 3.3.2 StatementHandler

Executor 会把后续的工作交给 `StatementHandler` 继续执行

StatementHandler 是数据库会话管理器，相当于JDBC中的Statement(PreparedStatement)，负责管理 Statement 对象与数据库进行交互。

##### **StatementHandler接口**

![](.\image\StatementHandler接口结构图.png)

`StatementHandler` 有两个实现类 `BaseStatementHandler` 和 `RoutingStatementHandler` ，`RoutingStatementHandler` 采用装饰器模式，根据上下文来选择适配器生成相应的 `StatementHandler`。`BaseStatementHandler` 有三个实现类：`SimpleStatementHandler`、`PreparedStatementHandler`和`CallableStatementHandler`。

- `RoutingStatementHandler`：`RoutingStatementHandler`的功能只是根据 StatementType 来创建一个代理，代理的就是对应Handler的三种实现类。
- `BaseStatementHandler`：是一个抽象类，它实现了`StatementHandler`接口，用于简化`StatementHandler`接口实现的难度，采用适配器设计模式，它主要有如下三个实现类
- `SimpleStatementHandler`： 最简单的`StatementHandler`，处理不带参数运行的SQL，对应JDBC的`Statement`
- `PreparedStatementHandler`：预处理`Statement`，处理带参数运行的SQL， 对应JDBC的`PreparedStatement`
- `CallableStatementHandler`：存储过程的`Statement`，处理存储过程SQL，对应JDBC的 `CallableStatement`

##### 创建StatementHandler

上述`Executor`执行到`SimpleExecutor#doQuery`方法，`StatementHandler`的初始化过程如下（它也是在`Configuration`对象中完成的）：

![](.\image\StatementHandler创建过程.png)

![](.\image\StatementHandler创建过程2.png)

MyBatis 会根据 SQL 语句的类型进行对应`StatementHandler`的创建，以预处理`PreparedStatementHandler`为例：

![](.\image\StatementHandler创建过程3.png)

创建`StatementHandler`的同时，也创建了`parameterHandler、resultSetHandler`

##### StatementHandler执行流程

以 roleMapper.getRole("111") 为例，接上图：

![](.\image\StatementHandler执行流程.png)



#### 3.3.3 ParameterHandler对象

`ParameterHandler`是参数处理器，它的作用是完成对预编译的参数的设置（`PreparedStatementSQL`语句参数动态赋值），`ParameterHandler`接口有两个方法：

```
public interface ParameterHandler {
  Object getParameterObject(); // 用于获取参数对象
  void setParameters(PreparedStatement ps) throws SQLException; // 用于设置预编译SQL的参数
}
```

##### ParameterHandler对象的创建

`ParameterHandler`参数处理器对象是在创建`StatementHandler`对象的同时被创建的，同样也是由`Configuration`对象负责创建，`ParameterHandler`只有一个实现类 `DefaultParameterHandler`。

![](.\image\ParameterHandler创建.png)

 可以发现在创建`ParameterHandler`对象时，传入了三个参数`mappedStatement、parameterObject、boundSql`。

- `mappedStatement`保存了一个映射器节点`<select|update|delete|insert>`中的内容，包括配置的`sql、sql Id、parameterType、resultType、resultMap`等配置内容
- `parameterObject`入参
- `boundSql`表示要实际执行的sql语句，它是通过`SqlSource`对象生成，根据传入的参数对象，`SqlSource`常用的实现类是 `DynamicSqlSource`

##### ParameterHandler解析sql

![](.\image\ParameterHandler解析入参.png)

##### parameterObject入参对象

parameterObject在生成parameterHandler对象时传入；在SQL执行过程中，执行`SqlSession`的`delete、update、insert、select`方法前，convertArgsToSqlCommandParam会预先处理roleMapper.getRole("111")入参。如果只有一个入参，直接返回，如果有多个入参，转为map类型。

![](.\image\convertArgsToSqlCommandParam处理入参.png)

![](.\image\convertArgsToSqlCommandParam处理入参2.png)

##### 参数设置过程

略 ，参见源码`org.apache.ibatis.scripting.defaults.DefaultParameterHandler#setParameters`

#### 3.3.4 ResultSetHandler

`ResultSetHandler`是结果处理器，它是用来组装结果集的。`ResultSetHandler`是一个接口，它只有一个默认的实现类，像是`ParameterHandler` 一样，它的默认实现类是`DefaultResultSetHandler`

```java
public interface ResultSetHandler {
  // 处理结果集
  <E> List<E> handleResultSets(Statement stmt) throws SQLException;
  // 处理批量游标结果集
  void handleOutputParameters(CallableStatement cs) throws SQLException;
  // 处理存储过程结果集
  void handleOutputParameters(CallableStatement cs) throws SQLException;
}
```

##### ResultSetHandler对象的创建

`ResultSetHandler`结果集处理器对象是在创建`StatementHandler`对象的同时被创建的，同样也是由`Configuration`对象负责创建。

![](.\image\ResultSetHandler创建.png)

##### ResultSetHandler 解析结果集

接`StatementHandler执行流程，SimpleExecutor#doQuery方法最终返回(StatementHandler)handler.query(stmt)，query方法返回resultSetHandler.<E> handleResultSets(ps)`

![](.\image\ResultSetHandler执行流程.png)

![](.\image\ResultSetHandler处理结果集.png)

##### 结果集处理过程

略，参见源码`org.apache.ibatis.executor.resultset.DefaultResultSetHandler#handleResultSets`

## 4、插件、拦截器

`Executor`的创建过程中，`newExecutor`方法在创建好`Executor`实例后，紧接着通过拦截器链`interceptorChain` 为 `Executor`实例植入代理逻辑

```java
public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
    ...
    executor = (Executor) interceptorChain.pluginAll(executor);
    return executor;
}

// 拦截器链
public class InterceptorChain {
    // 内部就是一个拦截器的List
    private final List<Interceptor> interceptors = new ArrayList<Interceptor>();
    public Object pluginAll(Object target) {
        // 循环调用每个Interceptor.plugin方法
        for (Interceptor interceptor : interceptors) {
	       // plugin 方法是由具体的插件类实现
            target = interceptor.plugin(target);
        }
        return target;
    }
    /** 添加插件实例到 interceptors 集合中 */
    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }
    /** 获取插件列表 */
    public List<Interceptor> getInterceptors() {
        return Collections.unmodifiableList(interceptors);
    }
}
```

plugin方法是由具体的插件类实现，该方法实现代码一般比较固定

```java
// ExecutorInterceptor类
public Object plugin(Object target) {
    return Plugin.wrap(target, this);
}
```

### 4.1 InterceptorChain拦截器链

InterceptorChain中interceptors变量是全局配置文件中 <plugins></plugins>标签plugin集合，上面的for循环代表了只要是插件，都会以**责任链**的方式逐一执行，所谓插件，其实就类似于拦截器。pluginAll 方法会调用具体插件的 plugin 方法植入相应的插件逻辑。如果有多个插件，则会多次调用 plugin 方法（plugin 方法是由具体的插件类实现），最终生成一个层层嵌套的代理类，当 Executor 的某个方法被调用的时候，插件逻辑会先行执行，执行顺序由外而内`plugin3 → plugin2 → Plugin1 → Executor`

![](.\image\Plugin层次嵌套.png)

### 4.2 Plugin类

Plugin类的作用是根据插件类@Intercepts注解配置，生成目标组件的代理对象，并根据注解配置判断是否需要拦截目标方法：

**wrap方法生成代理类**

```java
// Plugin 类
public static Object wrap(Object target, Interceptor interceptor) {
	// 取得签名Map, 就是获取Interceptor实现类上面的注解，要拦截的是哪个类（Executor，ParameterHandler，ResultSetHandler，StatementHandler）的哪个方法
	Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);
	// 取得要改变行为的类(ParameterHandler|ResultSetHandler|StatementHandler|Executor)
	Class<?> type = target.getClass();
	// 取目标类的接口
	Class<?>[] interfaces = getAllInterfaces(type, signatureMap);
	// 产生代理
	if (interfaces.length > 0) {
		return Proxy.newProxyInstance(
				type.getClassLoader(),
				interfaces,
				new Plugin(target, interceptor, signatureMap));
	}
	return target;
}
```

**@Intercepts注解**

```java
// @Intercepts注解用来定义插件类型，要拦截的方法已经方法的参数(根据参数类型获取目标方法)
@Intercepts({@Signature( // 要拦截的类型
        type = Executor.class,	// Executor 类型的插件
        method = "update",	// 拦截update方法
        args = {MappedStatement.class, Object.class} // update方法的参数
), @Signature(
        type = Executor.class,
        method = "query",	// 拦截update方法
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class} // 四个参数的update方法
), @Signature(
        type = Executor.class,
        method = "query",  // 拦截update方法
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}  // 六个参数的update方法
)})
```

**invoke执行插件逻辑**

```java
@Override
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
        // 获取被拦截方法列表，比如： signatureMap.get(Executor.class)，可能返回 [query, update, commit]
        Set<Method> methods = signatureMap.get(method.getDeclaringClass());
        // 检测方法列表是否包含被拦截的方法
        if (methods != null && methods.contains(method)) {
            // 调用Interceptor.intercept，执行插件逻辑
            return interceptor.intercept(new Invocation(target, method, args));
        }
        // 未被拦截的方法，执行原逻辑
        return method.invoke(target, args);
    } catch (Exception e) {
        throw ExceptionUtil.unwrapThrowable(e);
    }
}
```

### 4.3 动态代理

`MyBatis`插件是基于JDK动态代理实现的。`Plugin`实现了`InvocationHandler`接口，拦截类型为`Executor，ParameterHandler，ResultSetHandler，StatementHandler`四个中的一个，

```java
// 生成代理对象，Plugin实现了InvocationHandler接口
return Proxy.newProxyInstance(
        type.getClassLoader(),
        interfaces,
        new Plugin(target, interceptor, signatureMap));
```

### 4.4 插件类型

Mybatis中只是针对四个组件提供了扩展机制，`Executor，ParameterHandler，ResultSetHandler，StatementHandler`

```java
// Executor类型
public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
    // ....
    executor = (Executor) interceptorChain.pluginAll(executor);
    return executor;
}

// StatementHandler类型
public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
    // 创建路由选择语句处理器
    StatementHandler statementHandler = new RoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
    // 插件在这里插入
    statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
    return statementHandler;
}

// ParameterHandler类型
public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
    // 创建ParameterHandler
    ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameterObject, boundSql);
    // 插件在这里插入
    parameterHandler = (ParameterHandler) interceptorChain.pluginAll(parameterHandler);
    return parameterHandler;
}

// ResultSetHandler类型
public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql) {
    // 创建DefaultResultSetHandler
    ResultSetHandler resultSetHandler = new DefaultResultSetHandler(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
    // 插件在这里插入
    resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
    return resultSetHandler;
}
```

### 4.5 自定义插件

**使用步骤**

1. 创建一个类，实现拦截器接口`Interceptor`
2. 类上配置`@Intercepts`注解，指定对哪个对象，哪个方法进行扩展
3. 在`Mybatis`配置文件中，配置`plugins`标签，加入自定义的插件

**应用**

1. com.noahgroup.framework.common.mybatis.SqlLogInterceptor，我们公司用的答应sql日志插件
2. Mybatis-PageHelper 开源分页插件

插件参考文档 

[Mybatis插件原理]: https://zhuanlan.zhihu.com/p/163863114

## 5、Spring + mybatis

Mybatis的目的是：**使得程序员能够以调用方法的方式执行某个指定的sql，将执行sql的底层逻辑进行了封装**

```java
// 使用mybatis，以下才是我们关注的重点，当调用SqlSession的getMapper方法时，会对传入的接口生成一个代理对象，而程序要真正用到的就是这个代理对象，在调用代理对象的方法时，Mybatis会取出该方法所对应的sql语句，然后利用JDBC去执行sql语句，最终得到结果
RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
TblSysRole role = roleMapper.getRole("111");
```

Spring整合Mybatis时，重点要关注的就是这个代理对象。因为整合的目的就是：**把某个Mapper的代理对象作为一个bean放入Spring容器中，使得能够像使用一个普通bean一样去使用这个代理对象，比如能被@Autowire自动注入**

```java
@Autowired
private RoleMapper roleMapper;
TblSysRole role = roleMapper.getRole("111");
```

1. org.mybatis.spring.SqlSessionFactoryBean：实现了FactoryBean接口，用于生成其SqlSessionFactory
2. org.mybatis.spring.mapper.MapperFactoryBean<T>：实现了 FactoryBean 接口，(RoleMapper) context.getBean("mapperFactoryBean") 会调用 MapperFactoryBean 的 getObject() 方法，getObject()返回 getSqlSession().getMapper(this.mapperInterface)，拿到具体的 Mapper
3. MybatisAutoConfiguration：springboot mybatis自动配置类

## 6、参考文档

mybatis-3.3.0源码阅读：

[JDBC简介]: https://www.cnblogs.com/noteless/category/1382609.html
[MyBatis中文网]: https://mybatis.net.cn/
[MyBatis SQL是如何执行的]: https://blog.csdn.net/t4i2b10X4c22nF6A/article/details/104765156
[SqlSession下的四大对象]: http://t.zoukankan.com/tanghaorong-p-14094521.html
[Mybatis插件原理]: https://zhuanlan.zhihu.com/p/163863114
[Spring整合Mybatis原理]: https://www.cnblogs.com/raitorei/articles/12880617.html
[MyBatis源码阅读网]: http://coderead.cn/p/mybatis/doc/index.md

