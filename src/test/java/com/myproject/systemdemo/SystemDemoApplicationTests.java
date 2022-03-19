package com.myproject.systemdemo;

import com.myproject.systemdemo.domain.Indicator;
import com.myproject.systemdemo.domain.Search;
import com.myproject.systemdemo.domain.User;
import com.myproject.systemdemo.mapper.UserMapper;
import com.myproject.systemdemo.server.IndicatorServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SystemDemoApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private IndicatorServiceImpl indicatorServiceImpl;
    @Test
    void contextLoads() {
        List<User> users = userMapper.selectAll();
        System.out.println(users);
    }
    @Test
    public void selectTry(){
        int currentPage = 1;
        int pageSize = 5;
        String type = "lights";
        int begin = (currentPage -1) * pageSize;
        int size = pageSize;
        System.out.println(type);
        System.out.println(begin);
        System.out.println(pageSize);
        List<Indicator> rows = userMapper.selectByPage(type,begin,size);
        int totalCount = userMapper.selectTotalCount(type);
        Search<Indicator> searches = new Search<>();
        searches.setRows(rows);
        searches.setTotalCount(totalCount);
        System.out.println(searches);
    }
    @Test
    public void tryDemo(){

        indicatorServiceImpl.selectByPage("lights",1,5);
    }

}
