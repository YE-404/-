package com.myproject.systemdemo;



//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

//import org.apache.logging.log4j.Logger;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.tomcat.util.http.fileupload.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

////    public static final Logger LOGGER = LogManager.getLogger(logTry.class);
//public static void main(String[] args) {
//        ThreadContext.put("logFileName", "David");
//        Logger LOGGER = LogManager.getLogger(logTry.class);
//        //System.setProperty("id","1");
//        LOGGER.error("error");
//        LOGGER.warn("warn");
//        LOGGER.info("info");
//  }


public class logTry {


public static void main(String[] args) throws IOException {
    logTry logTryObject = new logTry();
    File file = new File("/home/sy/darknet-master/scripts/VOCdevkit/VOC2642/Annotations/test_xml/");
    String path = file.getName();

    System.out.println(path.substring(0,path.lastIndexOf("_")));
//    File[] fs = file.listFiles();
//    Arrays.sort(fs,logTryObject.comparatorGrade);
////    Arrays.stream(fs).sorted();
//    System.out.println(fs.length);
//    for(File f : fs){
//        System.out.println(f.getName());
//    }
}

}
