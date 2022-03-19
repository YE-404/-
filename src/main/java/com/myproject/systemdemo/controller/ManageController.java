package com.myproject.systemdemo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myproject.systemdemo.domain.Indicator;
import com.myproject.systemdemo.domain.Log;
import com.myproject.systemdemo.domain.Search;
import com.myproject.systemdemo.domain.User;
import com.myproject.systemdemo.log.LogDemo;
import com.myproject.systemdemo.mapper.UserMapper;
import com.myproject.systemdemo.server.IndicatorServiceImpl;
import com.myproject.systemdemo.tools.DateAnalyze;
import com.myproject.systemdemo.tools.RedisTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/manager")
public class ManageController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LogDemo logDemo;
    @Autowired
    private RedisTools redisTools;
    @Autowired
    private DateAnalyze dateAnalyze;
    @Autowired
    private IndicatorServiceImpl indicatorServiceImpl;

    @Value("${userLog}")
    private String userLog;

    @RequestMapping(value = "/userMessage", method = RequestMethod.GET)
    @ResponseBody
    public String userMessage(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws IOException {
        List<User> users = new ArrayList<>();
        users = userMapper.selectAll();
        return JSON.toJSONString(users);
    }
    @RequestMapping(value = "/selectUserMessage", method = RequestMethod.POST)
    @ResponseBody
    public void selectUserMessage(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws IOException {
        BufferedReader br = req.getReader();
        String params = br.readLine();
        Search search = JSON.parseObject(params, Search.class);
        List<User> users = new ArrayList<>();
        User user = new User();
        if(search!=null) {
            String type = search.getType();
            String content = search.getContent();
            if(type.equals("id")){
                //user = userMapper.selectUserById(Integer.parseInt(content));
                user = userMapper.selectAllByCondition("id",content);
            }else if(type.equals("class")){
//                user = userMapper.selectByUsername(content);
                user = userMapper.selectAllByCondition("username",content);
            }
        }
        if(user.getUsername()!=null){
            users.add(user);
        }
        System.out.println(users);
        String jsonString = JSON.toJSONString(users);
//        return jsonString;
        res.setContentType("text/json;charset=utf-8");
        res.getWriter().write(jsonString);
    }
    @RequestMapping(value = "/updateSystemInfo", method = RequestMethod.GET)
    @ResponseBody
    public String updateSystemInfo() throws Throwable {
        int documentValue = logDemo.numberOfFiles();
        redisTools.updateHashParam("system","documentVolume",String.valueOf(documentValue));
        redisTools.updateHashParam("system","weekOfYear",dateAnalyze.weekForYear());
        System.out.println("weekForYear = " + dateAnalyze.weekForYear());
        String systemInformation = (String) redisTemplate.boundValueOps("system").get();
        if(systemInformation == null || "".equals(systemInformation)){
            System.out.println("updateSystemInfo wrong: systemInformation is null");
            return null;
            //logDemo.writeLog(String.valueOf(userId),"systemInformation is null");
        }
        return systemInformation;
    }
    @RequestMapping(value = "/updateSystemWeek", method = RequestMethod.GET)
    @ResponseBody
    public String updateSystemWeek() throws Throwable {
        String weekStr = "week-" + dateAnalyze.weekForYear();
        String param = (String) redisTemplate.boundValueOps(weekStr).get();
        System.out.println(param);
        return param;
    }

    @RequestMapping(value = "/turnPageTable", method = RequestMethod.POST)
    @ResponseBody
    public String turnPageTable(HttpServletRequest req) throws Throwable {
        BufferedReader br = req.getReader();
        String weekOfYearNo = br.readLine();
        System.out.println(weekOfYearNo);
        String weekStr = "week-" + String.valueOf(weekOfYearNo);
        String param = (String) redisTemplate.boundValueOps(weekStr).get();
        if(param == null) param = "[0,0,0,0,0,0,0]";
        System.out.println(param);
        return param;
    }

    @RequestMapping(value = "/selectByPageAndCondition", method = RequestMethod.POST)
    @ResponseBody
    public void selectByPageAndCondition(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        String _currentPage = req.getParameter("currentPage");
        String _pageSize = req.getParameter("pageSize");
        int currentPage = Integer.parseInt(_currentPage);
        int pageSize = Integer.parseInt(_pageSize);
        BufferedReader br = req.getReader();
        String params = br.readLine();
        Search search= JSON.parseObject(params, Search.class);
        System.out.println(search);
        Search<Log> searches = indicatorServiceImpl.selectLogByPageAndCondition("userlogs",currentPage,pageSize,search);
        String jsonString = JSON.toJSONString(searches);
        res.setContentType("text/json;charset=utf-8");
        res.getWriter().write(jsonString);
    }

    @RequestMapping(value = "/deleteServlet", method = RequestMethod.POST)
    @ResponseBody
    public void deleteServlet(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws IOException {
        // 获取请求体数据
        BufferedReader br = req.getReader();
        String params = br.readLine();
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = "userlogs";
        if(params!=null && !params.equals("")) {
            try{
                userMapper.deleteUserLogByUserId(searchType, Integer.valueOf(params));
                logDemo.writeLog(String.valueOf(userId),"delete one message which id = " + params + " from " + searchType + " successfully");
            }catch (Exception e){
                logDemo.writeLog(String.valueOf(userId),"delete one message which id = " + params + " from " + searchType + " failed");
                logDemo.writeLog(String.valueOf(userId), String.valueOf(e));
            }
        }
    }

    @RequestMapping(value = "/deleteIds", method = RequestMethod.POST)
    @ResponseBody
    public void deleteIds(HttpServletRequest req,HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        BufferedReader br = req.getReader();
        String params = br.readLine();
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = "userlogs";
        Integer[] ids = JSON.parseObject(params, Integer[].class);
        try {
            userMapper.deleteIds(searchType,ids);
            logDemo.writeLog(String.valueOf(userId),"delete messages from " + searchType + " successfully");
        }catch (Exception e){
            logDemo.writeLog(String.valueOf(userId),"delete messages from " + searchType + " failed");
            logDemo.writeLog(String.valueOf(userId), String.valueOf(e));
        }
        res.getWriter().write("succeed");
    }

    @RequestMapping(value = "/downloadLog", method = RequestMethod.GET)//
    public ResponseEntity downloadLog(HttpServletRequest req,HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        String params = req.getParameter("userId");
        String logName = params + ".log";
        String path = userLog + logName;
        FileSystemResource fileSystemResource = new FileSystemResource(path);//http://127.0.0.1/log/user/
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition","attachment; filename="+logName);//
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileSystemResource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(fileSystemResource.getInputStream()));
    }

}
