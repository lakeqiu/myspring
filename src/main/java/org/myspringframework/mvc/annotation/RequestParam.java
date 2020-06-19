package org.myspringframework.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求方法的参数名称
 * @author lakeqiu
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {
    /**
     * @return 方法参数名称
     */
    String value() default "";

    /**
     * @return 该参数是否是必须的
     */
    boolean required() default true;
}
