package com.demo.interceptor.jdkproxy4;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HWInvocationHandler implements InvocationHandler {

	private Object target;

	private Interceptor interceptor;

	public HWInvocationHandler(Object target, Interceptor interceptor) {
		this.target = target;
		this.interceptor = interceptor;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Invocation invocation = new Invocation(target, method, args);
		return interceptor.intercept(invocation);
	}

	public static Object wrap(Object target, Interceptor interceptor) {
		HWInvocationHandler targetProxy = new HWInvocationHandler(target, interceptor);
		return Proxy.newProxyInstance(target.getClass().getClassLoader(), target.getClass().getInterfaces(),
				targetProxy);
	}

}
