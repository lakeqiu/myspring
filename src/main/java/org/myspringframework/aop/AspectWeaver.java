package org.myspringframework.aop;

import org.myspringframework.aop.annotation.Aspect;
import org.myspringframework.aop.annotation.Order;
import org.myspringframework.aop.aspect.AspectInfo;
import org.myspringframework.aop.aspect.DefaultAspect;
import org.myspringframework.core.BeanContainer;
import org.myspringframework.util.ValidationUtil;

import java.util.*;

/**
 * 2.0 版本
 * 将横切逻辑织入被代理对象以生成动态代理对象
 * @author lakeqiu
 */
public class AspectWeaver {
    private BeanContainer beanContainer;

    public AspectWeaver() {
        beanContainer = BeanContainer.getInstance();
    }


    public void doAop() {
        // 1、获取所有的切面类
        Set<Class<?>> aspectSet = beanContainer.getClassesByAnnotation(Aspect.class);
        // 判空处理
        if (ValidationUtil.isEmpty(aspectSet)) {
            return;
        }

        // 2、拼接AspectInfoList,即组装成AspectInfoList
        List<AspectInfo> aspectInfoList = packAspectInfoList(aspectSet);

        // 3、遍历容器中的类
        Set<Class<?>> classSet = beanContainer.getClasses();
        for (Class<?> targetClass : classSet) {
            // 排除Aspect自身
            if (targetClass.isAnnotationPresent(Aspect.class)) {
                continue;
            }

            // 4、初筛符合条件的Aspect，即可以切 targetClass 的切面
            List<AspectInfo> roughMatchedAspectList = collectRoughMatchedAspectInfoListForSpecificClass(aspectInfoList, targetClass);

            // 5、尝试进行Aspect的织入
            wrapIfNecessary(roughMatchedAspectList, targetClass);
        }
    }

    /**
     * 将所有可以切 targetClass 的aspect 织入 targetClass
     * 即生成动态代理实例后代替之前的实例
     * @param roughMatchedAspectList 可以切 targetClass 的 aspect
     * @param targetClass 被代理类的Class对象
     */
    private void wrapIfNecessary(List<AspectInfo> roughMatchedAspectList, Class<?> targetClass) {
        // 没有可以切 targetClass 的 aspect，直接返回
        if(ValidationUtil.isEmpty(roughMatchedAspectList)){
            return;
        }
        // 创建动态代理对象
        AspectListExecutor aspectListExecutor = new AspectListExecutor(targetClass, roughMatchedAspectList);
        Object proxyBean = ProxyCreator.createProxy(targetClass, aspectListExecutor);
        // 将动态代理实例添加到BeanContainer中，取代未被代理前的类实例
        beanContainer.addBean(targetClass, proxyBean);
    }

    /**
     * 初筛符合条件的Aspect，即可以切 targetClass 的切面
     * @param aspectInfoList aspectInfoList
     * @param targetClass beanContainer中管理的类的Class对象
     * @return List<AspectInfo>
     */
    private List<AspectInfo> collectRoughMatchedAspectInfoListForSpecificClass(List<AspectInfo> aspectInfoList, Class<?> targetClass) {
        List<AspectInfo> roughMatchedAspectList = new ArrayList<>();
        for(AspectInfo aspectInfo : aspectInfoList){
            // 粗筛
            if(aspectInfo.getPointcutLocator().roughMatches(targetClass)){
                roughMatchedAspectList.add(aspectInfo);
            }
        }
        return roughMatchedAspectList;
    }

    /**
     * 拼接AspectInfoList,即组装成AspectInfoList
     * @param aspectSet 切面类的Class对象集合
     * @return AspectInfoList
     */
    private List<AspectInfo> packAspectInfoList(Set<Class<?>> aspectSet) {
        List<AspectInfo> aspectInfoList = new ArrayList<>();

        // 遍历切面类的Class对象
        for(Class<?> aspectClass : aspectSet){
            // 校验是否符合规范
            if (verifyAspect(aspectClass)){
                Order orderTag = aspectClass.getAnnotation(Order.class);
                Aspect aspectTag = aspectClass.getAnnotation(Aspect.class);
                // 获取aspectClass对应的实例对象
                DefaultAspect defaultAspect = (DefaultAspect) beanContainer.getBean(aspectClass);
                // 初始化表达式定位器
                PointcutLocator pointcutLocator = new PointcutLocator(aspectTag.pointcut());

                // 根据相关信息组装AspectInfo并加入集合中
                AspectInfo aspectInfo = new AspectInfo(orderTag.value(), defaultAspect, pointcutLocator);
                aspectInfoList.add(aspectInfo);
            } else {
                // 不符合规范则直接抛出异常
                throw new RuntimeException("@Aspect 或 @Order 注解没有添加在切面类上, 或切面类没有继承" +
                        "DefaultAspect类或@Aspect注解的属性值是Aspect");
            }
        }
        return aspectInfoList;
    }

    /**
     * 校验切面类是否符合规范
     *  框架中一定要遵守给切面类添加 @Aspect 和 @Order 标签的规范，同时，
     *  必须继承 DefaultAspect.class,
     *  此外，@Aspect 的属性不能是其本身（会进入无限递归）
     * @param aspectClass 切面类Class对象
     * @return 是否符合规范
     */
    private boolean verifyAspect(Class<?> aspectClass) {
        return aspectClass.isAnnotationPresent(Aspect.class) &&
                aspectClass.isAnnotationPresent(Order.class) &&
                DefaultAspect.class.isAssignableFrom(aspectClass);
    }
}
