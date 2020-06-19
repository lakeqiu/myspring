package org.myspringframework.mvc.processor;

import org.myspringframework.mvc.RequestProcessorChain;

/**
 * 请求执行器接口
 * @author lakeqiu
 */
public interface RequestProcessor {
    /**
     * 以责任链模式处理请求，返回 true 则继续调用下一个处理器处理
     * @param requestProcessorChain 责任链
     * @return boolean
     * @throws Exception 处理过程中抛出的异常
     */
    boolean process(RequestProcessorChain requestProcessorChain) throws Exception;
}
