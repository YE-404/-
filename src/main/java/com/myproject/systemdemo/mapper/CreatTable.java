package com.myproject.systemdemo.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//@Component
//@PropertySource("classpath:application.yml")
//@Configuration
@Service
public class CreatTable {

    @Value(value = "${spring.datasource.driver-class-name}")
    private String driver;
    @Value("${spring.datasource.url}")
    private String url;
    @Value(value = "${spring.datasource.username}")
    private String userName;
    @Value(value = "${spring.datasource.password}")
    private String passWord;

    @Value("${userLog}")
    private String userLog;
    @Value("${masterDirectory}")
    private String masterDirectory;

    public void init(String newTable,Integer userId) throws SQLException {
        Connection conn = DriverManager.getConnection(url, userName, passWord);
        Statement stat = conn.createStatement();
        if(userId != null){
            String createTableName = String.valueOf(userId) + "_" + newTable;
            ResultSet rs = conn.getMetaData().getTables(null, null, createTableName, null);
            String filePar = masterDirectory + newTable + "/"+String.valueOf(userId);
            File myPath = new File( filePar );
            if (!myPath.exists()){//若此目录不存在，则创建之
                myPath.mkdir();
                System.out.println("创建文件夹路径为："+ filePar);
            }
            if(rs.next()){
                //System.out.println(createTableName + " has been, out");
                stat.close();
                conn.close();
                return;
            }
            System.out.println("createTableName:"+createTableName);
            if(newTable.equals("lights"))
            {
                stat.executeUpdate("create table if not exists " + createTableName +
                        "(id int unsigned not null auto_increment primary key,class varchar(60),Time datetime)" +
                        "ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1");
            }else if(newTable.equals("switchs")){
                stat.executeUpdate("create table if not exists " + createTableName +
                        "(id int unsigned not null auto_increment primary key,class varchar(60),Time datetime)" +
                        "ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1");
            }else if(newTable.equals("number_clocks")){
                stat.executeUpdate("create table if not exists " + createTableName +
                        "(id int unsigned not null auto_increment primary key,class varchar(20),result varchar(20),Time datetime)" +
                        "ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1");
            }else if(newTable.equals("point_clocks")){
                stat.executeUpdate("create table if not exists " + createTableName +
                        "(id int unsigned not null auto_increment primary key,class varchar(20),result varchar(20),Time datetime)" +
                        "ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1");
            }else if(newTable.equals("weights")){
                stat.executeUpdate("create table if not exists " + createTableName +
                        "(id int unsigned not null auto_increment primary key,category varchar(20),cfg varchar(255)," +
                        "weight varchar(255), message varchar(100),url varchar(255),date varchar(30),state boolean)" +
                        "ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;");
            }
        }
        stat.close();
        conn.close();
    }
    public void deleteUserTable(Integer userId) throws SQLException {
        Connection conn = DriverManager.getConnection(url, userName, passWord);
        Statement stat = conn.createStatement();

        String[] TableType = {"_lights","_switchs","_number_clocks","_point_clocks","_weights"};
        String TableName ="";
        for(String i : TableType){
            TableName = String.valueOf(userId) + i;
            System.out.println(TableName);
            stat.executeUpdate("drop table if exists " + TableName);
        }
        stat.close();
        conn.close();
        String path = userLog + String.valueOf(userId) + ".log";
        File file = new File(path);
        if(file.exists()){
            System.out.println(file.delete());
            System.out.println("删除日志文件");
        }
    }

}
