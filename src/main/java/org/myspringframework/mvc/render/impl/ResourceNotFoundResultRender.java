package org.myspringframework.mvc.render.impl;

import org.myspringframework.mvc.RequestProcessorChain;
import org.myspringframework.mvc.render.ResultRender;

import javax.servlet.http.HttpServletResponse;

/**
 * 资源找不到时的渲染器
 * @author lakeqiu
 */
public class ResourceNotFoundResultRender implements ResultRender {
    /**
     * 请求方法类型（GET、POST）
     */
    private String requestMethod;

    /**
     * 请求路径
     */
    private String requestPath;

    public ResourceNotFoundResultRender(String requestMethod, String requestPath) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
    }

    /**
     * 执行渲染结果
     *
     * @param requestProcessorChain 责任链实例
     * @throws Exception 渲染中出现的异常
     */
    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        String msg = "获取不到对应的请求资源，请求路径[" + requestPath + "], 请求方法类型[" + requestMethod + "]";
        requestProcessorChain.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND, msg);
    }
}
