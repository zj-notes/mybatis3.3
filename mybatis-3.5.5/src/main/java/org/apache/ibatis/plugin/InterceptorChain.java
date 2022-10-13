/**
 *    Copyright 2009-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.apache.ibatis.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Clinton Begin
 */
public class InterceptorChain {

  private final List<Interceptor> interceptors = new ArrayList<>();

  public Object pluginAll(Object target) {
    for (Interceptor interceptor : interceptors) {
      // 这里的 interceptor 是 Plugin 也即是自定义插件中plugin方法
      // 这里的target是Executor、ParameterHandler、ResultSetHandler、StatementHandler接口的实现类 具体是什么 要看@Interceptors中拦截的接口
      // 这里的target是通过for循环不断赋值的，也就是说如果有多个拦截器，那么如果我用P表示代理，生成第       //一次代理为P(target)，生成第二次代理为P(P(target))，生成第三次代理为P(P(P(target)))，不断      //嵌套下去，这就得到一个重要的结论：<plugins>...</plugins>中后定义的<plugin>实际其拦截器方法     //先被执行，因为根据这段代码来看，后定义的<plugin>代理实际后生成，包装了先生成的代理，自然其代理方     //法也先执行.也即是interceptor的执行顺序 后定义先执行
      // 这里的plugin方法实际上是调用对应拦截器类的重载方法
      target = interceptor.plugin(target);
    }
    return target;
  }

  public void addInterceptor(Interceptor interceptor) {
    // 添加拦截器至集合
    interceptors.add(interceptor);
  }

  public List<Interceptor> getInterceptors() {
    return Collections.unmodifiableList(interceptors);
  }

}
