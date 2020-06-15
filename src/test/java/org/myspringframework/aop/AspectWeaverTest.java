package org.myspringframework.aop;

import com.lakeqiu.controller.superadmin.HeadLineOperationController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myspringframework.core.BeanContainer;
import org.myspringframework.inject.annotation.DependencyInjector;

/**
 * @author lakeqiu
 */
public class AspectWeaverTest {

    @DisplayName("织入通过逻辑测试：doAop()")
    @Test
    public void doAop() {
        BeanContainer container = BeanContainer.getInstance();
        container.loadBeans("com.lakeqiu");
        new AspectWeaver().doAop();
        new DependencyInjector().doIoc();

        HeadLineOperationController controller = (HeadLineOperationController) container.getBean(HeadLineOperationController.class);
        controller.addHeadLine(null, null);
    }
}
