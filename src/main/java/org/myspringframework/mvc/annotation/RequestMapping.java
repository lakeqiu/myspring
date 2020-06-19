package org.myspringframework.mvc.annotation;

import org.myspringframework.mvc.type.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标识Controller的方法与请求路径和请求方法的映射关系
 * @author lakeqiu
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    /**
     * @return 请求路径
     */
    String value() default "";

    /**
     * @return 请求方法类型
     */
    RequestMethod method() default RequestMethod.GET;
}
