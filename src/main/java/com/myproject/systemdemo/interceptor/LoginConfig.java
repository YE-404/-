package com.myproject.systemdemo.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//@Configuration
//public class LoginConfig implements WebMvcConfigurer {
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        InterceptorRegistration registration = registry.addInterceptor(new PrivilegeInterceptor());
//        registration.addPathPatterns("/**");
//        registration.excludePathPatterns(
////                "/user/logincover",
////                "/user/logincheck",
////                "/user/registercover",
////                "/user/registercheck",
////                "/user/createPicture",
//                "/**/checkcodeservlet",
//                "/user/**",
//                "/**/*.html",
//                "/**/*.js",
//                "/**/*.css",
//                "/**/*.png",
//                "/**/*.jpg"
//        );
//    }
//}
