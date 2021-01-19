package com.demo.interceptor.jdkproxy2;

public class TransactionInterceptor implements Interceptor {

	@Override
	public void intercept() {
        System.out.println("------插入后置处理代码-------------");
	}

}
