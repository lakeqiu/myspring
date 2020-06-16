package com.lakeqiu.aspect;

import lombok.extern.slf4j.Slf4j;
import org.myspringframework.aop.annotation.Aspect;
import org.myspringframework.aop.annotation.Order;
import org.myspringframework.aop.aspect.DefaultAspect;
import org.myspringframework.core.annotation.Controller;

import java.lang.reflect.Method;

/**
 * @author lakeqiu
 */
@Slf4j
@Aspect(pointcut = "execution(* com.lakeqiu.controller.superadmin..*.*(..))")
@Order(0)
public class ControllerTimeCalculatorAspect extends DefaultAspect {
    private long startTime;

    /**
     * 钩子方法
     *
     * @param targetClass 被代理的目标类
     * @param method      被代理的目标方法
     * @param args        被代理的目标方法对应的参数列表
     * @throws Throwable 异常
     */
    @Override
    public void before(Class<?> targetClass, Method method, Object[] args) throws Throwable {
        log.info("开始计时，执行的类是[{}], 执行的方法是[{}], 参数是[{}]",
                targetClass.getName(), method.getName(), args);
        startTime = System.currentTimeMillis();
    }

    /**
     * 钩子方法
     *
     * @param targetClass 被代理的目标类
     * @param method      被代理的目标方法
     * @param args        被代理的目标方法对应的参数列表
     * @param returnValue 被代理的目标方法执行后的返回值
     * @throws Throwable 异常
     */
    @Override
    public Object afterRunning(Class<?> targetClass, Method method, Object[] args, Object returnValue) throws Throwable {
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;
        log.info("结束计时，执行的类是[{}], 执行的方法是[{}], 参数是[{}], 返回值是[{}], 执行时间为[{}]",
                targetClass.getName(), method.getName(), args, returnValue, time);
        return returnValue;
    }
}
