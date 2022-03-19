package com.myproject.systemdemo.controller;

import com.alibaba.fastjson.JSON;
import com.myproject.systemdemo.domain.FileHandleResponse;
import com.myproject.systemdemo.domain.Indicator;
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
@RequestMapping("/weight")
public class WeightController {
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

    @RequestMapping(value = "/selectByPage", method = RequestMethod.GET)
    @ResponseBody
    public void selectByPage(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        System.out.println("in selectByPage");
        String _currentPage = req.getParameter("currentPage");
        String _pageSize = req.getParameter("pageSize");
        Integer userId = (Integer) session.getAttribute("userId");
        int currentPage = Integer.parseInt(_currentPage);
        int pageSize = Integer.parseInt(_pageSize);
        Search<WeightMes> search = indicatorServiceImpl.selectWeightMesByPage("weights",currentPage,pageSize);
        String jsonString = JSON.toJSONString(search);
        res.setContentType("text/json;charset=utf-8");
        res.getWriter().write(jsonString);
    }

    @RequestMapping(value = "/selectByPageAndCondition", method = RequestMethod.POST)
    @ResponseBody
    public void selectByPageAndCondition(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        Integer userId = (Integer) session.getAttribute("userId");
        String _currentPage = req.getParameter("currentPage");
        String _pageSize = req.getParameter("pageSize");
        int currentPage = Integer.parseInt(_currentPage);
        int pageSize = Integer.parseInt(_pageSize);
        BufferedReader br = req.getReader();
        String params = br.readLine();
        Search search= JSON.parseObject(params, Search.class);
        Search<WeightMes> searches = indicatorServiceImpl.selectWeightMesByPageAndCondition("weights",currentPage,pageSize,search);
        String jsonString = JSON.toJSONString(searches);
        res.setContentType("text/json;charset=utf-8");
        res.getWriter().write(jsonString);
    }

    @RequestMapping("/packageZip")
    public String packageZip(String id, HttpServletResponse response){
        if( id != null && !"".equals(id)){
            String path = weightStore + id;
            List<String> list = new ArrayList<>();
            list.add(path);
            ZipUtil.downloadZipFiles(response,list,id + ".zip");
            return null;
        }
        return "wrong";
    }

    @RequestMapping("/updateFile")
    @ResponseBody
    public String updateFile(@RequestParam("file") MultipartFile uploadFile ,WeightMes weightMessage) throws IOException {
        String cfgName = "",jpgName = "",weightsName = "",message = "";
        int fileFlag = 0;
        if(!uploadFile.isEmpty()){
            String suffix = ZipUtil.suffixOfFile(uploadFile);
            if(!".zip".equals(suffix))
                return "上传文件须为.zip文件";
            FileHandleResponse fileHandleResponse = new FileHandleResponse();
            fileHandleResponse = ZipUtil.unZipFiles(weightTempStore,"tempFile",uploadFile,true);
            String path = weightTempStore + fileHandleResponse.getUrl() + "/";
            String toPath = weightStore + fileHandleResponse.getUrl() + "/";
            File toFile = new File(toPath);
            File fileUrl = new File(path);
            File[] fileList = fileUrl.listFiles();
            if(fileList.length!=3){
                System.out.println(fileList.length);
                message = "压缩包内须包含且仅包含.weights .cfg .jpg3个文件";
                return message;
            }else{
                for(File f:fileList){
                    String filename = f.getName();
                    if(filename.endsWith("jpg")) {
                        jpgName = filename;
                        fileFlag++;
                    }
                    else if(filename.endsWith("weights")){
                        weightsName = toPath + filename;
                        fileFlag += 10;
                    }
                    else if(filename.endsWith("cfg")){
                        cfgName =  toPath + filename;
                        fileFlag += 100;
                    }
                }
                String category = weightMessage.getCategory();
                String message1 = weightMessage.getMessage();
                String url = netWeightStore + fileHandleResponse.getUrl() + "/"+jpgName;
                if(fileFlag == 111){
                    userMapper.addWeight("weights",category, cfgName, weightsName, message1, url,false);
                    fileUrl.renameTo(toFile);
                    return "上传成功";
                }else if(fileFlag % 10 != 1){
                    message = "上传文件中须包含一张作为封面的.jpg图片";
                    return message;
                }else if(fileFlag/10 % 10 != 1){
                    message = "上传文件中须包含权重文件.weight";
                    return message;
                }else if(fileFlag/100 % 10 != 1){
                    message = "上传文件中须包含配置文件.cfg";
                    return message;
                }
            }
            return null;
        }else{
            return "文件为空";
        }
    }

    @RequestMapping(value = "/deleteWeightById", method = RequestMethod.POST)
    @ResponseBody
    public String deleteWeightById(String id){
        if( id != null && !"".equals(id)){
            System.out.println("id = " + id);
            try{
                String url = userMapper.selectWeightMesById("url","weights","id",id);
                String fileUrl = weightStore + ZipUtil.subStr(url);
                if(ZipUtil.deletePath(fileUrl) == 1){
                    userMapper.deleteWeightById("weights",Integer.parseInt(id));
                    return "ok";
                }else{
                    return "对应文件不存在，删除失败";
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
                return "删除失败";
            }
        }else{
            return "id = null";
        }
    }

    @RequestMapping(value = "/addWeightById", method = RequestMethod.POST)
    @ResponseBody
    public String addWeightById(String id,HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        String searchType = userId + elementType;
        WeightMes weightMes = userMapper.selectWeightById("weights",Integer.parseInt(id));
        System.out.println(weightMes);
        try{
            userMapper.addWeight(searchType,weightMes.getCategory(),weightMes.getCfg(),weightMes.getWeight(),
                    weightMes.getMessage(),weightMes.getUrl(),weightMes.getState());
            return "ok";
        }catch(Exception e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }






}
