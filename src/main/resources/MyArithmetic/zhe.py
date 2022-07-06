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
import imutils
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

def translateChinese(msg):
       switcher = {
               "red_on": "红灯亮",
               "red_off":"红灯灭",
               "green_on": "绿灯亮",
               "green_off":"绿灯灭",
               "yellow_on": "黄灯亮",
               "yellow_off":"黄灯灭",
               "white_on": "白灯亮",
               "white_off":"白灯灭",
               "switch_left":"开关左",
               "switch_right":"开关右",
               "number_clock_A":"数字电流表",
               "number_clock_V":"数字电压表",
               "point_clock_A":"指针电流表",
               "point_clock_V":"指针电压表",
       }
       result = switcher.get(msg,"nothing") 
       return result


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

def getPointResult(img,needLoc,needMessge,clock_class,url):

    resultData = []
    for i in range(len(needLoc)):
        my_class_list = []
        xmin = needLoc[i][0]
        ymin = needLoc[i][1]
        xmax = needLoc[i][0] + needLoc[i][2]
        ymax = needLoc[i][1] + needLoc[i][3]
        img_tep = img[int(ymin):int(ymax), int(xmin):int(xmax)]
        number = calculate_angle_flask(img_tep,clock_class,net_number)
        M_number = str(number) + clock_class
        convert = translateChinese(needMessge[i][0])
        point_type = {"class_":convert,"result":M_number,"saveUrl":url}
        resultData.append(point_type)
    return resultData


def lightAnalyze(status,img,userId):
        #light_class = set(['red_on','green_on','yellow_on','white_on','red_off','green_off','yellow_off','white_off'])
        light_class = []
        lab, img, loc, all_message = yolomain.yolo_detect(im = img,pathIn = None,net = net)
        time_now = time.strftime("%Y-%m-%d-%H:%M", time.localtime())
        imgName = "/light-" + time_now + ".jpg"
        cv2.imwrite("/home/sy/IMAGE/lights/" + userId + imgName, img)
        url = "http://127.0.0.1/lights/" + userId + imgName
        if 'red' in status:
            light_class +=   ['red_on','red_off']
        if 'green' in status:
            light_class += ['green_on','green_off']
        if 'yellow' in status:
            light_class += ['yellow_on','yellow_off']
        if 'white' in status:
            light_class += ['white_on','white_off']
        resultData = []
        for j in range(len(all_message)):
            if all_message[j][0] in light_class:
                convert = translateChinese(all_message[j][0])
                light_type = {"class_":convert,"confidence":all_message[j][1],"saveUrl":url}
                resultData.append(light_type)
        if resultData == []:
            resultData = [{"class_":None,"confidence":None,"saveUrl":url}]
        print(resultData)
        return json.dumps(resultData, sort_keys=False, indent=2)
        
def switchAnalyze(status,img,userId):
        lab, img, loc, all_message = yolomain.yolo_detect(im = img,pathIn = None,net = net)
        switch_class = []
        time_now = time.strftime("%Y-%m-%d-%H:%M", time.localtime())
        imgName = "/switch-" + time_now + ".jpg"
        cv2.imwrite("/home/sy/IMAGE/switchs/" + userId + imgName, img)
        url = "http://127.0.0.1/switchs/" + userId + imgName
        if 'left' in status:
            switch_class += ['switch_left']
        if 'right' in status:
            switch_class += ['switch_right']
        resultData = []
        for j in range(len(all_message)):
            if all_message[j][0] in switch_class:
                convert = translateChinese(all_message[j][0])
                switch_type = {"class_":convert,"confidence":all_message[j][1],"saveUrl":url}
                resultData.append(switch_type)
        if resultData == []:
            resultData = [{"class_":None,"confidence":None,"saveUrl":url}]
        print(resultData)
        return json.dumps(resultData, sort_keys=False, indent=2)
          
def numberAnalyze(status,img,userId):
        lab, img, loc, all_list = yolomain.yolo_detect(im = img,pathIn = None,net = net) #lab 没用 img 带框图   loc 框的坐标   all_list[类型，置信度，坐标]
        clock_class = []
        time_now = time.strftime("%Y-%m-%d-%H:%M", time.localtime())
        imgName = "/number_clock-" + time_now + ".jpg"
        cv2.imwrite("/home/sy/IMAGE/number_clocks/" + userId + imgName, img)
        url = "http://127.0.0.1/number_clocks/" + userId + imgName

        if 'ammeter' in status:
            clock_class += ['number_clock_A']
        if 'voltmeter' in status:
            clock_class += ['number_clock_V']
        result = []
        needMessge = []
        needLoc = []
        for j in range(len(all_list)):
            if all_list[j][0] in clock_class:
                needMessge.append(all_list[j])
                needLoc.append(loc[j])#保存坐标
        resultData = []
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
            convert = translateChinese(needMessge[i][0])
            number_type = {"class_":convert,"result":M_number,"saveUrl":url}
            resultData.append(number_type)
        print(resultData)
        return json.dumps(resultData, sort_keys=False, indent=2)

def pointAnalyze(status,img,userId):
        lab, img, loc, all_list = yolomain.yolo_detect(im = img,pathIn = None,net = net)
        clock_class = []
        time_now = time.strftime("%Y-%m-%d-%H:%M", time.localtime())
        imgName = "/point_clock-" + time_now + ".jpg"
        cv2.imwrite("/home/sy/IMAGE/point_clocks/" + userId + imgName, img)
        url = "http://127.0.0.1/point_clocks/" + userId + imgName

        if 'ammeter' in status:
            clock_class += ['point_clock_A']
        if 'voltmeter' in status:
            clock_class += ['point_clock_V']

        needMessge_A = []
        needLoc_A = []
        needMessge_V = []
        needLoc_V = []
        resultData_A = []
        resultData_V = []
        resultData = []
        for j in range(len(all_list)):
            if all_list[j][0] == 'point_clock_A' and 'point_clock_A' in clock_class:
                needMessge_A.append(all_list[j])
                needLoc_A.append(loc[j])
                resultData_A = getPointResult(img,needLoc_A,needMessge_A,'A',url)
                resultData += resultData_A
            elif all_list[j][0] == 'point_clock_V' and 'point_clock_V' in clock_class:
                needMessge_V.append(all_list[j])
                needLoc_V.append(loc[j])
                resultData_V = getPointResult(img,needLoc_V,needMessge_V,'V',url)
                resultData += resultData_V
        print(resultData)
        if resultData == []:
            resultData = [{"class_":None,"confidence":None,"saveUrl":url}]
        return json.dumps(resultData, sort_keys=False, indent=2)

def image_stitch(status,subImage,userId):
        if 'T' in status:
            isVertical = True #True or False
        elif 'F' in status:
            isVertical = False
        stitcher = Stitcher(isVertical)
        print('subImage: ',len(subImage))
        imageA = subImage[0]
        imageA = imutils.resize(imageA, width=imageA.shape[1], height=imageA.shape[0])
        resultImg = imageA
        resultData = []
        for image in subImage[1:]:
            image = imutils.resize(image, width=imageA.shape[1], height=imageA.shape[0])
            resultImg = stitcher.stitch([resultImg, image], showMatches=True)
            resultImg = remove_space(resultImg)

        time_now = time.strftime("%Y-%m-%d-%H:%M", time.localtime())
        imgName = "/spell-" + time_now + ".jpg"
        cv2.imwrite("/home/sy/IMAGE/spells/" + userId + imgName, resultImg)
        url = "http://127.0.0.1/spells/" + userId + imgName
        print(url)
        del stitcher
        del time_now
        resultData = [{"saveUrl":url}]
        
       
        return json.dumps(resultData, sort_keys=False, indent=2)



class FormatDataHandler(object):
    def GetImageAnalysisResult(self, jsonParm):
       resultData = [{"class_":None,"confidence":None,"saveUrl":None}]
       result = json.dumps(resultData, sort_keys = False, indent=2)
       return result
    def Hello(self):
            #exit()
            return 'Hello'





if __name__ == '__main__':
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
    
    print('done')


