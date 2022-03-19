package com.myproject.systemdemo.listener;

import com.myproject.systemdemo.config.DataSourceAspect;
import com.myproject.systemdemo.domain.SaveUserLogin;
import com.myproject.systemdemo.log.LogDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class SessionListener implements HttpSessionListener {



    @Override
    public void sessionCreated(HttpSessionEvent event) {
        System.out.println("session create");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if(userId != null){
            SaveUserLogin.getMap().remove(String.valueOf(userId));
            SaveUserLogin.offlineIdList.add(String.valueOf(userId));
            //System.out.println("offlineIdList" + SaveUserLogin.offlineIdList);
            System.out.println("session destroyed： " + userId);
        }
        System.out.println("session destroyed over");

    }
}
