package com.demo.interceptor;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Properties;

/**
 * Executor 类型插件
 */
@Intercepts({@Signature(
        type = Executor.class,
        method = "update",
        args = {MappedStatement.class, Object.class}
), @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
), @Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
)})
public class ExecutorInterceptor implements Interceptor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 这里是每次执行操作的时候，都会进行这个拦截器的方法内
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 增强逻辑
        logger.info("=========== Executor intercept, method: {}", invocation.getMethod().getName());
        return invocation.proceed();
    }

    /**
     * 主要是为了把这个拦截器生成一个代理放到拦截器链中。
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 获取配置文件的属性
     * 插件初始化的时候调用，也只调用一次，插件配置的属性从这里设置进来
     **/
    @Override
    public void setProperties(Properties properties) {
        logger.info("=========== ExecutorInterceptor setProperties：" + properties);
    }
}
