package com.myproject.systemdemo.server;

import com.alibaba.fastjson.JSON;
import com.myproject.systemdemo.domain.Search;
import com.myproject.systemdemo.domain.Task;
import com.myproject.systemdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import java.io.FileOutputStream;
import java.io.OutputStream;

@Service
public class WriteTaskImpl {
    @Autowired
    private UserMapper userMapper;

    @Value("${taskDirectory}")
    private String taskDirectory;

    @Value("${netTaskStore}")
    private String netTaskStore;

    public void writeTask(Task task){
        //Task task = JSON.parseObject(mes, Task.class);
        Integer userId = task.getUserId();
        String searchType = userId + "_task";
        String taskName = task.getTaskName();
        //System.out.println(GenerateImage(task.getPicture(),userId,taskName));
        String taskUrl = netTaskStore + userId + "/" + task.getPicture();
        System.out.println(userMapper.addTask(searchType,taskName,task.getPosition(),taskUrl,task.getTaskContent(),task.getUserId()));
    }


    public boolean GenerateImage(String imgStr, Integer id, String taskName)
    {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            String imgFilePath = taskDirectory + id + "/" + taskName + ".jpg";//新生成的图片
            System.out.println(imgFilePath);
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }


}
