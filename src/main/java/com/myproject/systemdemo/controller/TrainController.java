package com.myproject.systemdemo.controller;

import com.alibaba.fastjson.JSON;
import com.myproject.systemdemo.domain.FileHandleResponse;
import com.myproject.systemdemo.domain.Upload;
import com.myproject.systemdemo.mapper.UserMapper;
import com.myproject.systemdemo.thriftDemo.ThriftClient;
import com.myproject.systemdemo.tools.ZipUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/train")


public class TrainController {
    @Autowired
    private ThriftClient client;

    @Autowired
    private UserMapper userMapper;

    @Value("${darknetPath}")
    private String darknetPath;

    @RequestMapping(value = "/updateWeight", method = RequestMethod.POST)
    @ResponseBody
    public String updateWeight(@RequestParam("fileImg") MultipartFile[] uploadFileImg, @RequestParam("fileXml") MultipartFile[] uploadFileXml, @RequestParam("id") Integer id, HttpSession session, HttpServletResponse res) throws IOException {
//        if(uploadFileImg == null || uploadFileImg.length != 2 || uploadFileXml.length != 2){
//            return "请上传训练集和测试集文件";
//        }
        Integer userId = (Integer) session.getAttribute("userId");
        String message;
        int serialNumber = (int) (Math.random()*9000+1000);
        String path = darknetPath + "VOC" + serialNumber;
        createVOC(serialNumber);
        String mesPic = unzip(uploadFileImg, "/JPEGImages/", path, serialNumber);
        String mesXml = unzip(uploadFileXml, "/Annotations/", path, serialNumber);
        if(!mesPic.equals("succeed") || !mesXml.equals("succeed")){
            return mesPic;//如果解压过程失败，则返回
        }
        message = judgeDocument(serialNumber);
        if(!message.equals("ok")){
            destroy(serialNumber);
            return message;//如果解压完成后的文件结构不符则返回
        }
        if(!userMapper.selectMesByCondition("train_status", "id", String.valueOf(userId)).equals("0")){
            System.out.println("train has been ing");
            return "已有训练任务在进行中";
        }
        session.setAttribute("serialNumber",serialNumber);

        userMapper.updateMessageString(userId,"train_status", String.valueOf(id));//在user表中置入训练状态
        userMapper.setState(userId + "_weights","train",true,"id",String.valueOf(id));//在x_weights表中置入训练状态
        System.out.println("everything is ok!!!!!");
        return "ok";
    }

    @RequestMapping(value = "/startTrainWeight", method = RequestMethod.POST)
    @ResponseBody
    public String startTrainWeight(HttpSession session, HttpServletRequest req) throws IOException {
        BufferedReader br = req.getReader();
        String params = br.readLine();
        int serialNumber = (int) session.getAttribute("serialNumber");
        Integer userId = (Integer) session.getAttribute("userId");
        System.out.println(client.trainWeight(serialNumber,userId, Integer.parseInt(params)));
        userMapper.updateMessageString(userId,"train_status","0");//在user表中清除训练状态
        userMapper.setState(userId + "_weights","train",false,"id",params);//在x_weights表中置入训练状态
        return null;
    }




    public void createVOC(int serialNumber){
        File f1 = new File(darknetPath + "VOC" + serialNumber + "/"+ "Annotations");
        File f2 = new File(darknetPath + "VOC" + serialNumber + "/"+ "ImageSets/" + "Main");
        File f3 = new File(darknetPath + "VOC" + serialNumber + "/"+ "JPEGImages");
        File f4 = new File(darknetPath + "VOC" + serialNumber + "/"+ "labels");
        if(!f1.exists())  f1.mkdirs();
        if(!f2.exists())  f2.mkdirs();
        if(!f3.exists())  f3.mkdirs();
        if(!f4.exists())  f4.mkdirs();
    }

    public String unzip(MultipartFile[] uploadFile, String fileType, String path, int serialNumber) throws IOException {
        for(MultipartFile file : uploadFile){
            if (!file.isEmpty()) {
                String suffix = ZipUtil.suffixOfFile(file);
                if(!".zip".equals(suffix)){
                    destroy(serialNumber);
                    return "上传文件须为.zip文件";
                }
                String fileName = file.getOriginalFilename().substring(0,file.getOriginalFilename().indexOf("."));
                FileHandleResponse fileHandleResponse = new FileHandleResponse();
                fileHandleResponse = ZipUtil.unZipFiles(path + fileType,"tempFile",file,false);
            }
        }
        return "succeed";
    }

    public int destroy(int serialNumber) throws IOException {
        if(ZipUtil.deletePath(darknetPath + "VOC" + serialNumber) == 1){
            return 1;
        }else
            return 0;
    }

    public String judgeDocument(int serialNumber) throws IOException {
        File fileImg1 = new File(darknetPath + "VOC" + serialNumber + "/JPEGImages/train_pic/");
        File fileImg2 = new File(darknetPath + "VOC" + serialNumber + "/JPEGImages/test_pic/");
        File fileXml1 = new File(darknetPath + "VOC" + serialNumber + "/Annotations/train_xml/");
        File fileXml2 = new File(darknetPath + "VOC" + serialNumber + "/Annotations/test_xml/");
        String message;
        if(fileImg1.exists() && fileImg2.exists() && fileXml1.exists() && fileXml2.exists()){
            message = fileAnalyze(fileImg1,"image/jpeg","jpg");
            if(!message.equals("ok"))  return message;
            message = fileAnalyze(fileImg2,"image/jpeg","jpg");
            if(!message.equals("ok"))  return message;
            message = fileAnalyze(fileXml1,"application/xml","xml");
            if(!message.equals("ok"))  return message;
            message = fileAnalyze(fileXml2,"application/xml","xml");
            if(!message.equals("ok"))  return message;

            message = judgePicAndXml(fileImg1,fileXml1,"训练集");
            if(!message.equals("ok"))  return message;
            message = judgePicAndXml(fileImg2,fileXml2,"测试集");
            if(!message.equals("ok"))  return message;
            return "ok";
        }else
            return "文件路径有误，请参照说明修改";


    }

    public String fileAnalyze(File file,String fileType,String hint) throws IOException {
        URLConnection connection;
        File[] fs = file.listFiles();
        for(File f : fs){
            if(f.isDirectory()) return "文件路径有误，请参照说明修改";
            connection = f.toURL().openConnection();
            if(hint.equals("jpg")){
                if(!connection.getContentType().equals(fileType)) return "训练集和测试集图片文件夹中须均为jpg文件";
            }else if(hint.equals("xml")){
                if(!connection.getContentType().equals(fileType)) return "训练集和测试集标注文件夹中须均为xml文件";

            }
        }
        return "ok";
    }
    public String judgePicAndXml(File fileImg,File fileXml,String type){
        File[] imgList = fileImg.listFiles();
        File[] xmlList = fileXml.listFiles();
        if(imgList.length != xmlList.length)
            return type + "图片与xml文件数量不匹配";
        Arrays.sort(imgList,ZipUtil.comparatorName);
        Arrays.sort(xmlList,ZipUtil.comparatorName);
        for(int i=0;i<imgList.length;i++){
            if(!ZipUtil.cutFilePreName(imgList[i].getName()).equals(ZipUtil.cutFilePreName(xmlList[i].getName())))
                return type + "图片与xml文件名不匹配";
        }

        return "ok";
    }

}
