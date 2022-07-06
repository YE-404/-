package com.myproject.systemdemo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myproject.systemdemo.SubscribeSample;
import com.myproject.systemdemo.domain.*;
import com.myproject.systemdemo.domain.result.PointInfo;
import com.myproject.systemdemo.domain.result.Position;
import com.myproject.systemdemo.domain.result.RectMes;
import com.myproject.systemdemo.log.LogDemo;
import com.myproject.systemdemo.mapper.UserMapper;
import com.myproject.systemdemo.rabbitmq.config.RabbitMQConfig;
import com.myproject.systemdemo.server.IndicatorServiceImpl;
import com.myproject.systemdemo.server.MathCalculate;
import com.myproject.systemdemo.server.WriteTaskImpl;
import com.myproject.systemdemo.thriftDemo.ThriftClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/task")
public class TaskController {//extends thriftTools
    @Autowired
    private IndicatorServiceImpl indicatorServiceImpl;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ThriftClient client;
    @Autowired
    private LogDemo logDemo;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private WriteTaskImpl writeTask;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SubscribeSample subscribeSampleImpl;


    private String elementType = "_task";
//    ThriftClient client = new ThriftClient();

    @RequestMapping(value = "/deleteServlet", method = RequestMethod.POST)
    @ResponseBody
    public void deleteServlet(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws IOException {
        // 获取请求体数据
        BufferedReader br = req.getReader();
        String params = br.readLine();
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = userId + elementType;
        if(params!=null && !params.equals("")) {
            try{
                userMapper.deleteById(searchType, Integer.valueOf(params));
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
        String searchType = userId + elementType;
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
    @RequestMapping(value = "/selectByPage", method = RequestMethod.GET)
    @ResponseBody
    public void selectByPage(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        String _currentPage = req.getParameter("currentPage");
        String _pageSize = req.getParameter("pageSize");
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = userId + elementType;
        int currentPage = Integer.parseInt(_currentPage);
        int pageSize = Integer.parseInt(_pageSize);
        Search<Task> search = indicatorServiceImpl.selectTaskByPage(searchType,currentPage,pageSize);
        String jsonString = JSON.toJSONString(search);
        res.setContentType("text/json;charset=utf-8");
        res.getWriter().write(jsonString);
    }
    @RequestMapping(value = "/selectById", method = RequestMethod.POST)
    @ResponseBody
    public String selectById(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        BufferedReader br = req.getReader();
        String params = br.readLine();
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = userId + elementType;
        Task task = new Task();
        if(params!=null && !params.equals("")) {
            try{
                task = userMapper.selectTaskById(searchType, Integer.valueOf(params));
                logDemo.writeLog(String.valueOf(userId),"change one message which id = " + params + " in " + searchType + " successfully");

            }catch (Exception e){
                logDemo.writeLog(String.valueOf(userId),"change one message which id = " + params + " from " + searchType + " failed");
                logDemo.writeLog(String.valueOf(userId), String.valueOf(e));
            }
        }
        return JSON.toJSONString(task);
    }

    @RequestMapping(value = "/convertToPic", method = RequestMethod.POST)
    @ResponseBody
    public String editPic(HttpServletRequest req, HttpSession session) throws ServletException, IOException {
        BufferedReader br = req.getReader();
        String id = br.readLine();
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = userId + elementType;
        Task task = new Task();
        if(id != null) {
            try{
                task = userMapper.selectTaskById(searchType, Integer.valueOf(id));
                logDemo.writeLog(String.valueOf(userId),"change one message which id = " + id + " in " + searchType + " successfully");

            }catch (Exception e){
                logDemo.writeLog(String.valueOf(userId),"change one message which id = " + id + " from " + searchType + " failed");
                logDemo.writeLog(String.valueOf(userId), String.valueOf(e));
            }
        }
        return JSON.toJSONString(task);
    }

    @RequestMapping(value = "/calculate", method = RequestMethod.POST)
    @ResponseBody
    public String calculate(HttpServletRequest req, HttpSession session) throws ServletException, IOException {
        BufferedReader br = req.getReader();
        String param = br.readLine();
        JSONObject jsonObject= JSON.parseObject(param, JSONObject.class);
        String minParam = ((Map<String, Object>) jsonObject).get("min").toString();
        String middleSParam = ((Map<String, Object>) jsonObject).get("middleS").toString();
        String maxParam = ((Map<String, Object>) jsonObject).get("max").toString();
        String rectMes = ((Map<String, Object>) jsonObject).get("rectMes").toString();
        String id = ((Map<String, Object>) jsonObject).get("id").toString();
        PointInfo min = JSON.parseObject(minParam, PointInfo.class);
        PointInfo middleS = JSON.parseObject(middleSParam, PointInfo.class);
        PointInfo max = JSON.parseObject(maxParam, PointInfo.class);
        RectMes rect = JSON.parseObject(rectMes, RectMes.class);
        ClockInfo clockInfo =  MathCalculate.calculate(min,middleS,max,rect);
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = userId + elementType;
        userMapper.updateTask(searchType, clockInfo.getMinValue(),clockInfo.getMaxValue(),clockInfo.getMinAngle(),
                clockInfo.getAngleRange(),clockInfo.getCircleX(),clockInfo.getCircleY(), Integer.valueOf(id));
        return JSON.toJSONString(jsonObject);
    }

    @RequestMapping(value = "/getPathPoint", method = RequestMethod.POST)
    @ResponseBody
    public String getPathPoint(HttpServletRequest req, HttpSession session) throws ServletException, IOException, MqttException {
        Integer userId = (Integer) session.getAttribute("userId");
        SubscribeSample subscribeSample = new SubscribeSample();
        MqttClient client = subscribeSample.productClient("tcp://127.0.0.1:1883",userId + "-robot");
        subscribeSample.subscribe(client,userId + "-showPicture");
        BufferedReader br = req.getReader();
        String param = br.readLine();
        JSONObject jsonObject= JSON.parseObject(param, JSONObject.class);
        String taskName = ((Map<String, Object>) jsonObject).get("name").toString();
        if(userMapper.selectTaskByTaskName(userId + "_task",taskName) != null)
            return "wrong";
        String position = ((Map<String, Object>) jsonObject).get("currentPose").toString();
        String taskContent = ((Map<String, Object>) jsonObject).get("content").toString();

        subscribeSample.publish(client,userId + "-takePicture","true");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        System.out.println("subscribeSample.getResult() = " + subscribeSample.getResult());
        client.disconnect();
        client.close();
        boolean saveFlag = writeTask.GenerateImage(subscribeSample.getResult(),userId,date);
        if(saveFlag){
            Task task = new Task();
            task.setTaskName(taskName);
            task.setPicture(date + ".jpg");
            task.setPosition(position);
            task.setUserId(userId);
            task.setTaskContent(taskContent);
            writeTask.writeTask(task);
        }
        return "ok";
    }

    @RequestMapping(value = "/setTargetPathPoint", method = RequestMethod.POST)
    @ResponseBody
    public void setTargetPathPoint(HttpServletRequest req, HttpSession session) throws ServletException, IOException, MqttException {
        BufferedReader br = req.getReader();
        String param = br.readLine();
        Integer userId = (Integer) session.getAttribute("userId");
        SubscribeSample subscribeSample = new SubscribeSample();
        MqttClient client = subscribeSample.productClient("tcp://127.0.0.1:1883",userId + "-robot");
        subscribeSample.publish(client,userId + "-moveToPicture",param);
        client.disconnect();
        client.close();
    }
    @RequestMapping(value = "/executeOneMes", method = RequestMethod.POST)
    @ResponseBody
    public void executeOneMes(HttpServletRequest req, HttpSession session) throws ServletException, IOException, MqttException {
        BufferedReader br = req.getReader();
        String id = br.readLine();
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = userId + elementType;
        String param = userMapper.selectTaskPositionById(searchType, Integer.valueOf(id));
        SubscribeSample subscribeSample = new SubscribeSample();
        MqttClient client = subscribeSample.productClient("tcp://127.0.0.1:1883",userId + "-robot");
        subscribeSample.publish(client,userId + "-moveToPicture",param);
        client.disconnect();
        client.close();
    }

    @RequestMapping(value = "/selectAllTask", method = RequestMethod.GET)
    @ResponseBody
    public void selectAllTask(HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = userId + elementType;
        List<Task> tasks = userMapper.selectAllTask(searchType);
        String jsonString = JSON.toJSONString(tasks);
        res.setContentType("text/json;charset=utf-8");
        res.getWriter().write(jsonString);
    }

    @RequestMapping(value = "/selectTaskFlow", method = RequestMethod.GET)
    @ResponseBody
    public void selectTaskFlow(HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = userId + "_taskFlow";
        List<TaskFlow> taskFlows = userMapper.selectTaskFlow(searchType);
        String jsonString = JSON.toJSONString(taskFlows);
        res.setContentType("text/json;charset=utf-8");
        res.getWriter().write(jsonString);
    }

    @RequestMapping(value = "/addToTaskFlow", method = RequestMethod.POST)
    @ResponseBody
    public void addToTaskFlow(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        BufferedReader br = req.getReader();
        String id = br.readLine();
        System.out.println("id =" + id);
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = userId + elementType;
        String updateType = userId + "_taskFlow";
        Task task = userMapper.selectTaskById(searchType, Integer.valueOf(id));
        System.out.println(task);
        userMapper.addTaskTOFlow(updateType,task);
    }

    @RequestMapping(value = "/removeTask", method = RequestMethod.POST)
    @ResponseBody
    public void removeTask(HttpServletRequest req, HttpSession session) throws ServletException, IOException {
        BufferedReader br = req.getReader();
        String id = br.readLine();
        System.out.println("id =" + id);
        Integer userId = (Integer) session.getAttribute("userId");
        String updateType = userId + "_taskFlow";
        userMapper.deleteById(updateType, Integer.valueOf(id));
    }


    @RequestMapping(value = "/beginTaskFlow", method = RequestMethod.POST)
    @ResponseBody
    public void beginTaskFlow(HttpServletRequest req, HttpSession session) throws ServletException, IOException, MqttException {
        BufferedReader br = req.getReader();
        String param = br.readLine();
        Integer userId = (Integer) session.getAttribute("userId");

        SubscribeSample subscribeSample = new SubscribeSample();
        MqttClient client = subscribeSample.productClient("tcp://127.0.0.1:1883",userId + "-robot");
        subscribeSample.publish(client,userId + "-taskFlow",param);
        //subscribeSample.subscribe(client,userId + "-insertTable");

        //MqttClient client1 = subscribeSample.productClient("tcp://127.0.0.1:1883",userId + "-robot");
        subscribeSampleImpl.subscribe(client,userId + "-insertTable");

    }






}
