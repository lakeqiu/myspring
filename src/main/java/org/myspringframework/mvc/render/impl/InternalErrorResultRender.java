package org.myspringframework.mvc.render.impl;

import org.myspringframework.mvc.RequestProcessorChain;
import org.myspringframework.mvc.render.ResultRender;

import javax.servlet.http.HttpServletResponse;

/**
 * 内部异常渲染器
 * @author lakeqiu
 */
public class InternalErrorResultRender implements ResultRender {
    /**
     * 异常信息
     */
    private String errorMsg;

    public InternalErrorResultRender(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * 执行渲染结果
     *
     * @param requestProcessorChain 责任链实例
     * @throws Exception 渲染中出现的异常
     */
    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        // 设置500错误码和错误信息即可
        requestProcessorChain.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg);
    }
}
