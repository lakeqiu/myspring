package org.myspringframework.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myspringframework.core.annotation.Component;
import org.myspringframework.core.annotation.Controller;
import org.myspringframework.core.annotation.Repository;
import org.myspringframework.core.annotation.Service;
import org.myspringframework.util.ClassUtil;
import org.myspringframework.util.ValidationUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean容器
 * @author lakeqiu
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanContainer {
    /**
     * 存放所有被配置标记的目标对象
     */
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    /**
     * 用来判断bean是否需要加载的列表（有这些注解说明有IOC容器管理，需要加载）
     */
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION
            = Arrays.asList(Component.class, Controller.class, Repository.class, Service.class);

    /**
     * 容器是否已经被加载过
     */
    private volatile boolean loaded = false;
    /**
     * @return 容器是否已经被加载过
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * 扫描加载所有Bean
     * @param packageName 要加载bean所在包名
     */
    public synchronized void loadBeans(String packageName) {
        // 如果容器被加载过了就不加载了
        if (isLoaded()) {
            log.warn("IOC容器已经被加载过了");
            return;
        }

        // 获取包下类集合
        Set<Class<?>> classSet = ClassUtil.extractPackageClass(packageName);

        if (ValidationUtil.isEmpty(classSet)) {
            log.warn("包中没有任何类：" + packageName);
            return;
        }

        // 遍历包下所有类
        for (Class<?> clazz : classSet) {
            // 如果是由IOC容器管理的，才加载
            for (Class<? extends Annotation> annotation : BEAN_ANNOTATION) {
                // 如果类上面标注了定义的注解
                if (clazz.isAnnotationPresent(annotation)) {
                    // 将目标类本身作为键，目标类实例作为值，加载进IOC容器
                    beanMap.put(clazz, ClassUtil.newInstance(clazz, true));
                }
            }
        }

        loaded = true;
    }

    /**
     * 获取Bean实例的数量
     * @return bean实例的数量
     */
    public int size() {
        return beanMap.size();
    }

    /**
     * 获取Bean容器实例
     * @return Bean容器
     */
    public static BeanContainer getInstance() {
        return ContainerHolder.HOLDER.instance;
    }

    private enum ContainerHolder {
        HOLDER;
        private BeanContainer instance;
        ContainerHolder() {
            instance = new BeanContainer();
        }
    }

    /**
     * 添加一个class对象及其Bean实例
     *
     * @param clazz Class对象
     * @param bean  Bean实例
     * @return 原有的Bean实例, 没有则返回null
     */
    public Object addBean(Class<?> clazz, Object bean) {
        return beanMap.put(clazz, bean);
    }

    /**
     * 移除一个IOC容器管理的对象
     *
     * @param clazz Class对象
     * @return 删除的Bean实例, 没有则返回null
     */
    public Object removeBean(Class<?> clazz) {
        return beanMap.remove(clazz);
    }

    /**
     * 根据Class对象获取Bean实例
     *
     * @param clazz Class对象
     * @return Bean实例
     */
    public Object getBean(Class<?> clazz) {
        return beanMap.get(clazz);
    }

    /**
     * 获取容器管理的所有Class对象集合
     *
     * @return Class集合
     */
    public Set<Class<?>> getClasses() {
        return beanMap.keySet();
    }

    /**
     * 获取所有Bean集合
     *
     * @return Bean集合
     */
    public Set<Object> getBeans() {
        return new HashSet<>(beanMap.values());
    }

    /**
     * 根据注解筛选出Bean的Class集合
     *
     * @param annotation 注解
     * @return Class集合
     */
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        // 1、获取beanMap中所有的class对象
        Set<Class<?>> keySet = beanMap.keySet();
        // keySet为空
        if (ValidationUtil.isEmpty(keySet)) {
            log.warn("beanMap为空");
            return null;
        }

        // 2、通过注解筛选出被注解标记的class对象，并添加进classSet中
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> clazz : keySet) {
            // 判断类是否有相关的注解标记
            if (clazz.isAnnotationPresent(annotation)) {
                classSet.add(clazz);
            }
        }

        // classSet为空时返回null，统一返回值
        return classSet.size() > 0 ? classSet : null;
    }

    /**
     * 通过接口或者父类获取实现类或者子类的Class集合，不包括其本身
     *
     * @param interfaceOrClass 接口Class或者父类Class
     * @return Class集合
     */
    public Set<Class<?>> getClassesBySuper(Class<?> interfaceOrClass){
        // 1、获取beanMap中所有的class对象
        Set<Class<?>> keySet = beanMap.keySet();
        // keySet为空
        if (ValidationUtil.isEmpty(keySet)) {
            log.warn("beanMap为空");
            return null;
        }

        // 2、判断keySet里的元素是否是传入的接口或者类的子类，如果是，就将其添加到classSet里
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> clazz : keySet) {
            // 判断keySet里的元素是否是传入的接口或者类的子类
            if (interfaceOrClass.isAssignableFrom(clazz) && !clazz.equals(interfaceOrClass)) {
                classSet.add(clazz);
            }
        }

        // classSet为空时返回null，统一返回值
        return classSet.size() > 0 ? classSet : null;
    }
}
