package org.myspringframework.core;

import com.lakeqiu.controller.DispatcherServlet;
import com.lakeqiu.controller.frontend.MainPageController;
import com.lakeqiu.service.solo.HeadLineService;
import com.lakeqiu.service.solo.impl.HeadLineServiceImpl;
import org.junit.jupiter.api.*;
import org.myspringframework.core.annotation.Controller;
import org.myspringframework.core.annotation.Service;

/**
 * @author lakeqiu
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BeanContainerTest {
    private static BeanContainer beanContainer;

    @BeforeAll
    static void init() {
        beanContainer = BeanContainer.getInstance();
    }

    @DisplayName("加载目标类及其实例到BeanContainer：loadBeansTest()")
    @Order(1)
    @Test
    public void loadBeansTest() {
        Assertions.assertEquals(false, beanContainer.isLoaded());
        beanContainer.loadBeans("com.lakeqiu");
        Assertions.assertEquals(7, beanContainer.size());
        Assertions.assertEquals(true, beanContainer.isLoaded());
    }

    @DisplayName("根据类获取实例：getBeanTest()")
    @Order(2)
    @Test
    public void getBeanTest() {
        MainPageController controller = (MainPageController) beanContainer.getBean(MainPageController.class);
        Assertions.assertEquals(true, controller instanceof MainPageController);

        DispatcherServlet dispatcherServlet = (DispatcherServlet) beanContainer.getBean(DispatcherServlet.class);
        Assertions.assertEquals(null, dispatcherServlet);
    }

    @DisplayName("根据注解获取对应的实例：getClassesByAnnotation()")
    @Order(3)
    @Test
    public void getClassesByAnnotation() {
        Assertions.assertEquals(true, beanContainer.isLoaded());
        Assertions.assertEquals(3, beanContainer.getClassesByAnnotation(Controller.class).size());
        Assertions.assertEquals(4, beanContainer.getClassesByAnnotation(Service.class).size());
    }

    @DisplayName("根据接口获取对应的实现类：getClassesBySuper()")
    @Order(4)
    @Test
    public void getClassesBySuper() {
        Assertions.assertEquals(true, beanContainer.isLoaded());
        Assertions.assertEquals(true, beanContainer.getClassesBySuper(HeadLineService.class).contains(HeadLineServiceImpl.class));
    }
}
