package com.demo.interceptor.jdkproxy4;

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
		target = (HelloService) transactionInterceptor.plugin(target);

		LogInterceptor logInterceptor = new LogInterceptor();
		target = (HelloService) logInterceptor.plugin(target);

		target.sayHello();
	}

}
