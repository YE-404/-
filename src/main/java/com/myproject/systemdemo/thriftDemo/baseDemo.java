package com.myproject.systemdemo.thriftDemo;

import java.io.FileNotFoundException;

public class baseDemo {
    public static void main(String[] args) throws FileNotFoundException {
        //FileInputStream fis = new FileInputStream("/home/sy/try_ws/src/darknet_ros/darknet/img/00099.jpg");
        //String imgStr = "";
        //boolean flag = baseImg.base64StrToImage(imgStr,"/home/sy/try_ws/src/darknet_ros/darknet/img/00099.jpg");

        String base64Str = BaseImg.imageToBase64Str("/home/sy/try_ws/src/darknet_ros/darknet/img/change.jpg");
        boolean flag = BaseImg.base64StrToImage(base64Str,"/home/sy/try_ws/src/darknet_ros/darknet/img/repeat.jpg");
        System.out.println(flag);
    }
}
