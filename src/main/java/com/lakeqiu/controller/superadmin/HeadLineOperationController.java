package com.lakeqiu.controller.superadmin;

import com.lakeqiu.entity.bo.HeadLine;
import com.lakeqiu.entity.dto.Result;
import com.lakeqiu.service.solo.HeadLineService;
import org.myspringframework.core.annotation.Controller;
import org.myspringframework.inject.annotation.Autowired;
import org.myspringframework.mvc.annotation.RequestMapping;
import org.myspringframework.mvc.annotation.RequestParam;
import org.myspringframework.mvc.annotation.ResponseBody;
import org.myspringframework.mvc.type.ModelAndView;
import org.myspringframework.mvc.type.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping(value = "/headLine")
public class HeadLineOperationController {
    @Autowired
    private HeadLineService headLineService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ModelAndView addHeadLine(@RequestParam("lineName") String lineName,
                                    @RequestParam("lineLink")String lineLink,
                                    @RequestParam("lineImg")String lineImg,
                                    @RequestParam("priority")Integer priority){
        System.out.println("addHeadLine方法被调用了");
        HeadLine headLine = new HeadLine();
        headLine.setLineName(lineName);
        headLine.setLineLink(lineLink);
        headLine.setLineImg(lineImg);
        headLine.setPriority(priority);
        Result<Boolean> result = headLineService.addHeadLine(headLine);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView("addheadline.jsp").addViewData("result", result);
        return modelAndView;
    }

    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    public void removeHeadLine(){
        System.out.println("删除remove");
    }
    public Result<Boolean> modifyHeadLine(HttpServletRequest req, HttpServletResponse resp){
        System.out.println("modifyHeadLine方法被调用了");
        return headLineService.modifyHeadLine(new HeadLine());
    }
    public Result<HeadLine> queryHeadLineById(HttpServletRequest req, HttpServletResponse resp){
        return headLineService.queryHeadLineById(1);
    }

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @ResponseBody
    public Result<List<HeadLine>>queryHeadLine(){
        return headLineService.queryHeadLine(null, 1, 100);
    }


}
