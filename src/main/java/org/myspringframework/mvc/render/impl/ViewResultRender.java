package org.myspringframework.mvc.render.impl;

import org.myspringframework.mvc.RequestProcessorChain;
import org.myspringframework.mvc.render.ResultRender;
import org.myspringframework.mvc.type.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 页面渲染器
 * @author lakeqiu
 */
public class ViewResultRender implements ResultRender {
    /**
     * 视图路径前缀
     */
    private static final String VIEW_PATH = "/templates/";
    private ModelAndView modelAndView;

    /**
     * 对传入的参数进行处理，并赋值给ModelAndView成员变量
     * @param mv 参数
     */
    public ViewResultRender(Object mv) {
        if(mv instanceof ModelAndView){
            // 1.如果入参类型是ModelAndView，则直接赋值给成员变量
            this.modelAndView = (ModelAndView)mv;
        } else if(mv instanceof  String){
            // 2.如果入参类型是String，则为视图，需要包装后才赋值给成员变量
            this.modelAndView = new ModelAndView().setView((String)mv);
        } else {
            // 3.针对其他情况，则直接抛出异常
            throw new RuntimeException("非法返回结果类型");
        }
    }

    /**
     * 执行渲染结果
     *
     * @param requestProcessorChain 责任链实例
     * @throws Exception 渲染中出现的异常
     */
    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        HttpServletRequest request = requestProcessorChain.getRequest();
        HttpServletResponse response = requestProcessorChain.getResponse();

        // 获取视图路径与请求结果
        String path = modelAndView.getView();
        Map<String, Object> model = modelAndView.getModel();

        for(Map.Entry<String, Object> entry : model.entrySet()){
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        // 请求转发（JSP）
        request.getRequestDispatcher(VIEW_PATH +path).forward(request, response);
    }
}
