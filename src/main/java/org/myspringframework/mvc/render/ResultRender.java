package org.myspringframework.mvc.render;

import org.myspringframework.mvc.RequestProcessorChain;

/**
 * 渲染请求结果
 * @author lakeqiu
 */
public interface ResultRender {
    /**
     * 执行渲染结果
     * @param requestProcessorChain 责任链实例
     * @throws Exception 渲染中出现的异常
     */
    void render(RequestProcessorChain requestProcessorChain) throws Exception;
}
