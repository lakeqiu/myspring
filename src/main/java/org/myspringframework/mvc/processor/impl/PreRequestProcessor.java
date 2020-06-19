package org.myspringframework.mvc.processor.impl;

import lombok.extern.slf4j.Slf4j;
import org.myspringframework.mvc.RequestProcessorChain;
import org.myspringframework.mvc.processor.RequestProcessor;

/**
 * 请求预处理，包括编码和路径处理
 * @author lakeqiu
 */
@Slf4j
public class PreRequestProcessor implements RequestProcessor {
    /**
     * 以责任链模式处理请求，返回 true 则继续调用下一个处理器处理
     * @param requestProcessorChain 责任链
     * @return boolean
     * @throws Exception 处理过程中抛出的异常
     */
    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        // 1.设置请求编码，将其统一设置成UTF-8
        requestProcessorChain.getRequest().setCharacterEncoding("UTF-8");
        // 2.将请求路径末尾的/剔除，为后续匹配Controller请求路径做准备
        // （一般Controller的处理路径是/aaa/bbb，所以如果传入的路径结尾是/aaa/bbb/，
        // 就需要处理成/aaa/bbb）
        String requestPath = requestProcessorChain.getRequestPath();
        //http://localhost:8080/simpleframework requestPath="/"
        if(requestPath.length() > 1 && requestPath.endsWith("/")){
            requestProcessorChain.setRequestPath(requestPath.substring(0, requestPath.length() - 1));
        }
        log.info("处理 请求 {} {}", requestProcessorChain.getRequestMethod(), requestProcessorChain.getRequestPath());
        return true;
    }
}
