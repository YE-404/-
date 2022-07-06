package com.myproject.systemdemo.listener;

import com.myproject.systemdemo.domain.SaveUserLogin;
import com.myproject.systemdemo.domain.SystemMes;
import com.myproject.systemdemo.domain.User;
import com.myproject.systemdemo.log.LogDemo;
import com.myproject.systemdemo.mapper.UserMapper;
import com.myproject.systemdemo.tools.RedisTools;
import com.myproject.systemdemo.tools.ZipUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class SchedulerTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LogDemo logDemo;

    @Autowired
    private RedisTools redisTools;

    @Value("${weightDirectory}")
    private String weightDirectory;

    @Scheduled(cron = "0 50 * * * *")
    public void scheduled(){
        System.out.println("one time...");
    }

    @Scheduled(cron = "0 0 0 ? * 1")
    public void scheduleWeekClear(){
        for(int i=0;i<6;i++){
            SystemMes.thisWeekLogin[i] = 0;
        }
    }

//    @Scheduled(cron = "0 0/1 * * * *")
//    public void scheduleCheckTrain(){
//        ZipUtil zipUtil = new ZipUtil();
//        List<User> userArray = userMapper.selectTrainStatus();
//        String path;
//        for(User i : userArray){
//            path = weightDirectory + i.getId() + "/";
//            System.out.println("scheduleCheckTrain(): " + path);
//            if(judgeDocumentExists(path,"succeed",i)){
//                userMapper.updateMessageString(i.getId(),"train_status","0");
//            }
//            //
//        }
//    }
//
//    public boolean judgeDocumentExists(String filePath, String keyword, User user){
//        File file = new File(filePath);
//        File[] fileList = file.listFiles();
//        for(File i : fileList){
//            if(i.getName().toLowerCase().contains(keyword)){
//                String weightName = i.getName().substring(0,i.getName().lastIndexOf("_"));
//                String weightFinalName = filePath  + weightName + "_final.weights";
//                System.out.println("judgeDocumentExists(): " + weightFinalName);
//                userMapper.updateWeightMessage(user.getId()+"_weights","weight",weightFinalName,user.getTrainStatus());
//                System.out.println("set over!!!!!!!!!!!");
//                i.delete();
//                return true;
//            }
//        }
//        return false;
//    }

}
