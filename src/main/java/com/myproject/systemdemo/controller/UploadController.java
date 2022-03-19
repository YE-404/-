package com.myproject.systemdemo.controller;

import com.alibaba.fastjson.JSON;
import com.myproject.systemdemo.domain.Upload;
import com.myproject.systemdemo.thriftDemo.ThriftClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/upload")


public class UploadController {
    @Autowired
    private ThriftClient client;

    @Value("${masterDirectory}")
    private String masterDirectory;

    @Value("${spellPath}")
    private String spellPath;


    @RequestMapping(value = "/spellPic", method = RequestMethod.POST)
    @ResponseBody
    public String spellPic(@RequestParam("file") MultipartFile[] uploadFile, Upload upload, HttpServletResponse res, HttpSession session) throws IOException {
        List<String> dataList = new ArrayList<>();
        Integer userId = (Integer) session.getAttribute("userId");
        System.out.println(uploadFile.length);

        for(MultipartFile file : uploadFile){
            if (!file.isEmpty()) {
                BASE64Encoder encoder = new BASE64Encoder();
                // 通过base64来转化图片
                String data = encoder.encode(file.getBytes());
                dataList.add(data);
            }
        }
        String optionsRadios = upload.getOptionsRadios();
        String[] optionsRadiosList = new String[]{optionsRadios};
        String jsonString = JSON.toJSONString(dataList);
        System.out.println(jsonString);
//        System.out.println(jsonString);
        String getResult = client.getResult(jsonString,"spell",optionsRadiosList,String.valueOf(userId));
        return getResult;
    }

    @RequestMapping(value = "/downloadPic", method = RequestMethod.GET)//
    public ResponseEntity downloadPic(HttpServletRequest req, HttpServletResponse res, HttpSession session) throws ServletException, IOException {
        Integer userId = (Integer) session.getAttribute("userId");
        String params = req.getParameter("address");
        String logName = params.substring(params.lastIndexOf("/")+1);
        String path = spellPath + userId + "/" + logName;
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
