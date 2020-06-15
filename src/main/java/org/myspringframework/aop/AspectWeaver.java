package org.myspringframework.aop;

import org.myspringframework.aop.annotation.Aspect;
import org.myspringframework.aop.annotation.Order;
import org.myspringframework.aop.aspect.AspectInfo;
import org.myspringframework.aop.aspect.DefaultAspect;
import org.myspringframework.core.BeanContainer;
import org.myspringframework.util.ValidationUtil;

import java.lang.annotation.Annotation;
import java.util.*;

/**
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

        // 2、将切面类按照不同的织入目标进行分离
        // 存放不同织入目标的AspectInfo的容器，Map<织入目标，切面信息类>
        Map<Class<? extends Annotation>, List<AspectInfo>> categorizedMap = new HashMap<>();
        // 将切面类按照不同的织入目标进行分离
        for (Class<?> aspectClass : aspectSet) {
            // 校验切面类是否规范
            if (verifyAspect(aspectClass)) {
                categorizeAspect(categorizedMap, aspectClass);
            } else {
                throw new RuntimeException("@Aspect 或 @Order 注解没有添加在切面类上, 或切面类没有继承" +
                        "DefaultAspect类或@Aspect注解的属性值是Aspect");
            }
        }

        // 3、按照不同的织入目标分别去按序织入Aspect的逻辑
        if (ValidationUtil.isEmpty(categorizedMap)) {
            return;
        }
        for (Class<? extends Annotation> category : categorizedMap.keySet()) {
            weaveByCategory(category, categorizedMap.get(category));
        }
    }

    private void weaveByCategory(Class<? extends Annotation> category, List<AspectInfo> aspectInfos) {
        // 1、获取被代理类Class对象集合
        Set<Class<?>> classSet = beanContainer.getClassesByAnnotation(category);
        if (ValidationUtil.isEmpty(classSet)) {
            return;
        }

        // 2、遍历被代理类Class对象，为每个被代理类生成动态代理实例
        for (Class<?> targetClass : classSet) {
            AspectListExecutor aspectListExecutor = new AspectListExecutor(targetClass, aspectInfos);
            Object proxyBean = ProxyCreator.createProxy(targetClass, aspectListExecutor);

            // 3、将动态代理实例添加到BeanContainer中，取代未被代理前的类实例
            beanContainer.addBean(targetClass, proxyBean);
        }
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
                DefaultAspect.class.isAssignableFrom(aspectClass) &&
                aspectClass.getAnnotation(Aspect.class).value() != Aspect.class;
    }

    /**
     * 将切面类按照不同的织入目标进行分离
     * @param categorizedMap 存放容器
     * @param aspectClass 切面类的Class对象
     */
    private void categorizeAspect(Map<Class<? extends Annotation>, List<AspectInfo>> categorizedMap, Class<?> aspectClass) {
        // 获取切面类的相关注解信息
        Aspect aspectTag = aspectClass.getAnnotation(Aspect.class);
        Order orderTag = aspectClass.getAnnotation(Order.class);
        // 获取aspectClass对应的实例对象
        DefaultAspect aspect = (DefaultAspect) beanContainer.getBean(aspectClass);
        // 根据相关信息组成切面信息类
        AspectInfo aspectInfo = new AspectInfo(orderTag.value(), aspect);

        if (!categorizedMap.containsKey(aspectTag.value())) {
            // 如果织入的joinPoint第一次出现，则以joinPoint为key，以新创建的List<AspectInfo>为value
            List<AspectInfo> aspectInfoList = new ArrayList<>();
            aspectInfoList.add(aspectInfo);
            categorizedMap.put(aspectTag.value(), aspectInfoList);
        } else {
            // 如果织入的joinPoint不是第一次出现，则往joinPoint对应的value添加新的AspectInfo逻辑
            List<AspectInfo> aspectInfoList = categorizedMap.get(aspectTag.value());
            aspectInfoList.add(aspectInfo);
        }
    }
}
