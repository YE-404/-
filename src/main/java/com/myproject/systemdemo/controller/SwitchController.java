package com.myproject.systemdemo.controller;

import com.alibaba.fastjson.JSON;

import com.myproject.systemdemo.domain.Indicator;
import com.myproject.systemdemo.domain.Search;
import com.myproject.systemdemo.log.LogDemo;
import com.myproject.systemdemo.mapper.UserMapper;
import com.myproject.systemdemo.server.IndicatorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/switch")
public class SwitchController {
//    private IndicatorService lightService = new IndicatorService();
    @Autowired
    private IndicatorServiceImpl indicatorServiceImpl;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LogDemo logDemo;

    private String elementType = "_switchs";

    @RequestMapping(value = "/deleteServlet", method = RequestMethod.POST)
    @ResponseBody
    public void deleteServlet(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws IOException {
        // 获取请求体数据
        BufferedReader br = req.getReader();
        String params = br.readLine();
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = String.valueOf(userId) + elementType;
        if(params!=null && params != "") {
            try{
                userMapper.deleteById(searchType, Integer.valueOf(params));
                logDemo.writeLog(String.valueOf(userId),"delete one message which id = " + params + " from " + searchType + " successfully");
            }catch (Exception e){
                logDemo.writeLog(String.valueOf(userId),"delete one message which id = " + params + " from " + searchType + " failed");
                logDemo.writeLog(String.valueOf(userId), String.valueOf(e));
            }
        }
    }
    @RequestMapping(value = "/searchServlet", method = RequestMethod.POST)
    @ResponseBody
    public void searchServlet(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        // 获取请求体数据
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = String.valueOf(userId) + elementType;
        BufferedReader br = req.getReader();
        String params = br.readLine();
        Search search = JSON.parseObject(params, Search.class);
        List<Indicator> indicators = null;
        if(search!=null) {
            String type = search.getType();
            String content = search.getContent();
            if(type.equals("id")){
                indicators = userMapper.selectByIdIndicator(searchType, Integer.valueOf(content));
            }else if(type.equals("class")){
                content = "%" + content + "%";
                indicators = userMapper.selectByClassIndicator(searchType, content);
            }
        }
        String jsonString = JSON.toJSONString(indicators);
        res.setContentType("text/json;charset=utf-8");
        res.getWriter().write(jsonString);
    }

    @RequestMapping(value = "/deleteIds", method = RequestMethod.POST)
    @ResponseBody
    public void deleteIds(HttpServletRequest req,HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = String.valueOf(userId) + elementType;
        BufferedReader br = req.getReader();
        String params = br.readLine();
        System.out.println(params);
        Integer[] ids = JSON.parseObject(params, Integer[].class);
        System.out.println(ids);
        try {
            userMapper.deleteIds(searchType,ids);
            logDemo.writeLog(String.valueOf(userId),"delete messages from " + searchType + " successfully");
        }catch (Exception e){
            logDemo.writeLog(String.valueOf(userId),"delete messages from " + searchType + " failed");
            logDemo.writeLog(String.valueOf(userId), String.valueOf(e));
        }
        res.getWriter().write("succeed");
    }
    @RequestMapping(value = "/selectByPage", method = RequestMethod.GET)
    @ResponseBody
    public void selectByPage(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = String.valueOf(userId) + elementType;
        String _currentPage = req.getParameter("currentPage");
        String _pageSize = req.getParameter("pageSize");
        int currentPage = Integer.valueOf(_currentPage);
        int pageSize = Integer.valueOf(_pageSize);
        Search<Indicator> search = indicatorServiceImpl.selectByPage(searchType,currentPage,pageSize);
        System.out.println(search);
        String jsonString = JSON.toJSONString(search);
        res.setContentType("text/json;charset=utf-8");
        res.getWriter().write(jsonString);
    }
    @RequestMapping(value = "/selectByPageAndCondition", method = RequestMethod.POST)
    @ResponseBody
    public void selectByPageAndCondition(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = String.valueOf(userId) + elementType;
        String _currentPage = req.getParameter("currentPage");
        String _pageSize = req.getParameter("pageSize");
        int currentPage = Integer.valueOf(_currentPage);
        int pageSize = Integer.valueOf(_pageSize);
        BufferedReader br = req.getReader();
        String params = br.readLine();
        Search search= JSON.parseObject(params, Search.class);
        Search<Indicator> searches = indicatorServiceImpl.selectByPageAndCondition(searchType,currentPage,pageSize,search);
        String jsonString = JSON.toJSONString(searches);
        res.setContentType("text/json;charset=utf-8");
        res.getWriter().write(jsonString);
    }






}
