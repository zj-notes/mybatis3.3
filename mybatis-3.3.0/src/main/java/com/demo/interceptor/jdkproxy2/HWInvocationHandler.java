package com.demo.interceptor.jdkproxy2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class HWInvocationHandler implements InvocationHandler {

	
	private Object target;

    private List<Interceptor> interceptorList = new ArrayList<>();

    public HWInvocationHandler(Object target,List<Interceptor> interceptorList) {
        this.target = target;
        this.interceptorList = interceptorList;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
       //处理多个拦截器
        for (Interceptor interceptor : interceptorList) {
            interceptor.intercept();
        }
        return method.invoke(target, args);
    }

	public static Object wrap(Object target,List<Interceptor> interceptorList) {
        HWInvocationHandler targetProxy = new HWInvocationHandler(target, interceptorList);
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                                      target.getClass().getInterfaces(),targetProxy);
    }

}
