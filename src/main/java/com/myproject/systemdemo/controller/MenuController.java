package com.myproject.systemdemo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myproject.systemdemo.config.DataSourceHolder;
import com.myproject.systemdemo.domain.SaveUserLogin;
import com.myproject.systemdemo.domain.Search;
import com.myproject.systemdemo.domain.User;
import com.myproject.systemdemo.domain.UserManage;
import com.myproject.systemdemo.log.LogDemo;
import com.myproject.systemdemo.mapper.CreatTable;
import com.myproject.systemdemo.mapper.UserMapper;
import com.myproject.systemdemo.tools.RedisTools;
import org.apache.thrift.transport.TSocket;
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
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.sql.SQLException;

@Controller
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LogDemo logDemo;

    @Autowired
    private RedisTools redisTools;

    @RequestMapping(value = "/elementTry", method = RequestMethod.GET)
    public String elementTry(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        return "elementTry";
    }

    @RequestMapping(value = "/logmanage", method = RequestMethod.GET)
    public String logManage(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        return "LogManage";
    }
    @RequestMapping(value = "/manageservlet", method = RequestMethod.GET)
    public String emptyServlet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
        return "manage";
    }
    @RequestMapping(value = "/hostservlet", method = RequestMethod.GET)
    public String hostServlet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException , SQLException {
        return "host";
    }
    @RequestMapping(value = "/spellservlet", method = RequestMethod.GET)
    public String spellServlet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException , SQLException {
        return "spell";
    }
    @RequestMapping(value = "/weightServlet", method = RequestMethod.GET)
    public String weightServlet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException , SQLException {
        return "weightStore";
    }
    @RequestMapping(value = "/weightSelf", method = RequestMethod.GET)
    public String weightSelf(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException , SQLException {
        return "weightSelf";
    }

    @RequestMapping(value = "/hostdataservlet", method = RequestMethod.GET)
    @ResponseBody
    public void hostDataServlet(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException , SQLException {
        Integer userId = (Integer) session.getAttribute("userId");
        String user = (String) redisTemplate.boundValueOps("userId-" + String.valueOf(userId)).get();
        if(user == null || "".equals(user)){
            System.out.println("hostdataservlet wrong: user is null");
            logDemo.writeLog(String.valueOf(userId),"user is null");
        }
        res.setContentType("text/json;charset=utf-8");
        res.getWriter().write(user);
        return;
    }

    @RequestMapping(value = "/onlineservlet", method = RequestMethod.GET)
    @ResponseBody
    public String onlineServlet(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException , SQLException {
        int totalSize = SaveUserLogin.userCount;
        int onlineSize = SaveUserLogin.getMap().size();
        UserManage userManage = new UserManage();
        userManage.setOnlineSize(onlineSize);
        userManage.setOfflineSize(totalSize - onlineSize);
        return JSON.toJSONString(userManage);
    }

    @RequestMapping(value = "/lighttable", method = RequestMethod.GET)
    public String lightTableServlet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        return "tableLight";
    }

    @RequestMapping(value = "/switchtable", method = RequestMethod.GET)
    public String switchTableServlet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        return "tableSwitch";
    }
    @RequestMapping(value = "/numbertable", method = RequestMethod.GET)
    public String numberTableServlet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        return "tableNumber";
    }
    @RequestMapping(value = "/pointtable", method = RequestMethod.GET)
    public String pointTableServlet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        return "tablePoint";
    }
    @RequestMapping(value = "/tasktable", method = RequestMethod.GET)
    public String taskTableServlet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        return "tableTask";
    }


    @RequestMapping(value = "/lightcheck", method = RequestMethod.GET)
    public String lightCheckServlet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        return "uploadLight";
    }

    @RequestMapping(value = "/clockcheck", method = RequestMethod.GET)
    public String clockCheckServlet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        return "uploadClock";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String profile(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        return "profile";
    }

    @RequestMapping(value = "/profilelog", method = RequestMethod.GET)
    @ResponseBody
    public void profileLog(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        Integer userId = (Integer) session.getAttribute("userId");
        String param = (String) redisTemplate.boundValueOps((userId)+"-log").get();
        res.setContentType("text/json;charset=utf-8");
        res.getWriter().write(param);
    }


    @RequestMapping(value = "/profilechange", method = RequestMethod.POST)
    @ResponseBody
    public String profileChange(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        BufferedReader br = req.getReader();
        String params = br.readLine();
        JSONObject object = (JSONObject) JSONObject.parse(params);
        String message = "";
        String username = object.getString("username");
        String oriPassword = object.getString("oriPassword");
        String newPassword = object.getString("newPassword");
        Integer userId = (Integer) session.getAttribute("userId");
        User user = userMapper.selectAllByCondition("id",String.valueOf(userId));
        String myUsername = user.getUsername();
        String myPassword = user.getPassword();
        if(!username.equals(myUsername)){
            return "usernameWrong";
        }else if(!oriPassword.equals(myPassword)){
            return "oriPasswordWrong";
        }else if(newPassword.equals(oriPassword)){
            return "newSameOri";
        }else if(newPassword == null || "".equals(newPassword)){
            return "newPasswordEmpty";
        }else{
            boolean redisFlag = redisTools.updateUserPassword(myUsername,newPassword);
            if(redisFlag == true){
                userMapper.updateMessageString(userId,"password",newPassword);
                return "ok";
            }else
                return "wrong";
        }
    }
    @RequestMapping("/download")
    public ResponseEntity download(HttpSession session) throws IOException {
        Integer userId = (Integer) session.getAttribute("userId");
        String logName = String.valueOf(userId) + ".log";
        FileSystemResource fileSystemResource = new FileSystemResource(logDemo + logName);//http://127.0.0.1/log/user/
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition","attachment; filename="+logName);//
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileSystemResource.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(fileSystemResource.getInputStream()));
    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logOut(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        session.invalidate();
        res.sendRedirect(req.getContextPath() + "/user/logincover");
    }
}
