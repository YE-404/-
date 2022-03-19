package com.myproject.systemdemo.tools;

import com.alibaba.fastjson.JSON;
import com.myproject.systemdemo.domain.SaveUserLogin;
import com.myproject.systemdemo.domain.SystemMes;
import com.myproject.systemdemo.domain.User;
import com.myproject.systemdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class MyApplicationRunner  implements ApplicationRunner {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        SystemMes systemMes = new SystemMes();
//        systemMes.setId(1);
//        systemMes.setSystemLogVolume(0);
//        systemMes.setUserVolume(4);
//        systemMes.setVisitorVolume(201);
//        systemMes.setDocumentVolume(0);
//        systemMes.setWeekOfYear(51);
//        systemMes.historyTime = new String[]{"0", "0","0"};
//        systemMes.logMessage = new String[]{"0", "0","0"};
//        systemMes.username = new String[]{"0", "0","0"};
//        String jsonString = JSON.toJSONString(systemMes);
//        redisTemplate.boundValueOps("system").set(jsonString);
        Set<String> keys = stringRedisTemplate.keys("useId"+"*");//清空redis数据库中所有的键值对
        stringRedisTemplate.delete(keys);
        keys = stringRedisTemplate.keys("*" + "-log");//清空redis数据库中所有的键值对
        stringRedisTemplate.delete(keys);
        List<User> users = userMapper.selectAllUsernameAndPasswordAndId();
        SaveUserLogin.userCount = users.size();
        for(User user : users){
            String jsonString = JSON.toJSONString(user);
            redisTemplate.boundValueOps(user.getUsername()).set(jsonString);
        }
    }
}

//        String param = (String) redisTemplate.boundValueOps("system").get();
//        if(param != null && !param.equals("")){
//            SystemMes system = JSON.parseObject(param, SystemMes.class);
//            Integer visitorVolume = system.getVisitorVolume();
//            Integer userVolume = system.getUserVolume();
//            Integer systemLogVolume = system.getSystemLogVolume();
//            userMapper.updateSystemMessage(visitorVolume,userVolume,systemLogVolume);
//        }
//        if(param != null && !param.equals("")){
//            String initParam = "{visitorVolume=0,userVolume=0,systemLogVolume=0}";
//            redisTemplate.boundValueOps("system").set(initParam);
//        }else{
//            redisTemplate.boundValueOps("system").set(param);
//        }