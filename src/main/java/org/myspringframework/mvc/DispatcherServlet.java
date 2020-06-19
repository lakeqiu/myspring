package org.myspringframework.mvc;

import org.myspringframework.aop.AspectWeaver;
import org.myspringframework.core.BeanContainer;
import org.myspringframework.inject.annotation.DependencyInjector;
import org.myspringframework.mvc.processor.RequestProcessor;
import org.myspringframework.mvc.processor.impl.ControllerRequestProcess;
import org.myspringframework.mvc.processor.impl.JspRequestProcessor;
import org.myspringframework.mvc.processor.impl.PreRequestProcessor;
import org.myspringframework.mvc.processor.impl.StaticResourceRequestProcessor;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * "/*" 拦截所有请求
 * @author lakeqiu
 */
@WebServlet("/*")
public class DispatcherServlet extends HttpServlet {

    private static List<RequestProcessor> PROCESSOR = new ArrayList<>();

    @Override
    public void init(){
        // 1、初始化容器
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.lakeqiu");
        new AspectWeaver().doAop();
        new DependencyInjector().doIoc();

        // 2、初始化请求处理器责任链
        PROCESSOR.add(new PreRequestProcessor());
        PROCESSOR.add(new StaticResourceRequestProcessor(getServletContext()));
        PROCESSOR.add(new JspRequestProcessor(getServletContext()));
        PROCESSOR.add(new ControllerRequestProcess());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        // 1、创建责任链对象实例
        RequestProcessorChain requestProcessorChain = new RequestProcessorChain(PROCESSOR.iterator(), req, resp);

        // 2、通过责任链模式来依次调用请求处理器对请求进行处理
        requestProcessorChain.doRequestProcessorChain();

        // 3、对处理结进行渲染
        requestProcessorChain.doRender();
    }
}
