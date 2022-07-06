package com.myproject.systemdemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myproject.systemdemo.domain.Apparatus;
import com.myproject.systemdemo.domain.Indicator;
import com.myproject.systemdemo.domain.Task;
import com.myproject.systemdemo.mapper.UserMapper;
import com.myproject.systemdemo.server.WriteTaskImpl;
import com.myproject.systemdemo.thriftDemo.ThriftClient;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SubscribeSample {

    @Autowired
    private WriteTaskImpl writeTask;

    @Autowired
    private ThriftClient thriftClient;

    @Autowired
    private UserMapper userMapper;


    String HOST = "tcp://127.0.0.1:1883";
    String TOPIC = "mqtt11";
    int qos = 1;
    String clientid = "subClient112";
    String result = "";
    MqttClient client;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public MqttClient productClient(String Host, String clientid) throws MqttException {
        client = new MqttClient(HOST, clientid, new MemoryPersistence());
        client.connect(productOptions());
        return client;
    }
    public  MqttConnectOptions productOptions(){
        // MQTT的连接设置
        MqttConnectOptions options = new MqttConnectOptions();
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
        options.setCleanSession(false);
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(10);
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval(20);
        return options;
    }

    public void publish(MqttClient client,String TOPIC,String mes){
        MqttMessage message = new MqttMessage(mes.getBytes());
        message.setQos(1);
        try {
            client.publish(TOPIC,message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(MqttClient client,String TOPIC){//String clientid, String TOPIC,Integer userId
        try {
            // host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
//            client = new MqttClient(HOST, clientid, new MemoryPersistence());
            // 设置回调函数


            client.setCallback(new MqttCallback() {
                public void connectionLost(Throwable cause) {
                    System.out.println("connectionLost");
                }
                public void messageArrived(String topic, MqttMessage message)  {//throws Exception
                    String getMessage = new String(message.getPayload());
                    String[] typeList = new String[0];
                    if(topic.contains("showPicture")){
                        result = getMessage;
                        System.out.println("yes");
                    }else if(topic.contains("insertTable")){
                        JSONObject jsonObject= JSON.parseObject(getMessage, JSONObject.class);
                        String id = jsonObject.get("id").toString();
                        String userId = jsonObject.get("userId").toString();
                        String taskContent = jsonObject.get("taskContent").toString();
                        String taskName = jsonObject.get("taskName").toString();
                        String picture = jsonObject.get("picture").toString();
                        switch (taskContent) {
                            case "light":
                                typeList = new String[]{"red", "green", "yellow", "white"};
                                break;
                            case "on_off":
                                taskContent = "switch";
                                typeList = new String[]{"left", "right"};
                                break;
                            case "number":
                                typeList = new String[]{"ammeter", "voltmeter"};
                                System.out.println("number");
                                break;
                            case "point":
                                Task task = userMapper.selectTaskByTaskName(userId + "_task", taskName);
                                System.out.println(task);
                                typeList = new String[]{String.valueOf(task.getMinValue()), String.valueOf(task.getMaxValue()), String.valueOf(task.getZeroLine()),
                                        String.valueOf(task.getValueRange()), String.valueOf(task.getCenterX()), String.valueOf(task.getCenterY())};
                                System.out.println("point");
                                break;
                            default:
                                System.out.println("other");
                                break;
                        }
                        String getResult = thriftClient.getResult(picture, taskContent, typeList, String.valueOf(userId));
                        Indicator[] indicators = JSON.parseObject(getResult, Indicator[].class);
                        for(Indicator i : indicators){
                            String result;
                            if(taskContent.equals("light") || taskContent.equals("switch") ){
                                result = i.getClass_();

                            }else{
                                result = i.getResult();
                            }
                            Apparatus apparatus = new Apparatus(Integer.parseInt(userId),taskName,taskContent,result);
                            System.out.println(apparatus);
                            userMapper.addApparatus(userId+"_apparatus",apparatus);

                        }
                    }

                }
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("deliveryComplete---------" + token.isComplete());
                }
            });

            // 订阅消息
            client.subscribe(TOPIC, qos);
            System.out.println("订阅完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void close(MqttClient client){
        try {
//            MqttClient client = new MqttClient(HOST, clientid, new MemoryPersistence());
//            MqttConnectOptions options = new MqttConnectOptions();
//            options.setCleanSession(false);
//            client.connect(options);
//            client.disconnect();
            client.close();

            System.out.println("close successfully");

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws MqttException {
        SubscribeSample subscribeSample = new SubscribeSample();
        //subscribeSample.subscribe("15-robot","15-target",15);
    }
}
