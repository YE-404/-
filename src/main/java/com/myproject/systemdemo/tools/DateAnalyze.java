package com.myproject.systemdemo.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myproject.systemdemo.domain.SystemMes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



@Service
public class DateAnalyze {

    @Autowired
    private RedisTemplate redisTemplate;

    public int dayForWeek() throws Throwable {
        Calendar cal = Calendar.getInstance();
        String[] weekDays = { "7", "1", "2", "3", "4", "5", "6" };
        try {
            cal.setTimeInMillis(System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        if (w < 0) w = 0;
        return Integer.parseInt(weekDays[w]);
    }
    public String weekForYear() throws Throwable {
        Calendar calendar = Calendar.getInstance();
        //设置星期一为一周开始的第一天
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        //获得当前的时间戳
        calendar.setTimeInMillis(System.currentTimeMillis());
        //获得当前日期属于今年的第几周
        int weekOfYearNo = calendar.get(Calendar.WEEK_OF_YEAR);
        String weekOfYearStr = String.valueOf(weekOfYearNo);
        return weekOfYearStr;
    }

    public void addLoginTimes() throws Throwable {
        int thisDay = dayForWeek();
        String weekOfYearStr = "week-" + weekForYear();
        String param = (String) redisTemplate.boundValueOps(weekOfYearStr).get();
        String jsonString = "";
        if(param == null){
            SystemMes.thisWeekLogin[thisDay-1] += 1;
            jsonString = JSON.toJSONString(SystemMes.thisWeekLogin);
        }else{
            int[] jsonObject= JSON.parseObject(param, int[].class);
            jsonObject[thisDay-1] += 1;
            jsonString = JSON.toJSONString(jsonObject);
        }
        System.out.println("addLoginTimes" + jsonString);
        redisTemplate.boundValueOps(weekOfYearStr).set(jsonString);
    }
}
