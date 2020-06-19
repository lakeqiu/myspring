package org.myspringframework.mvc.processor.impl;

import org.myspringframework.mvc.RequestProcessorChain;
import org.myspringframework.mvc.processor.RequestProcessor;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

/**
 * jsp资源请求处理
 * @author lakeqiu
 */
public class JspRequestProcessor implements RequestProcessor {
    /**
     * jsp请求的RequestDispatcher的名称
     */
    private static final String JSP_SERVLET = "jsp";
    /**
     * Jsp请求资源路径前缀
     */
    private static final String  JSP_RESOURCE_PREFIX = "/templates/";

    /**
     * jsp的RequestDispatcher,处理jsp资源
     */
    private RequestDispatcher jspServlet;

    public JspRequestProcessor(ServletContext servletContext) {
        jspServlet = servletContext.getNamedDispatcher(JSP_SERVLET);
        if (null == jspServlet) {
            throw new RuntimeException("there is no jsp servlet");
        }
    }
    /**
     * 以责任链模式处理请求，返回 true 则继续调用下一个处理器处理
     *
     * @param requestProcessorChain 责任链
     * @return boolean
     * @throws Exception 处理过程中抛出的异常
     */
    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        if (isJspResource(requestProcessorChain.getRequestPath())) {
            jspServlet.forward(requestProcessorChain.getRequest(), requestProcessorChain.getResponse());
            return false;
        }
        return true;
    }

    /**
     * 是否请求的是jsp资源
     */
    private boolean isJspResource(String url) {
        return url.startsWith(JSP_RESOURCE_PREFIX);
    }
}
