package org.myspringframework.inject.annotation;

import com.lakeqiu.controller.frontend.MainPageController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.myspringframework.core.BeanContainer;

/**
 * @author lakeqiu
 */
public class DependencyInjectorTest {

    @DisplayName("依赖注入doIoc")
    @Test
    public void doIocTest() {
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.lakeqiu");
        Assertions.assertEquals(true, beanContainer.isLoaded());
        MainPageController controller = (MainPageController) beanContainer.getBean(MainPageController.class);
        Assertions.assertEquals(true, controller instanceof MainPageController);
        Assertions.assertEquals(null, controller.getHeadLineShopCategoryCombineService());
        new DependencyInjector().doIoc();
        Assertions.assertNotEquals(null, controller.getHeadLineShopCategoryCombineService());
    }
}
