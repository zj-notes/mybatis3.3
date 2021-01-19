package com.demo.interceptor.jdkproxy5;

public class TransactionInterceptor implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Exception {
		System.out.println("------插入前置通知代码Transaction-------------");
		Object result = invocation.process();
		System.out.println("------插入后置处理代码Transaction-------------");
		return result;
	}

	@Override
	public Object plugin(Object target) {
		return HWInvocationHandler.wrap(target, this);
	}

}
