package com.myproject.systemdemo.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class PrivilegeInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        //System.out.println("PrivilegeInterceptor userId:" + userId);
        if(userId == null){
            response.sendRedirect(request.getContextPath() + "/user/logincover");
            return false;
        }
        return true;
    }
}
