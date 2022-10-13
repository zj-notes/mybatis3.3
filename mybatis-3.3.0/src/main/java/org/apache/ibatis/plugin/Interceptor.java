
package org.apache.ibatis.plugin;

import java.util.Properties;

public interface Interceptor {

  //拦截
  Object intercept(Invocation invocation) throws Throwable;

  //插入
  Object plugin(Object target);

  //设置属性
  void setProperties(Properties properties);

}
