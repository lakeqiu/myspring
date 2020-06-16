package org.myspringframework.aop.annotation;

import java.lang.annotation.*;

/**
 * @author lakeqiu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
    /*// 需要被注入横切逻辑的注解标签, 2.0版本不在需要
    Class<? extends Annotation> value();*/
    // 2.0版本所需要的属性
    String pointcut();
}
