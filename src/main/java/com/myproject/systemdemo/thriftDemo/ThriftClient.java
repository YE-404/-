package com.myproject.systemdemo.thriftDemo;

import com.alibaba.fastjson.JSON;
import com.myproject.systemdemo.domain.Directory;
import com.myproject.systemdemo.mapper.UserMapper;
import com.myproject.systemdemo.tools.ZipUtil;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class ThriftClient {

    private TransmitMes transmitMes = new TransmitMes();

    private Directory directory = new Directory();

    @Autowired
    private UserMapper userMapper;

    @Value("${darknetPath}")
    private String darknetPath;

    @Value("${weightDirectory}")
    private String weightSavePath;

    public String startClient(String jsonMessage) {
        String result="";
        TTransport transport = new TSocket("127.0.0.1", 9000);
        TProtocol protocol = new TBinaryProtocol(transport);
        format_data.Client client = new format_data.Client(protocol);
        System.out.println("thrift client connext server at 9000 port ");
        try {
            transport.open();
            result = client.GetImageAnalysisResult(jsonMessage);
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        transport.close();
        transport = null;
        protocol = null;
        client = null;
        System.out.println("thrift client close connextion");
        return result;
    }

    public String trainClient(String jsonMessage) {
        String result="";

        TTransport transport = new TSocket("127.0.0.1", 9000);
        TProtocol protocol = new TBinaryProtocol(transport);
        format_data.Client client = new format_data.Client(protocol);
        System.out.println("thrift client connext server at 9000 port ");
        try {
            transport.open();
            result = client.train(jsonMessage);
            transport.close();
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        transport.close();
        //System.out.println("no off");
        transport = null;
        protocol = null;
        client = null;
        return result;
    }


    public String trainWeight(int serialNumber, Integer userId, int id){
        String VocDirectory = darknetPath + "/VOC" + serialNumber;
        String trainPicDirectory = VocDirectory + "/JPEGImages/train_pic/";
        String testPicDirectory = VocDirectory + "/JPEGImages/test_pic/";
        String trainXmlDirectory = VocDirectory + "/Annotations/train_xml/";
        String testXmlDirectory = VocDirectory + "/Annotations/test_xml/";
        String weightDirectory = userMapper.selectWeightMesById("weight",userId + "_weights","id" , String.valueOf(id));
        String cfgDirectory = userMapper.selectWeightMesById("cfg",userId + "_weights","id" , String.valueOf(id));
        String newCfgDirectory = ZipUtil.copyFileToNewPath(cfgDirectory,weightSavePath + userId + "/" + userId + "-" +  ZipUtil.cutFileSuffixName(cfgDirectory));
        directory.init(trainPicDirectory,testPicDirectory,trainXmlDirectory,testXmlDirectory,weightDirectory,newCfgDirectory,darknetPath,serialNumber,weightSavePath+userId);
        String jsonString = JSON.toJSONString(directory);

//        userMapper.updateMessageString(userId,"train_status", String.valueOf(id));//在user表中置入训练状态
//        userMapper.setState(userId + "_weights","train",true,"id",String.valueOf(id));//在x_weights表中置入训练状态

        //training...
        String result = trainClient(jsonString);

//        userMapper.updateMessageString(userId,"train_status","0");//在user表中清除训练状态
//        userMapper.setState(userId + "_weights","train",false,"id",String.valueOf(id));//在x_weights表中置入训练状态

        if(result.equals("wrong")){
            System.out.println("train wrong!");
        }else{
            System.out.println(result);
            userMapper.updateWeightMessage(userId+"_weights","weight",result,id);
        }

        return result;
    }



    public  String getResult(String base64Str,String checkType,String[] screenType,String userId){
        //ThriftClient client = new ThriftClient();
        String indicatorCfg = userMapper.selectWeightMesByCondition("cfg",userId + "_weights","Indicator",true);
        String indicatorWeight = userMapper.selectWeightMesByCondition("weight",userId + "_weights","Indicator",true);
        String insideCfg = userMapper.selectWeightMesByCondition("cfg",userId + "_weights","Inside",true);
        String insideWeight = userMapper.selectWeightMesByCondition("weight",userId + "_weights","Inside",true);

        if(indicatorCfg == null || indicatorWeight == null) return "NoIndicator";
        if(insideCfg == null || insideWeight == null) return "NoInside";
        transmitMes.init(base64Str,screenType,checkType,userId,indicatorCfg,indicatorWeight,insideCfg,insideWeight);
        String jsonString = JSON.toJSONString(transmitMes);
        String result = startClient(jsonString);
        System.out.println(result);
        return result;
    }



    public static void main(String[] args) {
        System.out.println("thrift client init ");
        ThriftClient client = new ThriftClient();
        System.out.println("thrift client start ");
        String base64Str = BaseImg.imageToBase64Str("/home/sy/try_ws/src/darknet_ros/darknet/img/00197.jpg");
        TransmitMes transmitMes = new TransmitMes();
        //System.out.println(client.getResult(base64Str,"light","all"));

//        transmitMes.setImgMessage(base64Str);
//        transmitMes.setCheckType("point");
//        transmitMes.setScreenType("all");
//        String jsonString = JSON.toJSONString(transmitMes);
//        //System.out.println(jsonString);
//        client.startClient(jsonString);
//        System.out.println("thrift client end ");

    }
}
