package com.lakeqiu.controller;

import com.lakeqiu.controller.frontend.MainPageController;
import com.lakeqiu.controller.superadmin.HeadLineOperationController;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/")
public class DispatcherServlet extends HttpServlet {
    @Override
    public void init(){

    }
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("request path is : " + req.getServletPath());
        System.out.println("request method is : " + req.getMethod());
        if (req.getServletPath() == "/frontend/getmainpageinfo" && req.getMethod().equals("GET")){
            new MainPageController().getMainPageInfo(req, resp);
        } else if(req.getServletPath() == "/superadmin/addheadline" && req.getMethod().equals("POST")){
            new HeadLineOperationController().addHeadLine(req, resp);
        }
    }
}
