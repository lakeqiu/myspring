package org.myspringframework.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * 负责创建动态代理类
 * @author lakeqiu
 */
public class ProxyCreator {
    /**
     * 创建动态代理对象并返回
     * @param targetClass 被代理类的Class对象
     * @param methodInterceptor 方法拦截器
     * @return 动态代理类
     */
    public static Object createProxy(Class<?> targetClass, MethodInterceptor methodInterceptor) {
        return Enhancer.create(targetClass, methodInterceptor);
    }
}
