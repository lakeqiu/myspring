package org.myspringframework.aop;

import lombok.Getter;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.myspringframework.aop.aspect.AspectInfo;
import org.myspringframework.util.ValidationUtil;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * 往被代理对象的被代理方法添加横切逻辑,一个代理的类一个实例
 * MethodInterceptor 类似于 JDK 的 InvocationHandler
 * @author lakeqiu
 */
public class AspectListExecutor implements MethodInterceptor {
    /**
     * 被代理的类
     */
    private Class<?> targetClass;
    /**
     * 按 @Order 排序后的 AspectInfo
     */
    @Getter
    private List<AspectInfo> sortAspectInfoList;

    public AspectListExecutor(Class<?> targetClass, List<AspectInfo> aspectInfoList) {
        this.targetClass = targetClass;
        this.sortAspectInfoList = sortAspectInfoList(aspectInfoList);
    }

    /**
     * 按照 Order 的值进行排序，确保 order值 小的 Aspect 先被织入
     * @param aspectInfoList aspectInfo集合
     * @return 排序好的集合
     */
    private List<AspectInfo> sortAspectInfoList(List<AspectInfo> aspectInfoList) {
        Collections.sort(aspectInfoList, Comparator.comparingInt(AspectInfo::getOrderIndex));
        return aspectInfoList;
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        // 定义返回值
        Object returnValue = null;

        // 筛选出代理 method 方法的 AspectInfo，精筛
        collectAccurateMatchedAspectList(method);

        // 没有代理信息，直接执行被代理方法，不用进行代理了
        if (ValidationUtil.isEmpty(sortAspectInfoList)) {
            returnValue = methodProxy.invokeSuper(proxy, args);
            return returnValue;
        }

        // 1、按照order的顺序升序执行完所有Aspect的before方法
        invokeBeforeAdvices(method, args);

        try {
            // 2、执行被代理类的方法
            returnValue = methodProxy.invokeSuper(proxy, args);

            // 3、如果被代理类正常返回，则按照order的顺序降序执行完Aspect的afterRunning方法
            returnValue = invokeAfterRunningAdvices(method, args, returnValue);

        } catch (Exception e) {
            // 4、如果被代理方法执行过程中抛出异常，则按照order的顺序降序执行完所有的Aspect的afterThrowing方法
            invokeAfterThrowingAdvices(method, args, e);
        }

        return returnValue;
    }

    /**
     * 筛选出代理 method 方法的 AspectInfo，精筛
     * @param method 方法
     */
    private void collectAccurateMatchedAspectList(Method method) {
        // 都没有切面类，说明 method 方法没有被代理，直接返回
        if (ValidationUtil.isEmpty(sortAspectInfoList)) {
            return;
        }

        Iterator<AspectInfo> it = sortAspectInfoList.iterator();
        while (it.hasNext()){
            AspectInfo aspectInfo = it.next();
            // AspectInfo不完全匹配 method 方法，移除
            if(!aspectInfo.getPointcutLocator().accurateMatches(method)){
                it.remove();
            }
        }
    }

    /**
     * 按照order的顺序升序执行完所有Aspect的before方法
     * @param method 被代理的方法
     * @param args 方法的参数列表
     */
    private void invokeBeforeAdvices(Method method, Object[] args) throws Throwable {
        for (AspectInfo aspectInfo : sortAspectInfoList) {
            aspectInfo.getAspectObject().before(targetClass, method, args);
        }
    }

    /**
     * 如果被代理类正常返回，则按照order的顺序降序执行完Aspect的afterRunning方法
     * @param method 被代理的方法
     * @param args 方法的参数列表
     * @param returnValue 被代理方法执行完的返回值
     * @return invokeAfterRunningAdvices 方法执行完的返回值
     */
    private Object invokeAfterRunningAdvices(Method method, Object[] args, Object returnValue) throws Throwable {
        Object result = null;

        for (int i = sortAspectInfoList.size() - 1; i >= 0; i--) {
            result = sortAspectInfoList.get(i).getAspectObject().afterRunning(targetClass, method, args, returnValue);
        }

        return result;
    }

    /**
     * 如果被代理方法执行过程中抛出异常，则按照order的顺序降序执行完所有的Aspect的afterThrowing方法
     * @param method 被代理的方法
     * @param args 被代理方法的参数列表
     * @param e 被代理方法执行过程中抛出的异常
     */
    private void invokeAfterThrowingAdvices(Method method, Object[] args, Exception e) throws Throwable {
        for (int i = sortAspectInfoList.size() - 1; i >= 0; i--) {
            sortAspectInfoList.get(i).getAspectObject().afterThrowing(targetClass, method, args, e);
        }
    }
}
