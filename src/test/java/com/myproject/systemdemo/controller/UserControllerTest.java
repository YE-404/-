package com.myproject.systemdemo.controller;

import com.myproject.systemdemo.MenuApplication;
import com.myproject.systemdemo.mapper.UserMapper;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest//(classes = {MenuApplication.class})//,,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
class UserControllerTest {

    @Rule
    public ContiPerfRule rule = new ContiPerfRule();

//    @Autowired
//    private WebApplicationContext wac;
//
//    private MockMvc mockMvc;
//
//    @Autowired
//    private UserMapper userMapper;
//
//    @BeforeEach
//    public void setUp() throws Exception {
//        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
//    }


//    TestRunnable runner = new TestRunnable() {}


    @PerfTest(invocations = 10,threads = 5, duration = 20)
    //@Required(max = 1200, average = 2500, totalTime = 60000)
    @Test
    public void login() throws Exception{

//        String json="{\"username\":\"666\",\"password\":\"666\"}";//,"url":"http://tengj.top/"
//        mockMvc.perform(MockMvcRequestBuilders.post("/user/logincheck")
//                .content(json.getBytes())//传参
//                .accept(MediaType.APPLICATION_JSON_UTF8))
//                .andExpect(MockMvcResultMatchers.status().isOk())//判断状态码
//                .andDo(MockMvcResultHandlers.print());

        System.out.println("123456");
        //int id = (int) (Math.random() * 100);
//        userMapper.selectAll();
//        System.out.println("ok!!!!!!!!!!");
    }
    @Test
    @PerfTest(invocations = 10,threads = 5, duration = 20)
    public  void  test2()throws Exception{
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(20);
        final CountDownLatch countDownLatch = new CountDownLatch(50);
        long l = System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            final int count = i;
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    System.out.println("123456");
                    semaphore.release();
                } catch (Exception e) {
                    // log.error("exception" , e);
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        long a = System.currentTimeMillis();
        System.out.println(a-l);
        executorService.shutdown();

        //log.info("size:{}" , map.size());
    }




}