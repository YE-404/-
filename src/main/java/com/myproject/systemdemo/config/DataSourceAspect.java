package com.myproject.systemdemo.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.myproject.systemdemo.mapper.CreatTable;
import com.myproject.systemdemo.mapper.UserMapper;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;


@Aspect
@Order(1)
@Configuration
public class DataSourceAspect {
    private static final String dsNo="db";//数据库编号 从header中取
    private ServletRequestAttributes attr;
    private HttpSession session;

    //private CreatTable creatTable = new CreatTable();
    @Autowired
    private CreatTable creatTable;

    @Autowired
    private RedisTemplate redisTemplate;


    @Pointcut("execution(* com.myproject.systemdemo.controller.UserController.loginSucceed(..))")
    private void loginMethod(){}

    @Pointcut("execution(* com.myproject.systemdemo.listener.SessionListener.*(..))")
    private void destroyMethod(){}

    @Before("loginMethod()")
    public void createTable() throws SQLException {
        attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
        session = attr.getRequest().getSession(true);
        Integer userId = (Integer) session.getAttribute("userId");
        creatTable.init("lights",userId);
        creatTable.init("switchs",userId);
        creatTable.init("number_clocks",userId);
        creatTable.init("point_clocks",userId);
        creatTable.init("spells",userId);
        creatTable.init("weights",userId);
    }

//    @Before("destroyMethod()")
//    public void deleteFromRedis(){
//        System.out.println("before");
//        attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
//        session = attr.getRequest().getSession(true);
//        Integer userId = (Integer) session.getAttribute("userId");
//        System.out.println(redisTemplate.opsForSet().members("onLineUser"));
//        System.out.println("after");
//        redisTemplate.opsForSet().remove("onLineUser", String.valueOf(userId));
//    }

    /**
     * 切入点,放在controller的每个方法上进行切入,更新数据源
     */
//    @Pointcut("execution(* com.myproject.systemdemo.controller..*.*(..))")
//    @Pointcut("execution(* com.myproject.systemdemo.controller.*.*(..)) && !execution(* com.myproject.systemdemo.controller.UserController.*(..))")//MenuController
//    private void anyMethod(){}//定义一个切入点
//    @Before("anyMethod()")
//    public void dataSourceChange()
//    {
//        //请求头head中获取对应数据库编号
//        //String no = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader(dsNo);//
//        attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
//        session=attr.getRequest().getSession(true);
//        Integer no = (Integer)session.getAttribute("userId");
//        System.out.println("当前数据源编号:"+no);
//        if(StringUtils.isEmpty(no)){
//            //TODO 根据业务抛异常
//        }
//        DataSourceHolder.setDataSource(String.valueOf(no));
//        /*这里数据库项目编号来更改对应的数据源*/
//    }
//
//    @Pointcut("execution(* com.myproject.systemdemo.controller.UserController.loginSucceed(..))")
//    private void loginMethod(){}
//    @Before("loginMethod()")
//    public void dataSourceInit()
//    {
//        dynamicDataSource.DynamicDataSourceInit(userMapper.getIds("users"));
//
//    }



}
