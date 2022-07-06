package com.myproject.systemdemo.controller;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;

//import com.myproject.service.IndicatorService;
import com.alibaba.fastjson.JSONObject;
import com.myproject.systemdemo.config.DataSourceHolder;
import com.myproject.systemdemo.domain.Indicator;
import com.myproject.systemdemo.domain.Search;
import com.myproject.systemdemo.domain.Upload;
import com.myproject.systemdemo.log.LogDemo;
import com.myproject.systemdemo.mapper.UserMapper;
import com.myproject.systemdemo.rabbitmq.config.RabbitMQConfig;
import com.myproject.systemdemo.server.IndicatorService;
import com.myproject.systemdemo.server.IndicatorServiceImpl;
import com.myproject.systemdemo.thriftDemo.ThriftClient;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/light")
public class LightController {//extends thriftTools

    @Autowired
    private IndicatorServiceImpl indicatorServiceImpl;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ThriftClient client;

    @Autowired
    private LogDemo logDemo;

    @Autowired
    AmqpTemplate amqpTemplate;

    private String elementType = "_lights";

    @RequestMapping(value = "/deleteServlet", method = RequestMethod.POST)
    @ResponseBody
    public void deleteServlet(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws IOException {
        // 获取请求体数据
        BufferedReader br = req.getReader();
        String params = br.readLine();
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = userId + elementType;
        if(params!=null && params != "") {
            try{
                userMapper.deleteById(searchType, Integer.valueOf(params));
                logDemo.writeLog(String.valueOf(userId),"delete one message which id = " + params + " from " + searchType + " successfully");
            }catch (Exception e){
                logDemo.writeLog(String.valueOf(userId),"delete one message which id = " + params + " from " + searchType + " failed");
                logDemo.writeLog(String.valueOf(userId), String.valueOf(e));
            }
        }
//        List<Indicator> indicators = userMapper.selectAllIndicator(searchType);
//        String jsonString = JSON.toJSONString(indicators);
//        res.setContentType("text/json;charset=utf-8");
//        res.getWriter().write(jsonString);
    }
    @RequestMapping(value = "/searchServlet", method = RequestMethod.POST)
    @ResponseBody
    public void searchServlet(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        // 获取请求体数据
        BufferedReader br = req.getReader();
        String params = br.readLine();
        Search search = JSON.parseObject(params, Search.class);
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = String.valueOf(userId) + elementType;
        List<Indicator> indicators = null;
        if(search!=null) {
            String type = search.getType();
            String content = search.getContent();
            if(type.equals("id")){
                indicators = indicatorServiceImpl.getUserMapper().selectByIdIndicator(searchType, Integer.valueOf(content));
            }else if(type.equals("class")){
                content = "%" + content + "%";
                indicators = indicatorServiceImpl.getUserMapper().selectByClassIndicator(searchType, content);
            }
        }
        String jsonString = JSON.toJSONString(indicators);
        res.setContentType("text/json;charset=utf-8");
        res.getWriter().write(jsonString);
    }

    @RequestMapping(value = "/deleteIds", method = RequestMethod.POST)
    @ResponseBody
    public void deleteIds(HttpServletRequest req,HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        BufferedReader br = req.getReader();
        String params = br.readLine();
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = String.valueOf(userId) + elementType;
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
        //DataSourceHolder.setDataSource("2");
        String _currentPage = req.getParameter("currentPage");
        String _pageSize = req.getParameter("pageSize");
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = String.valueOf(userId) + elementType;
        int currentPage = Integer.valueOf(_currentPage);
        int pageSize = Integer.valueOf(_pageSize);
        Search<Indicator> search = indicatorServiceImpl.selectByPage(searchType,currentPage,pageSize);
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
    @RequestMapping(value = "/analyzeLight", method = RequestMethod.POST)
    @ResponseBody
    public String analyzeLight(@RequestParam("file") MultipartFile uploadFile,  Upload upload, HttpSession session)  throws ServletException, IOException{
        //String data = "";

        System.out.println(upload);
        System.out.println("file is empty?  " + uploadFile.isEmpty());
        String getResult1 = "";
        List<String> getResult = new ArrayList<>();
        StringBuilder class_ = new StringBuilder();
        //Integer userId = (Integer) session.getAttribute("userId");
        Integer userId = 15;
        String searchTypeLight = userId + "_lights";
        String searchTypeSwitch = userId + "_switchs";
        String searchType = "";
        List<Indicator> indicatorsTotal = new ArrayList<>();
        if (!uploadFile.isEmpty()) {

            BASE64Encoder encoder = new BASE64Encoder();
            // 通过base64来转化图片
            String data = encoder.encode(uploadFile.getBytes());
            System.out.println("ready");
            String optionsRadios = upload.getOptionsRadios();
            String[] checkedLights = upload.getCheckedLights();
            String[] checkedSwitches = upload.getCheckedSwitches();//checkedSwitches

            if(checkedSwitches != null&&checkedLights != null){
                String[] list = new String[checkedSwitches.length + checkedLights.length];
                System.arraycopy(checkedSwitches, 0, list, 0, checkedSwitches.length);
                System.arraycopy(checkedLights, 0, list, checkedSwitches.length, checkedLights.length);
                System.out.println(Arrays.toString(list));
                getResult1 = client.getResult(data,"switch+light",list,String.valueOf(userId));
                if(getResult1.equals("NoIndicator") || getResult1.equals("NoInside")) return "lackWeight";
            }else if(checkedLights != null){
                getResult1 = client.getResult(data,"light",checkedLights,String.valueOf(userId));
            }else if(checkedSwitches != null){
                getResult1 = client.getResult(data,"switch",checkedSwitches,String.valueOf(userId));
            }

            getResult.add(getResult1);
            for(int i=0;i<getResult.size();i++) {
                class_ = new StringBuilder();
                if(i == 0){ searchType = searchTypeLight; }
                else if(i == 1){ searchType = searchTypeSwitch; }
                Indicator[] indicators = JSON.parseObject(getResult.get(i), Indicator[].class);
                if(indicators.length == 1 && indicators[0].getClass_() == null) continue;
                for(Indicator j : indicators){
                    indicatorsTotal.add(j);
                    class_.append(j.getClass_()).append(" ");
                }
                logDemo.writeLog(String.valueOf(userId),"detect " + "[ Indicator ]" + " - " + class_);
                if("yes".equals(optionsRadios)){
                    amqpTemplate.convertAndSend(RabbitMQConfig.QUEUE, "insertTry");//RabbitMQConfig.QUEUE
                    try{
                        userMapper.addIndicator(searchType, class_.toString());
                        logDemo.writeLog(String.valueOf(userId),"insert " + "[" + searchType + "]" + " - " + class_);
                    }catch (Exception e){
                        logDemo.writeLog(String.valueOf(userId),"insert " + "[" + searchType + "]" + " failed");
                        logDemo.writeLog(String.valueOf(userId), String.valueOf(e));
                    }
                }
            }
            return JSON.toJSONString(indicatorsTotal);
        }
        return "noFile";
    }


}
