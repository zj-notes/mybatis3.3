
package org.apache.ibatis.builder;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

import java.util.List;
import java.util.Properties;

public class ExampleObjectFactory extends DefaultObjectFactory {

  public <T> T create(Class<T> type) {
    return super.<T>create(type);
  }

  public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
    return super.<T>create(type, constructorArgTypes, constructorArgs);
  }

  public void setProperties(Properties properties) {
    super.setProperties(properties);
  }

}
