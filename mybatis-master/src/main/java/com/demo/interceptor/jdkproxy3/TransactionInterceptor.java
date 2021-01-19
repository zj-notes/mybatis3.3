package com.demo.interceptor.jdkproxy3;

public class TransactionInterceptor implements Interceptor {

	@Override
    public Object intercept(Invocation invocation) throws Exception{
        System.out.println("------插入前置通知代码-------------");
        Object result = invocation.process();
        System.out.println("------插入后置处理代码-------------");
        return result;
    }

}
