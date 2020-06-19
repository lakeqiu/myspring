package org.myspringframework.mvc.render.impl;

import org.myspringframework.mvc.RequestProcessorChain;
import org.myspringframework.mvc.render.ResultRender;

/**
 * 默认渲染器
 * @author lakeqiu
 */
public class DefaultResultRender implements ResultRender {
    /**
     * 执行渲染结果
     *
     * @param requestProcessorChain 责任链实例
     * @throws Exception 渲染中出现的异常
     */
    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        // 请求执行成功，设置想要状态码200即可
        requestProcessorChain.getResponse().setStatus(requestProcessorChain.getResponseCode());
    }
}
