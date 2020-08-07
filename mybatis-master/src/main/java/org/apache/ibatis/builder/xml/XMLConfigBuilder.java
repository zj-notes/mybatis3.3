/*
 *    Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.builder.xml;

import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.JdbcType;

/**
 * XML配置构建器，建造者模式,继承BaseBuilder
 */
public class XMLConfigBuilder extends BaseBuilder {

    private boolean parsed;
    private XPathParser parser;
    private String environment;

    public XMLConfigBuilder(Reader reader) {
        this(reader, null, null);
    }

    public XMLConfigBuilder(Reader reader, String environment) {
        this(reader, environment, null);
    }

    // 构造函数，转换成XPathParser再去调用构造函数
    public XMLConfigBuilder(Reader reader, String environment, Properties props) {
        // 构造一个需要验证，XMLMapperEntityResolver的XPathParser
        this(new XPathParser(reader, true, props, new XMLMapperEntityResolver()), environment, props);
    }

    public XMLConfigBuilder(InputStream inputStream) {
        this(inputStream, null, null);
    }

    public XMLConfigBuilder(InputStream inputStream, String environment) {
        this(inputStream, environment, null);
    }

    public XMLConfigBuilder(InputStream inputStream, String environment, Properties props) {
        this(new XPathParser(inputStream, true, props, new XMLMapperEntityResolver()), environment, props);
    }

    // 上面6个构造函数最后都合流到这个函数，传入XPathParser
    private XMLConfigBuilder(XPathParser parser, String environment, Properties props) {
        // 首先调用父类初始化Configuration
        super(new Configuration());
        ErrorContext.instance().resource("SQL Mapper Configuration");
        // 将Properties全部设置到Configuration里面去
        this.configuration.setVariables(props);
        this.parsed = false;
        this.environment = environment;
        this.parser = parser;
    }

    // 解析配置
    public Configuration parse() {
        if (parsed)
            throw new BuilderException("Each XMLConfigBuilder can only be used once.");
        parsed = true;
        parseConfiguration(parser.evalNode("/configuration"));
        return configuration;
    }

    private void parseConfiguration(XNode root) {
        try {
            /** 1、properties */
            propertiesElement(root.evalNode("properties"));
            /** 2、类型别名 */
            typeAliasesElement(root.evalNode("typeAliases"));
            /** 3、插件 */
            pluginElement(root.evalNode("plugins"));
            /** 4、对象工厂 */
            objectFactoryElement(root.evalNode("objectFactory"));
            /** 5、对象包装工厂 */
            objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
            /** 6、设置 */
            settingsElement(root.evalNode("settings"));
            /** 7、环境 */
            environmentsElement(root.evalNode("environments"));
            /** 8、databaseIdProvider */
            databaseIdProviderElement(root.evalNode("databaseIdProvider"));
            /** 9、类型处理器 */
            typeHandlerElement(root.evalNode("typeHandlers"));
            /** 10、映射器 */
            mapperElement(root.evalNode("mappers"));
        } catch (Exception e) {
            throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
    }

    /**
     * 2、别名配置，package 和 typeAlias 不能同时配置
     * <!--<typeAliases>                                        -->
     * <!--    <!-- <package name="com.demo.po"/> -->           -->
     * <!--    <typeAlias alias="role" type="com.demo.po.Role"/>-->
     * <!--</typeAliases>                                       -->
     */
    private void typeAliasesElement(XNode parent) {
        if (parent != null) {
            for (XNode child : parent.getChildren()) {
                /** 如果子节点是 package, 那么就获取 package节点的 name 属性， mybatis会扫描指定的 package */
                if ("package".equals(child.getName())) {
                    String typeAliasPackage = child.getStringAttribute("name");
                    /** 调用TypeAliasRegistry.registerAliases，去包下找所有类,然后注册别名(有@Alias注解则用，没有则取类的simpleName) */
                    configuration.getTypeAliasRegistry().registerAliases(typeAliasPackage);
                } else {
                    /** 如果是typeAlias */
                    String alias = child.getStringAttribute("alias");
                    String type = child.getStringAttribute("type");
                    try {
                        Class<?> clazz = Resources.classForName(type);
                        /** 调用TypeAliasRegistry.registerAlias，alias 可以不填，默认使用@Alias注解，如无注解，使用类名(首字母小写的非限定类名)*/
                        if (alias == null) {
                            typeAliasRegistry.registerAlias(clazz);
                        } else {
                            typeAliasRegistry.registerAlias(alias, clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new BuilderException("Error registering typeAlias for '" + alias + "'. Cause: " + e, e);
                    }
                }
            }
        }
    }

    /**
     * plugins 节点解析
     *     <!--<plugins>-->
     *     <!--    <plugin interceptor="org.mybatis.example.ExamplePlugin">-->
     *     <!--        <property name="someProperty" value="100"/>-->
     *     <!--    </plugin>-->
     *     <!--</plugins>-->
     */
    private void pluginElement(XNode parent) throws Exception {
        if (parent != null) {
            for (XNode child : parent.getChildren()) {
                String interceptor = child.getStringAttribute("interceptor");
                Properties properties = child.getChildrenAsProperties();
                // 定义一个interceptor的时候，需要去实现Interceptor
                Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor).newInstance();
                interceptorInstance.setProperties(properties);
                // 调用 InterceptorChain.addInterceptor
                configuration.addInterceptor(interceptorInstance);
            }
        }
    }

    /**
     * 对象工厂
     *     <!--<objectFactory type="org.mybatis.example.ExampleObjectFactory">-->
     *     <!--    <property name="someProperty" value="100"/>-->
     *     <!--</objectFactory>-->
     */
    private void objectFactoryElement(XNode context) throws Exception {
        if (context != null) {
            // 读取type属性的值， 接下来进行实例化 ObjectFactory, 并set进 configuration
            String type = context.getStringAttribute("type");
            // 读取 properties 的值，根据需要可以配置(如：someProperty)，mybatis默认实现的 objectFactory 没有使用 properties
            Properties properties = context.getChildrenAsProperties();
            ObjectFactory factory = (ObjectFactory) resolveClass(type).newInstance();
            factory.setProperties(properties);
            configuration.setObjectFactory(factory);
        }
    }

    // 5.对象包装工厂
    private void objectWrapperFactoryElement(XNode context) throws Exception {
        if (context != null) {
            String type = context.getStringAttribute("type");
            ObjectWrapperFactory factory = (ObjectWrapperFactory) resolveClass(type).newInstance();
            configuration.setObjectWrapperFactory(factory);
        }
    }

    /**
     *     <!-- properties 节点-->
     *     <!-- 方法一： 从外部指定properties配置文件, 除了使用resource属性指定外，还可通过url属性指定url -->
     *     <!--    <properties resource="dbConfig.properties"></properties>-->
     *     <!-- 方法二： 直接配置为xml，两种方式xml方式优先级高 -->
     *     <!--    <properties>-->
     *     <!--        <property name="driver" value="com.mysql.cj.jdbc.Driver"/>-->
     *     <!--        <property name="url" value="jdbc:mysql://localhost:3306/gims?useUnicode=true&amp;characterEncoding=UTF-8&amp;useSSL=false&amp;serverTimezone=Asia/Shanghai&amp;allowPublicKeyRetrieval=true"/>-->
     *     <!--        <property name="username" value="root"/>-->
     *     <!--        <property name="password" value="root"/>-->
     *     <!--    </properties>-->
     */
    private void propertiesElement(XNode context) throws Exception {
        if (context != null) {
            // 将子节点的 name 以及value属性set进properties对象
            // 1、这儿可以注意一下顺序，xml配置优先， 外部指定properties配置其次
            Properties defaults = context.getChildrenAsProperties();
            // 2、然后查找resource或者url,加入前面的Properties
            String resource = context.getStringAttribute("resource");
            String url = context.getStringAttribute("url");
            // resource和url不能同时配置
            if (resource != null && url != null) {
                throw new BuilderException("The properties element cannot specify both a URL and a resource based property file reference.  Please specify one or the other.");
            }
            // 3、把解析出的properties文件set进Properties对象
            if (resource != null) {
                defaults.putAll(Resources.getResourceAsProperties(resource));
            } else if (url != null) {
                defaults.putAll(Resources.getUrlAsProperties(url));
            }

            // 3、将configuration对象中已配置的Properties属性与刚刚解析的融合
            //  configuration这个对象会装载所解析mybatis配置文件的所有节点元素，以后也会频频提到这个对象
            //  既然configuration对象用有一系列的get/set方法， 那是否就标志着我们可以使用java代码直接配置？
            //  答案是肯定的， 不过使用配置文件进行配置，优势不言而喻
            Properties vars = configuration.getVariables();
            if (vars != null) {
                defaults.putAll(vars);
            }
            // 把装有解析配置propertis对象set进解析器， 因为后面可能会用到
            parser.setVariables(defaults);
            // set进configuration对象
            configuration.setVariables(defaults);
        }
    }

    // 设置
    // 这些是极其重要的调整, 它们会修改 MyBatis 在运行时的行为方式
    // <settings>
    //  <setting name="cacheEnabled" value="true"/>
    //  <setting name="lazyLoadingEnabled" value="true"/>
    //  <setting name="multipleResultSetsEnabled" value="true"/>
    //  <setting name="useColumnLabel" value="true"/>
    //  <setting name="useGeneratedKeys" value="false"/>
    //  <setting name="enhancementEnabled" value="false"/>
    //  <setting name="defaultExecutorType" value="SIMPLE"/>
    //  <setting name="defaultStatementTimeout" value="25000"/>
    //  <setting name="safeRowBoundsEnabled" value="false"/>
    //  <setting name="mapUnderscoreToCamelCase" value="false"/>
    //  <setting name="localCacheScope" value="SESSION"/>
    //  <setting name="jdbcTypeForNull" value="OTHER"/>
    //  <setting name="lazyLoadTriggerMethods" value="equals,clone,hashCode,toString"/>
    // </settings>
    private void settingsElement(XNode context) throws Exception {
        if (context != null) {
            Properties props = context.getChildrenAsProperties();
            // 检查下是否在Configuration类里都有相应的setter方法（没有拼写错误）
            MetaClass metaConfig = MetaClass.forClass(Configuration.class);
            for (Object key : props.keySet()) {
                if (!metaConfig.hasSetter(String.valueOf(key))) {
                    throw new BuilderException("The setting " + key + " is not known.  Make sure you spelled it correctly (case sensitive).");
                }
            }

            // 下面非常简单，一个个设置属性
            // 如何自动映射列到字段/ 属性
            configuration.setAutoMappingBehavior(AutoMappingBehavior.valueOf(props.getProperty("autoMappingBehavior", "PARTIAL")));
            //缓存
            configuration.setCacheEnabled(booleanValueOf(props.getProperty("cacheEnabled"), true));
            //proxyFactory (CGLIB | JAVASSIST)
            // 延迟加载的核心技术就是用代理模式，CGLIB/JAVASSIST两者选一
            configuration.setProxyFactory((ProxyFactory) createInstance(props.getProperty("proxyFactory")));
            // 延迟加载
            configuration.setLazyLoadingEnabled(booleanValueOf(props.getProperty("lazyLoadingEnabled"), false));
            // 延迟加载时，每种属性是否还要按需加载
            configuration.setAggressiveLazyLoading(booleanValueOf(props.getProperty("aggressiveLazyLoading"), true));
            // 允不允许多种结果集从一个单独 的语句中返回
            configuration.setMultipleResultSetsEnabled(booleanValueOf(props.getProperty("multipleResultSetsEnabled"), true));
            // 使用列标签代替列名
            configuration.setUseColumnLabel(booleanValueOf(props.getProperty("useColumnLabel"), true));
            // 允许 JDBC 支持生成的键
            configuration.setUseGeneratedKeys(booleanValueOf(props.getProperty("useGeneratedKeys"), false));
            // 配置默认的执行器
            configuration.setDefaultExecutorType(ExecutorType.valueOf(props.getProperty("defaultExecutorType", "SIMPLE")));
            //超时时间
            configuration.setDefaultStatementTimeout(integerValueOf(props.getProperty("defaultStatementTimeout"), null));
            //是否将DB字段自动映射到驼峰式Java属性（A_COLUMN-->aColumn）
            configuration.setMapUnderscoreToCamelCase(booleanValueOf(props.getProperty("mapUnderscoreToCamelCase"), false));
            //嵌套语句上使用RowBounds
            configuration.setSafeRowBoundsEnabled(booleanValueOf(props.getProperty("safeRowBoundsEnabled"), false));
            //默认用session级别的缓存
            configuration.setLocalCacheScope(LocalCacheScope.valueOf(props.getProperty("localCacheScope", "SESSION")));
            //为null值设置jdbctype
            configuration.setJdbcTypeForNull(JdbcType.valueOf(props.getProperty("jdbcTypeForNull", "OTHER")));
            //Object的哪些方法将触发延迟加载
            configuration.setLazyLoadTriggerMethods(stringSetValueOf(props.getProperty("lazyLoadTriggerMethods"), "equals,clone,hashCode,toString"));
            //使用安全的ResultHandler
            configuration.setSafeResultHandlerEnabled(booleanValueOf(props.getProperty("safeResultHandlerEnabled"), true));
            //动态SQL生成语言所使用的脚本语言
            configuration.setDefaultScriptingLanguage(resolveClass(props.getProperty("defaultScriptingLanguage")));
            //当结果集中含有Null值时是否执行映射对象的setter或者Map对象的put方法。此设置对于原始类型如int,boolean等无效。
            configuration.setCallSettersOnNulls(booleanValueOf(props.getProperty("callSettersOnNulls"), false));
            //logger名字的前缀
            configuration.setLogPrefix(props.getProperty("logPrefix"));
            //显式定义用什么log框架，不定义则用默认的自动发现jar包机制
            configuration.setLogImpl(resolveClass(props.getProperty("logImpl")));
            //配置工厂
            configuration.setConfigurationFactory(resolveClass(props.getProperty("configurationFactory")));
        }
    }

    /**
     * 解析enviroments元素节点
     * <!--   <environments default="development">                                         -->
     * <!--       <environment id="development">                                           -->
     * <!--           <transactionManager type="JDBC"/>                                    -->
     * <!--           <dataSource type="POOLED">                                           -->
     * <!--               <property name="driver" value="com.mysql.cj.jdbc.Driver"/>       -->
     * <!--               <property name="url" value="jdbc:mysql://localhost:3306/gims"/>  -->
     * <!--               <property name="username" value="gims"/>                         -->
     * <!--               <property name="password" value="gims"/>                         -->
     * <!--           </dataSource>                                                        -->
     * <!--       </environment>                                                           -->
     * <!--   </environments>                                                              -->
     */
    private void environmentsElement(XNode context) throws Exception {
        if (context != null) {
            if (environment == null) {
                // 解析environments节点的default属性的值，例如: <environments default="development">
                environment = context.getStringAttribute("default");
            }
            // 递归解析environments子节点
            for (XNode child : context.getChildren()) {
                // environments 节点下可以拥有多个 environment 子节点，对应多个环境，比如开发环境，测试环境等， 由environments的default属性去选择对应的enviroment
                String id = child.getStringAttribute("id");
                // isSpecial 就是根据由 environments 的 default 属性去选择对应的 environment
                if (isSpecifiedEnvironment(id)) {
                    // 事务， mybatis有两种：JDBC 和 MANAGED, 配置为JDBC则直接使用JDBC的事务，配置为MANAGED则是将事务托管给容器
                    TransactionFactory txFactory = transactionManagerElement(child.evalNode("transactionManager"));
                    // environment 节点下面就是 dataSource 节点了
                    DataSourceFactory dsFactory = dataSourceElement(child.evalNode("dataSource"));
                    DataSource dataSource = dsFactory.getDataSource();
                    Environment.Builder environmentBuilder = new Environment.Builder(id)
                            .transactionFactory(txFactory)
                            .dataSource(dataSource);
                    // 将dataSource设置进configuration对象
                    configuration.setEnvironment(environmentBuilder.build());
                }
            }
        }
    }
    /**
     * dataSource
     * <!-- <dataSource type="POOLED">                                           -->
     * <!--     <property name="driver" value="com.mysql.cj.jdbc.Driver"/>       -->
     * <!--     <property name="url" value="jdbc:mysql://localhost:3306/gims"/>  -->
     * <!--     <property name="username" value="gims"/>                         -->
     * <!--     <property name="password" value="gims"/>                         -->
     * <!-- </dataSource>                                                        -->
     */
    private DataSourceFactory dataSourceElement(XNode context) throws Exception {
        if (context != null) {
            // dataSource的连接池
            String type = context.getStringAttribute("type");
            // dataSource 子节点 Key/Value
            Properties props = context.getChildrenAsProperties();
            // 根据 type="POOLED" 解析返回适当的 DataSourceFactory
            DataSourceFactory factory = (DataSourceFactory) resolveClass(type).newInstance();
            factory.setProperties(props);
            return factory;
        }
        throw new BuilderException("Environment declaration requires a DataSourceFactory.");
    }

    // databaseIdProvider
    // 可以根据不同数据库执行不同的SQL，sql要加databaseId属性
    // 这个功能感觉不是很实用，真要多数据库支持，那SQL工作量将会成倍增长，用mybatis以后一般就绑死在一个数据库上了。但也是一个不得已的方法吧
    // 可以参考org.apache.ibatis.submitted.multidb 包里的测试用例
    //	<databaseIdProvider type="VENDOR">
    //	  <property name="SQL Server" value="sqlserver"/>
    //	  <property name="DB2" value="db2"/>
    //	  <property name="Oracle" value="oracle" />
    //	</databaseIdProvider>
    private void databaseIdProviderElement(XNode context) throws Exception {
        DatabaseIdProvider databaseIdProvider = null;
        if (context != null) {
            String type = context.getStringAttribute("type");
            // awful patch to keep backward compatibility
            //与老版本兼容
            if ("VENDOR".equals(type)) {
                type = "DB_VENDOR";
            }
            Properties properties = context.getChildrenAsProperties();
            //"DB_VENDOR"-->VendorDatabaseIdProvider
            databaseIdProvider = (DatabaseIdProvider) resolveClass(type).newInstance();
            databaseIdProvider.setProperties(properties);
        }
        Environment environment = configuration.getEnvironment();
        if (environment != null && databaseIdProvider != null) {
            //得到当前的databaseId，可以调用DatabaseMetaData.getDatabaseProductName()得到诸如"Oracle (DataDirect)"的字符串，
            //然后和预定义的property比较,得出目前究竟用的是什么数据库
            String databaseId = databaseIdProvider.getDatabaseId(environment.getDataSource());
            configuration.setDatabaseId(databaseId);
        }
    }

    // 事务管理器
    // <transactionManager type="JDBC">
    //  <property name="..." value="..."/>
    // </transactionManager>
    private TransactionFactory transactionManagerElement(XNode context) throws Exception {
        if (context != null) {
            String type = context.getStringAttribute("type");
            Properties props = context.getChildrenAsProperties();
            //根据type="JDBC"解析返回适当的TransactionFactory
            TransactionFactory factory = (TransactionFactory) resolveClass(type).newInstance();
            factory.setProperties(props);
            return factory;
        }
        throw new BuilderException("Environment declaration requires a TransactionFactory.");
    }


    /**
     * 类型处理器
     * <!--    <typeHandlers>-->
     * <!--        &lt;!&ndash;当配置package的时候，mybatis会去配置的package扫描TypeHandler&ndash;&gt;-->
     * <!--        &lt;!&ndash;<package name="com.demo.handler"/>&ndash;&gt;-->
     * <!--        &lt;!&ndash;<typeHandler handler="org.mybatis.example.ExampleTypeHandler"/>&ndash;&gt;-->
     * <!--        <typeHandler jdbcType="VARCHAR" javaType="string" handler="com.demo.handler.MyStringHandler"/>-->
     * <!--    </typeHandlers>-->
     */
    private void typeHandlerElement(XNode parent) throws Exception {
        if (parent != null) {
            for (XNode child : parent.getChildren()) {
                // 如果是package
                if ("package".equals(child.getName())) {
                    String typeHandlerPackage = child.getStringAttribute("name");
                    typeHandlerRegistry.register(typeHandlerPackage);
                } else {
                    // 如果是typeHandler
                    String javaTypeName = child.getStringAttribute("javaType");
                    String jdbcTypeName = child.getStringAttribute("jdbcType");
                    String handlerTypeName = child.getStringAttribute("handler");
                    Class<?> javaTypeClass = resolveClass(javaTypeName);
                    JdbcType jdbcType = resolveJdbcType(jdbcTypeName);
                    // resolveClass 即 TypeAliasRegistry 处理别名的方法
                    Class<?> typeHandlerClass = resolveClass(handlerTypeName);
                    // 注册 typeHandler, typeHandler 通过 TypeHandlerRegistry 这个类管理
                    if (javaTypeClass != null) {
                        if (jdbcType == null) {
                            typeHandlerRegistry.register(javaTypeClass, typeHandlerClass);
                        } else {
                            typeHandlerRegistry.register(javaTypeClass, jdbcType, typeHandlerClass);
                        }
                    } else {
                        typeHandlerRegistry.register(typeHandlerClass);
                    }
                }
            }
        }
    }

    /**
     * Mapper节点解析
     * 1、通过 resource 指定
     * <!-- <mappers>                                                   -->
     * <!--   <mapper resource="org/mybatis/builder/AuthorMapper.xml"/> -->
     * <!--   <mapper resource="org/mybatis/builder/BlogMapper.xml"/>   -->
     * <!--   <mapper resource="org/mybatis/builder/PostMapper.xml"/>   -->
     * <!-- </mappers>                                                  -->
     * 2、通过class指定接口
     * <!-- <mappers>                                                   -->
     * <!--   <mapper class="org.mybatis.builder.AuthorMapper"/>        -->
     * <!--   <mapper class="org.mybatis.builder.BlogMapper"/>          -->
     * <!--   <mapper class="org.mybatis.builder.PostMapper"/>          -->
     * <!-- </mappers>                                                  -->
     * 3、直接指定包，自动扫描
     * <!-- <mappers>                                                   -->
     * <!--   <package name="org.mybatis.builder"/>                     -->
     * <!-- </mappers>                                                  -->
     * 4、URL
     * <!-- <mappers>                                                   -->
     * <!--   <mapper url="file:///var/mappers/AuthorMapper.xml"/>      -->
     * <!--   <mapper url="file:///var/mappers/BlogMapper.xml"/>        -->
     * <!--   <mapper url="file:///var/mappers/PostMapper.xml"/>        -->
     * <!-- </mappers>                                                  -->
     */
    private void mapperElement(XNode parent) throws Exception {
        if (parent != null) {
            for (XNode child : parent.getChildren()) {
                if ("package".equals(child.getName())) {
                    // 如果mappers节点的子节点是package, 那么就扫描package下的文件, 注入进configuration
                    String mapperPackage = child.getStringAttribute("name");
                    configuration.addMappers(mapperPackage);
                } else {
                    // resource, url, class 三选一
                    String resource = child.getStringAttribute("resource");
                    String url = child.getStringAttribute("url");
                    String mapperClass = child.getStringAttribute("class");
                    if (resource != null && url == null && mapperClass == null) {
                        ErrorContext.instance().resource(resource);
                        InputStream inputStream = Resources.getResourceAsStream(resource);
                        // 映射器比较复杂，调用 XMLMapperBuilder
                        // 注意在for循环里每个 mapper 都重新new一个 XMLMapperBuilder，来解析
                        XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                        mapperParser.parse();
                    } else if (resource == null && url != null && mapperClass == null) {
                        // 使用绝对url路径
                        ErrorContext.instance().resource(url);
                        InputStream inputStream = Resources.getUrlAsStream(url);
                        // 映射器比较复杂，调用XMLMapperBuilder
                        XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
                        mapperParser.parse();
                    } else if (resource == null && url == null && mapperClass != null) {
                        // 使用java类名
                        Class<?> mapperInterface = Resources.classForName(mapperClass);
                        // 直接把这个映射加入配置
                        configuration.addMapper(mapperInterface);
                    } else {
                        throw new BuilderException("A mapper element may only specify a url, resource or class, but not more than one.");
                    }
                }
            }
        }
    }

    //比较id和environment是否相等
    private boolean isSpecifiedEnvironment(String id) {
        if (environment == null) {
            throw new BuilderException("No environment specified.");
        } else if (id == null) {
            throw new BuilderException("Environment requires an id attribute.");
        } else if (environment.equals(id)) {
            return true;
        }
        return false;
    }

}
