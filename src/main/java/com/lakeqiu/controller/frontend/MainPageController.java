package com.lakeqiu.controller.frontend;

import com.lakeqiu.entity.dto.MainPageInfoDTO;
import com.lakeqiu.entity.dto.Result;
import com.lakeqiu.service.combine.HeadLineShopCategoryCombineService;
import lombok.Getter;
import org.myspringframework.core.annotation.Controller;
import org.myspringframework.inject.annotation.Autowired;
import org.myspringframework.mvc.annotation.RequestMapping;
import org.myspringframework.mvc.type.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Getter
@RequestMapping(value = "/main")
public class MainPageController {
    @Autowired(value = "HeadLineShopCategoryCombineServiceImpl")
    private HeadLineShopCategoryCombineService headLineShopCategoryCombineService;
    public Result<MainPageInfoDTO> getMainPageInfo(HttpServletRequest req, HttpServletResponse resp){
        try {
            System.out.println("MainPageController的getMainPageInfo方法被执行了");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return headLineShopCategoryCombineService.getMainPageInfo();
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public void testError() {
        throw new RuntimeException("异常测试，内部出错了！！！！");
    }
}
