package com.demo.interceptor.jdkproxy2;

import java.util.ArrayList;
import java.util.List;

public class JDKProxy {

	/**
	 * 一个接口
	 */
	public interface HelloService {
		void sayHello();
	}

	/**
	 * 目标类实现接口
	 */
	static class HelloServiceImpl implements HelloService {
		@Override
		public void sayHello() {
			System.out.println("sayHello......");
		}
	}

	public static void main(String[] args) {
		List<Interceptor> interceptorList = new ArrayList<>();
		interceptorList.add(new LogInterceptor());
		interceptorList.add(new TransactionInterceptor());

		HelloService target = new HelloServiceImpl();
		HelloService targetProxy = (HelloService) HWInvocationHandler.wrap(target, interceptorList);
		targetProxy.sayHello();

	}

}
