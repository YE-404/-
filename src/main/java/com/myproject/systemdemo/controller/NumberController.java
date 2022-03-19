package com.myproject.systemdemo.controller;

import com.alibaba.fastjson.JSON;

import com.myproject.systemdemo.domain.Indicator;
import com.myproject.systemdemo.domain.Search;
import com.myproject.systemdemo.domain.Upload;
import com.myproject.systemdemo.log.LogDemo;
import com.myproject.systemdemo.mapper.UserMapper;
import com.myproject.systemdemo.server.IndicatorServiceImpl;
import com.myproject.systemdemo.thriftDemo.ThriftClient;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/number")
public class NumberController {
    @Autowired
    private IndicatorServiceImpl indicatorServiceImpl;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LogDemo logDemo;

    @Autowired
    private ThriftClient client;

    private String elementType = "_number_clocks";

    @RequestMapping(value = "/deleteServlet", method = RequestMethod.POST)
    @ResponseBody
    public void deleteServlet(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws IOException {
        // 获取请求体数据
        BufferedReader br = req.getReader();
        String params = br.readLine();
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = String.valueOf(userId) + elementType;

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

//    @RequestMapping(value = "/showServlet", method = RequestMethod.POST)
//    @ResponseBody
//    public void showServlet(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
//        Integer userId = (Integer) session.getAttribute("userId");
//        String searchType = String.valueOf(userId) + elementType;
//        List<Indicator> indicators = userMapper.selectAllIndicator(searchType);
//        String jsonString = JSON.toJSONString(indicators);
//        res.setContentType("text/json;charset=utf-8");
//        res.getWriter().write(jsonString);
//    }
    @RequestMapping(value = "/deleteIds", method = RequestMethod.POST)
    @ResponseBody
    public void deleteIds(HttpServletRequest req,HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = String.valueOf(userId) + elementType;
        BufferedReader br = req.getReader();
        String params = br.readLine();
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
        System.out.println(search);
        Search<Indicator> searches = indicatorServiceImpl.selectByPageAndCondition(searchType,currentPage,pageSize,search);
        String jsonString = JSON.toJSONString(searches);
        res.setContentType("text/json;charset=utf-8");
        res.getWriter().write(jsonString);
    }
    @RequestMapping(value = "/analyzeClock", method = RequestMethod.POST)
    @ResponseBody
    public String analyzeClock(@RequestParam("file") MultipartFile uploadFile, Upload upload, HttpServletResponse res, HttpSession session)  throws ServletException, IOException{
        //String data = "";
        String getResult1 = "";
        String getResult2 = "";
        List<String> getResult = new ArrayList<>();
        String class_ = "";
        Integer userId = (Integer) session.getAttribute("userId");
        String searchTypeNumber = userId + "_number_clocks";
        String searchTypePoint = userId + "_point_clocks";
        String searchType = "";
        List<Indicator> indicatorsTotal = new ArrayList<>();
        if (!uploadFile.isEmpty()) {
            BASE64Encoder encoder = new BASE64Encoder();
            // 通过base64来转化图片
            String data = encoder.encode(uploadFile.getBytes());
            String optionsRadios = upload.getOptionsRadios();
            String[] checkedNumbers = upload.getCheckedNumbers();
            String[] checkedPoints = upload.getCheckedPoints();//checkedSwitches
            if(checkedNumbers != null){
                getResult1 = client.getResult(data,"number",checkedNumbers,String.valueOf(userId));
                if(getResult1.equals("NoIndicator") || getResult1.equals("NoInside")) return "lackWeight";
            }
            if(checkedPoints != null){
                getResult2 = client.getResult(data,"point",checkedPoints,String.valueOf(userId));
            }

            getResult.add(getResult1);
            getResult.add(getResult2);
            for(int i=0; i<getResult.size(); i++) {//
                if(getResult.get(i) == null || "".equals(getResult.get(i))) continue;
                if(i == 0){ searchType = searchTypeNumber; }
                else if(i == 1){ searchType = searchTypePoint; }
                System.out.println("getResult.get(i):" + getResult.get(i));
                Indicator[] indicators = JSON.parseObject(getResult.get(i), Indicator[].class);
                for(Indicator u : indicators)
                    System.out.println(u);
                if(indicators.length == 1 && indicators[0].getClass_() == null) continue;
                for(Indicator j : indicators){
                    indicatorsTotal.add(j);
                    class_ = j.getClass_();

                    logDemo.writeLog(String.valueOf(userId),"detect " + "[ Clock ]" + " - " + class_+ " - " + j.getResult());
                    if("yes".equals(optionsRadios)){
                        try{
                            userMapper.addClockIndicator(searchType,class_,j.getResult());
                            logDemo.writeLog(String.valueOf(userId),"insert " + "[" + searchType + "]" + " - " + class_+ " - " + j.getResult());
                        }catch (Exception e){
                            logDemo.writeLog(String.valueOf(userId),"insert " + "[" + searchType + "]" + " failed");
                            logDemo.writeLog(String.valueOf(userId), String.valueOf(e));
                        }
                    }
                }
            }
            String jsonString = JSON.toJSONString(indicatorsTotal);
            return jsonString;
        }
        return "noFile";
    }



}
