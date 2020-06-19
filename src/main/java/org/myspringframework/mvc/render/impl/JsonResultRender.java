package org.myspringframework.mvc.render.impl;

import com.google.gson.Gson;
import org.myspringframework.mvc.RequestProcessorChain;
import org.myspringframework.mvc.render.ResultRender;

import java.io.PrintWriter;

/**
 * Json渲染器
 * @author lakeqiu
 */
public class JsonResultRender implements ResultRender {
    /**
     * 返回给客户端的结果
     */
    private Object jsonData;

    public JsonResultRender(Object jsonData) {
        this.jsonData = jsonData;
    }

    /**
     * 执行渲染结果
     *
     * @param requestProcessorChain 责任链实例
     * @throws Exception 渲染中出现的异常
     */
    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        // 设置响应头
        requestProcessorChain.getResponse().setContentType("application/json");
        requestProcessorChain.getResponse().setCharacterEncoding("UTF-8");

        // 响应流写入经过Gson处理后的结果
        try(PrintWriter writer = requestProcessorChain.getResponse().getWriter()) {
            final Gson gson = new Gson();
            writer.write(gson.toJson(jsonData));
            writer.flush();
        }
    }
}
