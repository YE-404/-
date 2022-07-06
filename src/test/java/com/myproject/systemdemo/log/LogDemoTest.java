package com.myproject.systemdemo.log;


import com.myproject.systemdemo.MenuApplication;
import com.myproject.systemdemo.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import com.myproject.systemdemo.mapper.UserMapper;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {MenuApplication.class})//classes = {LogDemo.class,UserMapper.class }
class LogDemoTest {

    @Autowired
    private LogDemo logDemo;

//    @MockBean
//    private User user;

    //private TestRestTemplate testRestTemplate;

    @Test
    void judgeFileSize() {
        System.out.println(logDemo.judgeFileSize(7895461));//

    }
}