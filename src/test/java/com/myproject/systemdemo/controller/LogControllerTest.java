package com.myproject.systemdemo.controller;

import com.myproject.systemdemo.MenuApplication;
import com.myproject.systemdemo.domain.User;
import com.myproject.systemdemo.log.LogDemo;
import com.myproject.systemdemo.mapper.UserMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static sun.misc.Version.print;

//@RunWith(SpringRunner.class)
//@ExtendWith(SpringExtension.class)


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MenuApplication.class},webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)//,
//@WebAppConfiguration
//@WebMvcTest(controllers = MenuApplication.class)
class LogControllerTest {


    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    public static final Logger LOGGER = (Logger) LogManager.getLogger(LogDemo.class);

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testMvc() throws Exception{

//        String json="{\"username\":\"666\",\"password\":\"666\"}";//,"url":"http://tengj.top/"
//        mockMvc.perform(MockMvcRequestBuilders.post("/user/logincheck")
//                .content(json.getBytes())//传参
//                .accept(MediaType.APPLICATION_JSON_UTF8))
//                .andExpect(MockMvcResultMatchers.status().isOk())//判断状态码
//                .andDo(MockMvcResultHandlers.print());
//        LOGGER.info(MockMvcResultHandlers.print());


        mockMvc.perform(MockMvcRequestBuilders.get("/menu/hostservlet")
                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())//判断状态码
                .andDo(MockMvcResultHandlers.print());

    }


//    @Autowired
//    private TestRestTemplate testRestTemplate;

//
//    @Test
//    void testServlet() throws Exception{
//        String content = testRestTemplate.getForObject("/testServlet",String.class);
//        System.out.println(content);
//
////        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/user/logincheck")
////                //.param("name", "lvgang")
////                .accept(MediaType.APPLICATION_JSON))
////                //----等同于Assert.assertEquals(200,status);
////                .andExpect(MockMvcResultMatchers.status().isOk())
////                //----等同于 Assert.assertEquals("hello world!",content);
////                .andExpect(MockMvcResultMatchers.content().string("hello world!"))
////                .andDo(MockMvcResultHandlers.print())
////                .andReturn();
////        //得到返回代码
////        int status = mvcResult.getResponse().getStatus();
//        //得到返回结果
//
//    }
}