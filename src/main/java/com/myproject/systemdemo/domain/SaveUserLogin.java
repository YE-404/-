package com.myproject.systemdemo.domain;

import com.myproject.systemdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SaveUserLogin {

    @Autowired
    private UserMapper userMapper;

    private static HashMap<String,String> map = new HashMap<>();

    public static Integer userCount;

    public static List<String> offlineIdList = new ArrayList<>();

    public static List<String> getOfflineIdList() {
        return offlineIdList;
    }

    public static void setOfflineIdList(List<String> offlineIdList) {
        SaveUserLogin.offlineIdList = offlineIdList;
    }

//    public static Integer getUserCount() {
//        return userCount;
//    }
//
//    public static void setUserCount(Integer userCount) {
//        SaveUserLogin.userCount = userCount;
//    }

    public static HashMap<String, String> getMap() {
        return map;
    }

    public static void setMap(HashMap<String, String> map) {
        SaveUserLogin.map = map;
    }
//    public static HashMap<String,String> map;

}
