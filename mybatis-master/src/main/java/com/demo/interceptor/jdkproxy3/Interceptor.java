package com.demo.interceptor.jdkproxy3;

public interface Interceptor {
	/**
	 * 具体拦截处理
	 */
    Object intercept(Invocation invocation) throws Exception;
}
