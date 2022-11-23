package org.apache.ibatis.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 拦截器链
public class InterceptorChain {

    // 内部就是一个拦截器的List
    private final List<Interceptor> interceptors = new ArrayList<Interceptor>();

    public Object pluginAll(Object target) {
        // 循环调用每个Interceptor.plugin方法
        for (Interceptor interceptor : interceptors) {
            // plugin 方法是由具体的插件类实现
            target = interceptor.plugin(target);
        }
        return target;
    }
    /** 添加插件实例到 interceptors 集合中 */
    public void addInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }
    /** 获取插件列表 */
    public List<Interceptor> getInterceptors() {
        return Collections.unmodifiableList(interceptors);
    }

}
