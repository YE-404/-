package com.myproject.systemdemo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myproject.systemdemo.domain.Log;
import com.myproject.systemdemo.domain.SaveUserLogin;
import com.myproject.systemdemo.domain.User;
//import com.myproject.service.UserService;
import com.myproject.systemdemo.log.LogDemo;
import com.myproject.systemdemo.mapper.CreatTable;
import com.myproject.systemdemo.mapper.UserMapper;
import com.myproject.systemdemo.tools.RedisTools;
import com.myproject.systemdemo.tools.DateAnalyze;
import com.myproject.systemdemo.utils.CheckCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/user")
public class UserController {

    //private UserService userService = new UserService();
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private LogDemo logDemo;

    @Autowired
    private CreatTable creatTable;

    @Autowired
    private RedisTools redisTools;

    @Autowired
    private DateAnalyze dateAnalyze;

    @Value(value = "${address}")
    private String address;

    @Value(value = "${avatarDirectory}")
    private String avatarDirectory;

    File file = new File("/home/sy/IMAGE/Avatar/");
    File[] array = file.listFiles();
    Random random = new Random();

    @RequestMapping(value = "/logincover", method = RequestMethod.GET)
    public String loginCover() throws IOException {
        return "login";
    }

    @RequestMapping(value = "/logincheck", method = RequestMethod.POST)
    @ResponseBody
    public String login(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws IOException {
        res.setContentType("text/json;charset=utf-8");
        BufferedReader br = req.getReader();
        String params = br.readLine();
        User userInput = JSON.parseObject(params, User.class);
        String username = userInput.getUsername();
        String password = userInput.getPassword();
        int succeedFlag = 0;
        if(username==null&&password==null){
            return "请输入账号密码";
        }
        User user = new User();
        String value1 = (String) redisTemplate.boundValueOps(username).get();
        if(value1 == null){
            user = userMapper.select(username,password);
            if(user == null){
                redisTemplate.boundValueOps(username).set(" ",5, TimeUnit.MINUTES);
                return "wrong";
            }
            else{
                redisTemplate.boundValueOps(username).set(user.getPassword());
                succeedFlag = 1;
            }
        }else{
            if(!value1.equals(" ")){
                user = JSON.parseObject(value1, User.class);
                if(user.getPassword().equals(password))
                    succeedFlag = 1;
            }
            else
                return "wrong";
        }
        if(succeedFlag == 1){
            Integer userId = user.getId();
            session.setAttribute("userId",userId);
            return "ok";
        }else
            return "wrong";
    }


    @RequestMapping(value = "/registercover", method = RequestMethod.GET)
    public String registercover(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        //DataSourceHolder.setDataSource("1");
        return "register";
    }
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public String register(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/json;charset=utf-8");
        BufferedReader br = req.getReader();
        String params = br.readLine();
        JSONObject jsonObj = JSON.parseObject(params);
        String username = jsonObj.getString("username");
        String password = jsonObj.getString("password");
        String checkCode = jsonObj.getString("checkCode");
        HttpSession session = req.getSession();
        String checkCodeGen = (String) session.getAttribute("checkCodeGen");
        String avatar = (String) session.getAttribute("avatar");
        if(checkCodeGen!=null&&!checkCodeGen.equalsIgnoreCase(checkCode)){
            if(username!=null&&password!=null){
                return "验证码错误";
            }
        }
        if(username == null || password == null || username.equals("") || password.equals("")){
            return "用户名或密码不可为空";
        }else if(password.equals(" ")){
            return "密码不符合规范";
        }
        User user = new User();
        String value = (String) redisTemplate.boundValueOps(username).get();
        if(value == null || value.equals(" ")){
            user = userMapper.selectAllByCondition("username",username);
        }
        User userInput = new User();
        userInput.setUsername(username);
        userInput.setPassword(password);
        userInput.setAvatarUrl(avatar);
        userInput.setLoginTimes(0);
        if(user == null) {
            redisTools.changeVisit("system","userVolume","add");    //系统登录次数+1
            userMapper.add(userInput);                                              //在数据库中加入新注册的账号
            User newUser = userMapper.select(username,password);                    //在数据库中获取该账号所有信息
            redisTemplate.boundValueOps(username).set(JSON.toJSONString(newUser));  //在redis中加入新注册的账号
            logDemo.createDatabase(newUser.getId(),username);                       //建立日志文件
            SaveUserLogin.userCount++;                                              //总用户数加1
            System.out.println("注册成功, userId = " + newUser.getId());
            return "注册成功，去登录";
        }else{
            return "该用户名已存在";
        }
    }
    @RequestMapping(value = "/registercheck", method = RequestMethod.POST)
    @ResponseBody
    public String registerCheck(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/json;charset=utf-8");
        BufferedReader br = req.getReader();
        String params = br.readLine();
        JSONObject jsonObj = JSON.parseObject(params);
        String username = jsonObj.getString("username");
        String password = jsonObj.getString("password");
        String message;

        if(username == null || username.equals("")){
            return "用户名不可为空";
        }
        String user = (String) redisTemplate.boundValueOps(username).get();
        if(user == null) {
            return "该用户名可用";
        }else{
            return "该用户名已存在";
        }
    }

    @RequestMapping(value = "/loginsucceed", method = RequestMethod.GET)
    public String loginSucceed(HttpSession session) throws Throwable {
        Integer userId = (Integer) session.getAttribute("userId");
        User user = userMapper.selectAllByCondition("id",String.valueOf(userId));
        Log log = userMapper.selectUserLogByUserId(userId);
        Integer loginTimes = user.getLoginTimes();//更新登录次数
        loginTimes ++;
        user.setLoginTimes(loginTimes);
        userMapper.updateMessageString(userId,"login_times", String.valueOf(loginTimes));
        //将登录用户信息与日志信息存入redis
        String jsonStringUser = JSON.toJSONString(user);
        String jsonStringLog = JSON.toJSONString(log);
        redisTemplate.boundValueOps("userId-" + String.valueOf(userId)).set(jsonStringUser);//将用户信息存入redis以便在登录后的页面使用
        redisTemplate.boundValueOps(String.valueOf(userId)+"-log").set(jsonStringLog);      //将日志信息存入redis以便在登录后的页面使用
        redisTools.changeVisit("system","visitorVolume","add");                 //系统访问量加一
        dateAnalyze.addLoginTimes();                                                            //对应星期访问加1
        SaveUserLogin.getMap().put(String.valueOf(userId),user.getUsername());                  //登录用户信息
        logDemo.writeLog(String.valueOf(userId),"Login in");                         //写日志
        return "redirect:/menu/hostservlet";
    }
    @RequestMapping(value = "/checkcodeservlet", method = RequestMethod.GET)
    @ResponseBody
    public void CheckCode(HttpServletRequest req, HttpServletResponse res,HttpSession session) throws IOException {
        ServletOutputStream os = res.getOutputStream();
        String checkCode = CheckCodeUtil.outputVerifyImage(100,50,os,4);
        session.setAttribute("checkCodeGen",checkCode);
        session.setAttribute("avatar","http://" + address + "/Avatar/avatar.jpg");
    }

    @RequestMapping(value = "/createPicture", method = RequestMethod.GET)
    @ResponseBody
    public void createPicture(HttpServletRequest req, HttpServletResponse res,HttpSession session) throws IOException {
        //HttpSession session = req.getSession();
        res.setContentType("text/json;charset=utf-8");
        int i = random.nextInt(array.length - 1) + 1;
        String avatar = array[i].getName();
        String avatarUrl = "http://" + address + "/Avatar/" + avatar;
        session.setAttribute("avatar",avatarUrl);
        res.getWriter().write(avatarUrl);
    }

    @RequestMapping(value = "/manageRegister", method = RequestMethod.POST)
    @ResponseBody
    public void manageRegister(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("text/json;charset=utf-8");
        BufferedReader br = req.getReader();
        String params = br.readLine();
        JSONObject jsonObj = JSON.parseObject(params);
        String username = jsonObj.getString("username");
        String password = jsonObj.getString("password");
        String checkCode = jsonObj.getString("checkCode");
        String avatar = jsonObj.getString("avatarUrl");
        String message;
        //User user = userMapper.selectByUsername(username);
        String user = userMapper.selectMesByCondition("id","username",username);
        User userInput = new User();
        userInput.setUsername(username);
        userInput.setPassword(password);
        userInput.setAvatarUrl(avatar);
        userInput.setLoginTimes(0);
        if(user == null) {
            message = "注册成功";
            redisTools.changeVisit("system","userVolume","add");
            userMapper.add(userInput);
            //Integer userId = userMapper.selectUserIdByUsername(username);
            String userId = userMapper.selectMesByCondition("id","username",username);
            System.out.println("userId = " + userId);
            logDemo.createDatabase(Integer.valueOf(userId),username);
            res.getWriter().write(message);
            return;
        }else{
            message = "该用户名已存在";
            res.getWriter().write(message);
            return;
        }
    }
    @RequestMapping(value = "/deleteServlet", method = RequestMethod.POST)
    @ResponseBody
    public String deleteServlet(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws IOException {
        //获取id值删除用户
        BufferedReader br = req.getReader();
        String params = br.readLine();
        JSONObject jsonObj = JSON.parseObject(params);
        String username = jsonObj.getString("username");
        String id = jsonObj.getString("id");
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = "users";
        String message ="";
        if(id!=null && !id.equals("")) {
            try{
                redisTools.changeVisit("system","userVolume","subtract");
                userMapper.deleteById(searchType, Integer.valueOf(id));
                logDemo.writeLog(String.valueOf(userId),"delete one message which id = " + id + " from " + searchType + " successfully");
                redisTemplate.delete(username);
                creatTable.deleteUserTable(Integer.valueOf(id));
                userMapper.deleteUserLogByUserId("userlogs",Integer.valueOf(id));
                SaveUserLogin.userCount--;
                message = "删除成功";
            }catch (Exception e){
                logDemo.writeLog(String.valueOf(userId),"delete one message which id = " + id + " from " + searchType + " failed");
                logDemo.writeLog(String.valueOf(userId), String.valueOf(e));
                message = "删除失败";
            }
        }
        return message;
    }

}
