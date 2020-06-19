package org.myspringframework.mvc.type;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 存储完处理后的结果，以及显示该数据的视图
 * @author lakeqiu
 */
public class ModelAndView {
    /**
     * 页面所在的路径
     */
    @Getter
    private String view;

    /**
     * 页面的Data数据
     */
    @Getter
    private Map<String, Object> model = new HashMap<>();

    public ModelAndView setView(String view) {
        this.view = view;
        return this;
    }

    /**
     * 返回本身是为了能这样使用 modelAndView.setView("addheadline.jsp").addViewData("aaa", "bbb");
     * @param attributeName
     * @param attributeValue
     * @return
     */
    public ModelAndView addViewData(String attributeName, Object attributeValue) {
        model.put(attributeName, attributeValue);
        return this;
    }
}
