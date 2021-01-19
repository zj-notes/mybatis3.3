package com.demo.interceptor.jdkproxy2;

public class LogInterceptor implements Interceptor {

	@Override
	public void intercept() {
        System.out.println("------插入前置通知代码-------------");
	}

}
