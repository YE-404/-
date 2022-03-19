package com.myproject.systemdemo.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myproject.systemdemo.domain.Log;
import com.myproject.systemdemo.domain.Search;
import com.myproject.systemdemo.domain.User;
import com.myproject.systemdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisTools {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;
    //修改redis中hash存值
    //修改对应的大key小key的值
    public String updateHashParam(String bigKey,String key,String value){
        String param = searchParam(bigKey);
        JSONObject jsonObject= JSON.parseObject(param, JSONObject.class);
        jsonObject.put(key,value);
        String jsonString = JSON.toJSONString(jsonObject);
        redisTemplate.boundValueOps(bigKey).set(jsonString);
        return jsonString;
    }
    //修改redis缓存中的用户密码
    public boolean updateUserPassword(String key,String value){
        String param = (String) redisTemplate.boundValueOps(key).get();
        User user = JSON.parseObject(param, User.class);
        if(user == null)
            return false;
        else{
            user.setPassword(value);
            String jsonString = JSON.toJSONString(user);
            redisTemplate.boundValueOps(key).set(jsonString);
            return true;
        }
    }



    public String searchParam(String bigKey){
        String param = (String) redisTemplate.boundValueOps(bigKey).get();
        return param;
    }
    //登出时将日志信息写入数据库
    public void saveLogToDatabase(String userId){
        String param = searchParam(userId);
        Log logInput = JSON.parseObject(param, Log.class);
        String fileSize = logInput.getFileSize();
        userMapper.updateUserLog(fileSize,Integer.parseInt(userId));
    }
    public String changeVisit(String bigKey,String key,String way){ // 修改系统用户数量和访问数
        String param = searchParam(bigKey);
        JSONObject jsonObject= JSON.parseObject(param, JSONObject.class);
        Integer visitValue = 0;
        if(way.equals("add")){
            System.out.println(visitValue);
            visitValue = (Integer) jsonObject.getInteger(key) + 1;
            System.out.println(visitValue);
        }else if(way.equals("subtract")){
            visitValue = (Integer) jsonObject.getInteger(key) - 1;
        }
        jsonObject.put(key,visitValue);
        String jsonString = JSON.toJSONString(jsonObject);
        redisTemplate.boundValueOps(bigKey).set(jsonString);
        return jsonString;
    }

}
