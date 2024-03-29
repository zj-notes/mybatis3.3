package org.apache.ibatis.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.reflection.ExceptionUtil;

/**
 * 插件,用的代理模式
 */
public class Plugin implements InvocationHandler {

    private Object target;
    private Interceptor interceptor;
    private Map<Class<?>, Set<Method>> signatureMap;

    private Plugin(Object target, Interceptor interceptor, Map<Class<?>, Set<Method>> signatureMap) {
        this.target = target;
        this.interceptor = interceptor;
        this.signatureMap = signatureMap;
    }

    public static Object wrap(Object target, Interceptor interceptor) {
        //取得签名Map
        Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);
        //取得要改变行为的类(ParameterHandler|ResultSetHandler|StatementHandler|Executor)
        Class<?> type = target.getClass();
        //取得接口
        Class<?>[] interfaces = getAllInterfaces(type, signatureMap);
        //产生代理
        if (interfaces.length > 0) {
            return Proxy.newProxyInstance(
                    type.getClassLoader(),
                    interfaces,
                    new Plugin(target, interceptor, signatureMap));
        }
        return target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            // 获取被拦截方法列表，比如： signatureMap.get(Executor.class)，可能返回 [query, update, commit]
            Set<Method> methods = signatureMap.get(method.getDeclaringClass());
            // 检测方法列表是否包含被拦截的方法
            if (methods != null && methods.contains(method)) {
                // 调用Interceptor.intercept，执行插件逻辑
                return interceptor.intercept(new Invocation(target, method, args));
            }
            // 未被拦截的方法，执行原逻辑
            return method.invoke(target, args);
        } catch (Exception e) {
            throw ExceptionUtil.unwrapThrowable(e);
        }
    }

    // 取得签名Map, 就是获取Interceptor实现类上面的注解，要拦截的是那个类（Executor，ParameterHandler，ResultSetHandler，StatementHandler）的那个方法
    private static Map<Class<?>, Set<Method>> getSignatureMap(Interceptor interceptor) {
        //取Intercepts注解，例子可参见ExamplePlugin.java
        Intercepts interceptsAnnotation = interceptor.getClass().getAnnotation(Intercepts.class);
        // issue #251
        //必须得有Intercepts注解，没有报错
        if (interceptsAnnotation == null) {
            throw new PluginException("No @Intercepts annotation was found in interceptor " + interceptor.getClass().getName());
        }
        //value是数组型，Signature的数组
        Signature[] sigs = interceptsAnnotation.value();
        //每个class里有多个Method需要被拦截,所以这么定义
        Map<Class<?>, Set<Method>> signatureMap = new HashMap<Class<?>, Set<Method>>();
        for (Signature sig : sigs) {
            Set<Method> methods = signatureMap.get(sig.type());
            if (methods == null) {
                methods = new HashSet<Method>();
                signatureMap.put(sig.type(), methods);
            }
            try {
                Method method = sig.type().getMethod(sig.method(), sig.args());
                methods.add(method);
            } catch (NoSuchMethodException e) {
                throw new PluginException("Could not find method on " + sig.type() + " named " + sig.method() + ". Cause: " + e, e);
            }
        }
        return signatureMap;
    }

    // 取目标类接口
    private static Class<?>[] getAllInterfaces(Class<?> type, Map<Class<?>, Set<Method>> signatureMap) {
        Set<Class<?>> interfaces = new HashSet<Class<?>>();
        while (type != null) {
            for (Class<?> c : type.getInterfaces()) {
                //只能拦截ParameterHandler|ResultSetHandler|StatementHandler|Executor
                //拦截其他的无效
                //当然我们可以覆盖Plugin.wrap方法，达到拦截其他类的功能
                if (signatureMap.containsKey(c)) {
                    interfaces.add(c);
                }
            }
            type = type.getSuperclass();
        }
        return interfaces.toArray(new Class<?>[interfaces.size()]);
    }

}
