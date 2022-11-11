package com.demo.interceptor.mybtaisPlugins;

@Intercepts({
        @Signature(
                type = MybatisPlugin.SpeakService.class,
                method = "speak",
                args = {String.class}
        )
})
public class SpeakInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Exception {
        System.out.println("------Speak 插入前置通知代码-------------");
        Object result = invocation.process();
        System.out.println("------Speak 插入后置处理代码-------------");
        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

}
