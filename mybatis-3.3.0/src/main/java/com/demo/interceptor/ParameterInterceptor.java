package com.demo.interceptor;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;

/**
 * ParameterHandler 类型插件
 */
@Intercepts({
        @Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class})
})
public class ParameterInterceptor implements Interceptor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 这里是每次执行操作的时候，都会进行这个拦截器的方法内
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 增强逻辑
        logger.info("=========== ParameterInterceptor intercept, method: {}", invocation.getMethod().getName());
        // 执行原方法
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
        logger.info("=========== ParameterInterceptor setProperties：" + properties);
    }
}
