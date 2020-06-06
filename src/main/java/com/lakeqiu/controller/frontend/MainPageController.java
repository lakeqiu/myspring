package com.lakeqiu.controller.frontend;

import com.lakeqiu.entity.dto.MainPageInfoDTO;
import com.lakeqiu.entity.dto.Result;
import com.lakeqiu.service.combine.HeadLineShopCategoryCombineService;
import lombok.Getter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@Controller
@Getter
public class MainPageController {
//    @Autowired(value = "HeadLineShopCategoryCombineServiceImpl")
    private HeadLineShopCategoryCombineService headLineShopCategoryCombineService;
    public Result<MainPageInfoDTO> getMainPageInfo(HttpServletRequest req, HttpServletResponse resp){
        return headLineShopCategoryCombineService.getMainPageInfo();
    }
}
