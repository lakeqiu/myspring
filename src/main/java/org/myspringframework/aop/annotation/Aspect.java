package org.myspringframework.aop.annotation;

import java.lang.annotation.*;

/**
 * @author lakeqiu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
    // 需要被注入横切逻辑的注解标签
    Class<? extends Annotation> value();
}
