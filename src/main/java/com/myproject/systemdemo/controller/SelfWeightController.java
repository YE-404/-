package com.myproject.systemdemo.controller;

import com.alibaba.fastjson.JSON;
import com.myproject.systemdemo.domain.FileHandleResponse;
import com.myproject.systemdemo.domain.Search;
import com.myproject.systemdemo.domain.WeightMes;
import com.myproject.systemdemo.mapper.UserMapper;
import com.myproject.systemdemo.server.IndicatorServiceImpl;
import com.myproject.systemdemo.tools.ZipUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/selfWeight")
public class SelfWeightController {
    @Autowired
    private IndicatorServiceImpl indicatorServiceImpl;
    @Autowired
    private UserMapper userMapper;
    @Value("${weightStore}")
    private String weightStore;
    @Value("${netWeightStore}")
    private String netWeightStore;
    @Value("${weightTempStore}")
    private String weightTempStore;

    private String elementType = "_weights";

    @RequestMapping(value = "/selectByPageAndCondition", method = RequestMethod.POST)
    @ResponseBody
    public String selectByPageAndCondition(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = userId + elementType;
        String _currentPage = req.getParameter("currentPage");
        String _pageSize = req.getParameter("pageSize");
        int currentPage = Integer.parseInt(_currentPage);
        int pageSize = Integer.parseInt(_pageSize);
        BufferedReader br = req.getReader();
        String params = br.readLine();
        Search search= JSON.parseObject(params, Search.class);
        Search<WeightMes> searches = indicatorServiceImpl.selectWeightMesByPageAndCondition(searchType,currentPage,pageSize,search);
        String jsonString = JSON.toJSONString(searches);
        return jsonString;
    }

    @RequestMapping(value = "/deleteWeightById", method = RequestMethod.POST)
    @ResponseBody
    public String deleteWeightById(String id, HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = userId + elementType;
        if( id != null && !"".equals(id)){
            System.out.println("id = " + id);
            try{
                userMapper.deleteWeightById(searchType,Integer.parseInt(id));
                return "ok";
            }catch (Exception e){
                System.out.println(e.getMessage());
                return "删除失败";
            }
        }else{
            return "id = null";
        }
    }

    @RequestMapping(value = "/useWeightById", method = RequestMethod.POST)
    @ResponseBody
    public String useWeightById(String id,HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = userId + elementType;
        String category = userMapper.selectWeightMesById("category",searchType,"id",id);
        try{
            userMapper.setState(searchType,"state",false,"category",category);
            userMapper.setState(searchType,"state",true,"id",id);
            return "ok";
        }catch(Exception e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/showSelfWeight", method = RequestMethod.POST)
    @ResponseBody
    public void showSelfWeight(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = userId + elementType;
        String _currentPage = req.getParameter("currentPage");
        String _pageSize = req.getParameter("pageSize");
        int currentPage = Integer.parseInt(_currentPage);
        int pageSize = Integer.parseInt(_pageSize);
        BufferedReader br = req.getReader();
        String params = br.readLine();
        Search search= JSON.parseObject(params, Search.class);
        Search<WeightMes> searches = indicatorServiceImpl.selectWeightMesByPageAndCondition(searchType,currentPage,pageSize,search);
        String jsonString = JSON.toJSONString(searches);
        res.setContentType("text/json;charset=utf-8");
        res.getWriter().write(jsonString);
    }





}
