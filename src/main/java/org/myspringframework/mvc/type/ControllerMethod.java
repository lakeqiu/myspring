package org.myspringframework.mvc.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 保存待执行的Controller及其方法实例与参数的映射
 * @author lakeqiu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControllerMethod {
    /**
     * 其对象的Controller的Class对象
     */
    private Class<?> controllerClass;

    /**
     * 对应的Controller方法实例
     */
    private Method invokeMethod;

    /**
     * 方法参数名称以及对应的参数类型
     */
    private Map<String, Class<?>> methodParameters;
}
