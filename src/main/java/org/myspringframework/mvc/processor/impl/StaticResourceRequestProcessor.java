package org.myspringframework.mvc.processor.impl;

import lombok.extern.slf4j.Slf4j;
import org.myspringframework.mvc.RequestProcessorChain;
import org.myspringframework.mvc.processor.RequestProcessor;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

/**
 * 静态资源请求处理，包含但不限与图片、css以及js文件等
 * @author lakeqiu
 */
@Slf4j
public class StaticResourceRequestProcessor implements RequestProcessor {
    public static final String DEFAULT_TOMCAT_SERVLET = "default";
    /**
     * 静态资源前缀
     */
    public static final String STATIC_RESOURCE_PREFIX = "/static/";
    /**
     * tomcat默认请求派发器RequestDispatcher的名称
     */
    RequestDispatcher defaultDispatcher;

    public StaticResourceRequestProcessor(ServletContext servletContext) {
        this.defaultDispatcher = servletContext.getNamedDispatcher(DEFAULT_TOMCAT_SERVLET);
        if(this.defaultDispatcher == null){
            throw new RuntimeException("There is no default tomcat servlet");
        }
        log.info("The default servlet for static resource is {}", DEFAULT_TOMCAT_SERVLET);
    }

    /**
     * 以责任链模式处理请求，返回 true 则继续调用下一个处理器处理
     * @param requestProcessorChain 责任链
     * @return boolean
     * @throws Exception 处理过程中抛出的异常
     */
    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        // 1.通过请求路径判断是否是请求的静态资源 webapp/static
        if(isStaticResource(requestProcessorChain.getRequestPath())){
            // 2.如果是静态资源，则将请求转发给default servlet处理
            defaultDispatcher.forward(requestProcessorChain.getRequest(), requestProcessorChain.getResponse());
            return false;
        }
        return true;
    }


    /**
     * 通过请求路径前缀（目录）是否为静态资源 /static/
     * @param path 判断请求是否是请求静态资源
     * @return Boolean
     */
    private boolean isStaticResource(String path){
        return path.startsWith(STATIC_RESOURCE_PREFIX);
    }
}
