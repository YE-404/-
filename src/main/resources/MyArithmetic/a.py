#-*- coding: utf-8 -*-
# -*- coding: UTF-8 -*-
from ctypes import*
import base64
import cv2
import numpy as np
import json
import pyzbar.pyzbar as pyzbar
from PIL import Image,ImageEnhance
import urllib.request
import multiprocessing

import os
import sys

from example import format_data
from example import ttypes
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer
from thrift.server import TProcessPoolServer
from thrift import Thrift
from example.darknet_data import Client
import time

import yolomain
from panorama import Stitcher
from panorama import remove_space
from matchImg import calculate_angle_flask
__HOST = '127.0.0.1' #localhost
__PORT = 9000

#HOST1 = '127.0.0.1'
#PORT1 = 6000

config_path = './cfg/yolov4-obj-all.cfg'
weights_path = './cfg/yolov4-obj-all.weights'
config_number_path='./cfg/yolov4-number.cfg'
weights_number_path='./cfg/yolov4-number.weights'

net = cv2.dnn.readNetFromDarknet(config_path, weights_path)
net_number = cv2.dnn.readNetFromDarknet(config_number_path, weights_number_path)

def get_number(class_,my_class_list):
       #reading_list = []
       
       if class_ == "number_clock_V":
               strl = "V"
       elif class_ == "number_clock_A":
               strl = "A"
       else:
               strl = "unknow"
       switcher = {
               "zero": 0,
               "one":1,
               "two": 2,
               "three":3,
               "four": 4,
               "five":5,
               "six": 6,
               "seven":7,
               "eight": 8,
               "nine":9,
       }
       n = 1
       total = 0   
       for i in my_class_list:
           count = switcher.get(i,"nothing")  
           total = total + count/n
           n = n*10
       total = round(total,3)
       one_clock_reading = total
       #reading_list.append(one_clock_reading)
       return one_clock_reading,strl

def getPointResult(img,needLoc,needMessge,clock_class):
    input_ = []
    output = []
    for i in range(len(needLoc)):
        my_class_list = []
        xmin = needLoc[i][0]
        ymin = needLoc[i][1]
        xmax = needLoc[i][0] + needLoc[i][2]
        ymax = needLoc[i][1] + needLoc[i][3]
        img_tep = img[int(ymin):int(ymax), int(xmin):int(xmax)]
        number = calculate_angle_flask(img_tep,clock_class,net_number)
        print('number',number)
        M_number = str(number) + clock_class
        output.append([needMessge[i][0],M_number,needLoc[i][0],needLoc[i][1],needLoc[i][2],needLoc[i][3]])
        input_.append([needMessge[i][0],number,needLoc[i][0],needLoc[i][1],needLoc[i][2],needLoc[i][3]])
    print(output)
    return output,input_


def lightAnalyze(status,img):
        light_class = set(['red_on','green_on','yellow_on','white_on','red_off','green_off','yellow_off','white_off'])
        lab, img, loc, all_message = yolomain.yolo_detect(im = img,pathIn = None,net = net)
        if status == 'red':
            light_class = set(['red_on','red_off'])
        elif status == 'green':
            light_class = set(['green_on','green_off'])
        elif status == 'yellow':
            light_class = set(['yellow_on','yellow_off'])
        elif status == 'white':
            light_class = set(['white_on','white_off'])
        light_type = []
        for j in range(len(all_message)):
            if all_message[j][0] in light_class:
                light_type.append(all_message[j][0])
        print(light_type)
        answer = {"result":light_type}
        return json.dumps(answer, sort_keys=False, indent=2)
        
def switchAnalyze(status,img):
        lab, img, loc, all_message = yolomain.yolo_detect(im = img,pathIn = None,net = net)
        switch_class = set(['switch_left','switch_right'])
        if status == 'left':
            switch_class = ['switch_left']
        elif status == 'right':
            switch_class = ['switch_right']
        switch_type = []
        print(all_message)
        for j in range(len(all_message)):
            if all_message[j][0] == switch_class or all_message[j][0] in switch_class:
                switch_type.append(all_message[j][0])
        answer = {"result":switch_type}
        return json.dumps(answer, sort_keys=False, indent=2)
          
def numberAnalyze(status,img):
        lab, img, loc, all_list = yolomain.yolo_detect(im = img,pathIn = None,net = net)
        clock_class = set(['number_clock_A','number_clock_V'])
        if status == 'ammeter':
            clock_class = set(['number_clock_A'])
        elif status == 'voltmeter':
            clock_class = set(['number_clock_V'])
        result = []
        needMessge = []
        needLoc = []
        for j in range(len(all_list)):
            if all_list[j][0] in clock_class:
                needMessge.append(all_list[j])
                needLoc.append(loc[j])
        output = []
        input_ = []
        for i in range(len(needLoc)):
            my_class_list = []
            xmin = needLoc[i][0]
            ymin = needLoc[i][1]
            xmax = needLoc[i][0] + needLoc[i][2]
            ymax = needLoc[i][1] + needLoc[i][3]
            img_tep = img[int(ymin):int(ymax), int(xmin):int(xmax)]
            cv2.imwrite('./look.jpg',img_tep)
            my_lab, my_img, my_loc, my_all_list = yolomain.yolo_detect(im=img_tep,
                                   label_path='./cfg/number.names',
                                   net = net_number,
                                   confidence_thre=0.8,
                                   nms_thre=0.6)  
            my_all_list.sort(key=lambda x:x[2]) 
                
            for j in my_all_list:
                my_class_list.append(j[0])
            number,strl = get_number(needMessge[i][0],my_class_list)
            M_number = str(number)+strl
            result.append(M_number)
          
            output.append([needMessge[i][0],M_number,needLoc[i][0],needLoc[i][1],needLoc[i][2],needLoc[i][3]])
            input_.append([needMessge[i][0],number,needLoc[i][0],needLoc[i][1],needLoc[i][2],needLoc[i][3]])
        answer = {"result":result}
        return json.dumps(answer, sort_keys=False, indent=2)

def pointAnalyze(status,img):
        lab, img, loc, all_list = yolomain.yolo_detect(im = img,pathIn = None,net = net)
        A_output = []
        V_output = []
        A_input = []
        V_input = []
        needMessge_A = []
        needLoc_A = []
        needMessge_V = []
        needLoc_V = []
        print(all_list)
        for j in range(len(all_list)):
            if all_list[j][0] == 'point_clock_A':
                needMessge_A.append(all_list[j])
                needLoc_A.append(loc[j])
                A_output,A_input = getPointResult(img,needLoc_A,needMessge_A,'A')
            elif all_list[j][0] == 'point_clock_V':
                needMessge_V.append(all_list[j])
                needLoc_V.append(loc[j])
                V_output,V_input = getPointResult(img,needLoc_V,needMessge_V,'V')
        input_ = []
        output = []
        result = []
        if status == 'ammeter':
            input_ = A_input
            output = A_output
        elif status == 'voltmeter':
            input_ = V_input
            output = V_output
        elif status == 'all':
            input_ = V_input + A_input
            output = V_output + A_output
        for i in output:
            result.append(i[1])
        print('result',result)
        answer = {"result":result}
        return json.dumps(answer, sort_keys=False, indent=2)



'''class FormatDataHandler(object):
    def GetImageAnalysisResult(self, jsonParm): 
        allJsonMes = json.loads(jsonParm)
        checkType = allJsonMes["checkType"]#检测类型
        status = allJsonMes["screenType"]#过滤类型
        img = allJsonMes['imgMessage']
        img = base64.b64decode(img)
        img = np.frombuffer(img, np.uint8)
        img=cv2.imdecode(img, flags = 1)#图片
        if checkType == "light":
            result = lightAnalyze(status,img)
        elif checkType == "switch":
            result = switchAnalyze(status,img)
        elif checkType == "number":
            result = numberAnalyze(status,img)
        elif checkType == "point":
            result = pointAnalyze(status,img)


        else:
            print("no")
            result = {"result":"wrong"}

        return result
    def Hello(self):
            #exit()
            return 'Hello'
'''

def GetImageAnalysisResult(jsonParm): 
        print("in")
        allJsonMes = json.loads(jsonParm)
        checkType = allJsonMes["checkType"]#检测类型
        status = allJsonMes["screenType"]#过滤类型
        img = allJsonMes['imgMessage']
        img = base64.b64decode(img)
        img = np.frombuffer(img, np.uint8)
        img=cv2.imdecode(img, flags = 1)#图片
        if checkType == "light":
            result = lightAnalyze(status,img)
        elif checkType == "switch":
            result = switchAnalyze(status,img)
        elif checkType == "number":
            result = numberAnalyze(status,img)
        elif checkType == "point":
            result = pointAnalyze(status,img)



'''if __name__ == '__main__':
    handler = FormatDataHandler()
    processor = format_data.Processor(handler)
    transport = TSocket.TServerSocket(__HOST, __PORT)
    # 传输方式，使用buffer
    tfactory = TTransport.TBufferedTransportFactory()
    # 传输的数据类型：二进制
    pfactory = TBinaryProtocol.TBinaryProtocolFactory()
     
    # 创建一个thrift 服务
    #rpcServer = TServer.TSimpleServer(processor,transport, tfactory, pfactory)
    rpcServer = TProcessPoolServer.TProcessPoolServer(processor, transport, tfactory, pfactory)
    print('Starting the rpc server at', __HOST,':', __PORT)

    rpcServer.serve()
    
    print('done')'''


