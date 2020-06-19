package org.myspringframework.mvc;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.myspringframework.mvc.processor.RequestProcessor;
import org.myspringframework.mvc.render.ResultRender;
import org.myspringframework.mvc.render.impl.DefaultResultRender;
import org.myspringframework.mvc.render.impl.InternalErrorResultRender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;

/**
 * 作用：
 *  1、以责任链的模式执行注册的请求处理器
 *  2、委派给特定的Render实例对处理后的结果进行渲染
 * @author lakeqiu
 */
@Data
@Slf4j
public class RequestProcessorChain {
    /**
     * 请求处理器迭代器
     */
    private Iterator<RequestProcessor> requestProcessorIterator;
    /**
     * 请求request
     */
    private HttpServletRequest request;
    /**
     * 请求response
     */
    private HttpServletResponse response;
    /**
     * http请求方法
     */
    private String requestMethod;
    /**
     * http请求路径
     */
    private String requestPath;
    /**
     * http响应状态码
     */
    private  int responseCode;
    /**
     * 请求结果渲染器
     */
    private ResultRender resultRender;

    public RequestProcessorChain(Iterator<RequestProcessor> requestProcessorIterator, HttpServletRequest request, HttpServletResponse response) {
        this.requestProcessorIterator = requestProcessorIterator;
        this.request = request;
        this.response = response;
        this.requestMethod = request.getMethod();
        this.requestPath = request.getPathInfo();
        this.responseCode = HttpServletResponse.SC_OK;
    }

    /**
     * 以责任链的模式执行请求链
     */
    public void doRequestProcessorChain() {
        try {
            // 1、通过迭代器遍历注册的请求处理器实现类列表
            while (requestProcessorIterator.hasNext()) {
                // 2、直到某个请求处理器执行后返回false为止
                if (!requestProcessorIterator.next().process(this)) {
                    break;
                }
            }

        } catch (Exception e) {
            // 3、期间如果出现异常，则交由内部异常渲染器处理
            this.resultRender = new InternalErrorResultRender(e.getMessage());
            log.error("doRequestProcessorChain 方法执行出错：", e);
        }
    }

    /**
     * 渲染结果
     */
    public void doRender() {
        // 1、如果请求处理器实现类未选用合适的渲染器，则使用默认的
        if (resultRender == null) {
            this.resultRender = new DefaultResultRender();
        }

        // 2、调用渲染器的render方法对结果进行渲染
        try {
            this.resultRender.render(this);
        } catch (Exception e) {
            log.error("doRender 方法执行出错：", e);
            throw new RuntimeException(e);
        }
    }
}
