package com.myproject.systemdemo.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.myproject.systemdemo.domain.SystemMes;
import com.myproject.systemdemo.logTry;
import com.myproject.systemdemo.mapper.UserMapper;
import com.myproject.systemdemo.tools.RedisTools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class LogDemo {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisTools redisTools;

    @Value(value = "${address}")
    private String address;

    @Value("${userLog}")
    private String userLog;

    @Value("${systemInfo}")
    private String systemInfo;

    public static final Logger LOGGER = (Logger) LogManager.getLogger(LogDemo.class);
    //Logger LOGGER = LogManager.getLogger(LogDemo.class);
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void writeLog(String userId,String logMessage){
        String str1 = (String) redisTemplate.boundValueOps("userId-" + userId).get();
        JSONObject jsonObj = JSON.parseObject(str1);
        String username = jsonObj.getString("username");
        String path = userLog + userId + ".log";
        File file = new File(path);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            String time = df.format(new Date());
            String finalMessage = "[" + time + "] - " + "[" + username + "]" +  " - " + logMessage;
            FileWriter fw = new FileWriter(file,true);
            fw.write('\n');
            fw.write(finalMessage);
            fw.flush();
            fw.close();
            LOGGER.info(finalMessage);
            updateManage(time,username,logMessage);
            double size = file.length();
            String fileSize = judgeFileSize(size);
            //userMapper.updateUserLog(fileSize,Integer.parseInt(userId));
            String bigKey = userId + "-log";
            redisTools.updateHashParam(bigKey,"fileSize",fileSize);
            redisTools.updateHashParam(bigKey,"changeDate",time);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String judgeFileSize(double size){
        String fileSize = "";
        if(size<500){
            fileSize = String.valueOf(size) + " B";
        }else if(size>=500&&size<51200){
            size = size/1024;
            fileSize = String.valueOf(size) + " KB";
        }else{
            size = size/1024/1024;
            fileSize = String.valueOf(size) + " MB";
        }
        return fileSize;
    }

    public void createDatabase(Integer userId,String username){
        String path = "http://" + address +"/log/user/" + userId + ".log";
        if(userMapper.selectUserLogByUserId(userId) == null){
            userMapper.adduserLog(userId,username,"0",path);
        }else {
            System.out.println("table has been");
        }
    }

    public void updateManage(String time,String name,String message){
        numberOfFiles();
        String param = (String) redisTemplate.boundValueOps("system").get();
        JSONObject jsonObject= JSON.parseObject(param, JSONObject.class);
        JSONArray historyTime = jsonObject.getJSONArray("historyTime");
        JSONArray logMessage = jsonObject.getJSONArray("logMessage");
        JSONArray username = jsonObject.getJSONArray("username");

        for(int i=2; i>0; i--){
            historyTime.set(i,historyTime.get(i-1));
            logMessage.set(i,logMessage.get(i-1));
            username.set(i,username.get(i-1));
        }
        historyTime.set(0,time);
        username.set(0,name);
        logMessage.set(0,message);
        jsonObject.put("historyTime",historyTime);
        jsonObject.put("logMessage",logMessage);
        jsonObject.put("username",username);
        String jsonString = JSON.toJSONString(jsonObject);
        redisTemplate.boundValueOps("system").set(jsonString);
    }



    public int numberOfFiles(){
        File folder = new File(systemInfo);
        File []list = folder.listFiles();
        int fileCount = 0, folderCount = 0;
        long length = 0;
        for (File file : list){
            if (file.isFile()){
                fileCount++;
                length += file.length();
            }else {
                folderCount++;
            }
        }
        //System.out.println("文件夹的数目: " + folderCount + " 文件的数目: " + fileCount);
        return  fileCount;
    }


}
