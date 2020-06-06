package org.myspringframework.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * @author lakeqiu
 */
public class ClassUtilTest {

    @DisplayName("提取目标类方法：extractPackageClassTest()")
    @Test
    public void extractPackageClassTest() {
        Set<Class<?>> classSet = ClassUtil.extractPackageClass("com.lakeqiu.entity");
        System.out.println(classSet);
        Assertions.assertEquals(4, classSet.size());
    }
}
