package com.demo.interceptor.mybtaisPlugins;

public class MybatisPlugin {
	// SpeakService
	public interface SpeakService {
		void speak(String language);

		void speak2(String language);
	}
	static class SpeakServiceImpl implements SpeakService {
		@Override
		public void speak(String language) {
			System.out.println("speak " + language + "......");
		}

		@Override
		public void speak2(String language) {
			System.out.println("speak2 不被拦截 " + language + "......");
		}
	}

	// Eat
	public interface EatService {
		void eat();
	}
	static class EatServiceImpl implements EatService {
		@Override
		public void eat() {
			System.out.println("eat......");
		}
	}

	public static void main(String[] args) {
		InterceptorChain interceptorChain = new InterceptorChain();
		interceptorChain.addInterceptor(new SpeakInterceptor());
		interceptorChain.addInterceptor(new EatInterceptor());

		EatService eatService = new EatServiceImpl();
		SpeakService speakService = new SpeakServiceImpl();

		eatService = (EatService)interceptorChain.pluginAll(eatService);
		eatService.eat();

		speakService = (SpeakService)interceptorChain.pluginAll(speakService);
		speakService.speak("English");
		speakService.speak2("English");
	}

}
