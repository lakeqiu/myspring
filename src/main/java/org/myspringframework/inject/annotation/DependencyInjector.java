package org.myspringframework.inject.annotation;

import lombok.extern.slf4j.Slf4j;
import org.myspringframework.core.BeanContainer;
import org.myspringframework.util.ClassUtil;
import org.myspringframework.util.ValidationUtil;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * 实现依赖注入
 * @author lakeqiu
 */
@Slf4j
public class DependencyInjector {
    /**
     * Bean容器
     */
    private BeanContainer beanContainer;
    public DependencyInjector() {
        beanContainer = BeanContainer.getInstance();
    }

    /**
     * 执行IOC（依赖注入）
     */
    public void doIoc() {

        Set<Class<?>> classes = beanContainer.getClasses();
        if (ValidationUtil.isEmpty(classes)) {
            log.warn("Bean容器为空");
            return;
        }

        // 1、遍历容器中所有的class对象
        for (Class<?> clazz : classes) {
            // 2、遍历clazz对象的所有成员变量
            Field[] fields = clazz.getDeclaredFields();
            // 该clazz没有成员变量，跳过
            if (ValidationUtil.isEmpty(fields)) {
                continue;
            }

            // 3、遍历成员变量，找出被Autowired标记的成员变量
            for (Field field : fields) {
                // 找出被Autowired标记的成员变量
                if (field.isAnnotationPresent(Autowired.class)) {
                    // 获取注解的value属性值（有多个实现类时，根据这个判断返回哪个实现类）
                    Autowired autowired = field.getAnnotation(Autowired.class);
                    String autowiredValue = autowired.value();

                    // 4、获取这些成员变量的类型
                    Class<?> fieldClass = field.getType();

                    // 5、获取这些成员变量对应的实例（由于fieldClass可能是接口，所以需要额外处理）
                    Object fieldValue = getFieldInstance(fieldClass, autowiredValue);
                    if (fieldValue == null) {
                        throw new RuntimeException("依赖注入失败，找不到类：" + fieldClass.getName() + ",autowiredValue:" + autowiredValue);
                    }

                    // 6、通过反射将成员变量实例注入到成员变量所在的类的实例中
                    Object targetBean = beanContainer.getBean(clazz);
                    ClassUtil.setField(field, targetBean, fieldValue, true);
                }
            }

        }
    }

    /**
     * 根据Class在BeanContainer中获取其实例或实现类
     * @param fieldClass Class
     * @param autowiredValue 指定注入的实现类名
     * @return Class的实例或实现类
     */
    private Object getFieldInstance(Class<?> fieldClass, String autowiredValue) {
        // 1、尝试获取Class的实例
        Object fieldValue = beanContainer.getBean(fieldClass);
        // 实例不为空，直接返回
        if (fieldValue != null) {
            return fieldValue;
        }

        // 2、Class没有实例,获取Class的实现类
        Class<?> implementClass = getImplementClass(fieldClass, autowiredValue);
        // BeanContainer中有Class的实现类，返回
        if (implementClass != null) {
            return beanContainer.getBean(implementClass);
        }

        // 没有，返回null
        return null;
    }

    /**
     * 获取接口的实现类
     * @param interfaceClass 接口
     * @param autowiredValue 指定注入的实现类名
     * @return 接口的实现类
     */
    private Class<?> getImplementClass(Class<?> interfaceClass, String autowiredValue) {
        // 获取接口在BeanContainer中是实现类
        Set<Class<?>> classSet = beanContainer.getClassesBySuper(interfaceClass);

        if (!ValidationUtil.isEmpty(classSet)) {
            // autowiredValue为空，说明没说明要注入的是哪个实现类
            if (ValidationUtil.isEmpty(autowiredValue)) {
                // 只有一个实现类，直接返回
                if (classSet.size() == 1) {
                    return classSet.iterator().next();
                } else {
                    // 如果有多个实现类且用户未指明注入哪个，则抛出异常
                    throw new RuntimeException(interfaceClass + "有多个实现类，不知道注入哪个，请指定");
                }
            } else { // autowiredValue不为空，注入用户指定的实现类
                for (Class<?> clazz : classSet) {
                    if (autowiredValue.equals(clazz.getSimpleName())) {
                        return clazz;
                    }
                }
            }
        }
        return null;
    }
}
