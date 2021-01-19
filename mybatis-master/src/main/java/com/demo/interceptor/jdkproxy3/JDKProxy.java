package com.demo.interceptor.jdkproxy3;

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
		HelloService target = new HelloServiceImpl();
		Interceptor transactionInterceptor = new TransactionInterceptor();
		HelloService targetProxy = (HelloService) HWInvocationHandler.wrap(target, transactionInterceptor);
		targetProxy.sayHello();
	}

}
