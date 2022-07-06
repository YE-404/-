# -*- coding: UTF-8 -*-
import numpy as np
import os
import cv2
from glob import glob
from Point import Point, getCircle, circle
import json
import math
from datetime import timedelta
import yolomain
from ctypes import *
def load_image(image_path, image_size,clock_class): #根据仪表类型加载模板图片
    file_name = glob(image_path+"/"+clock_class+"*jpg")
    sample = []
    file_name.sort()
    for file_ in file_name:
        pic = cv2.imread(file_) #.astype(np.float32)
        #pic = cv2.resize(pic, (image_size, image_size), interpolation=cv2.INTER_CUBIC)
        sample.append(pic)
    return sample

def load_json(image_path,target_index,clock_class):
    json_name = image_path+"/"+clock_class+str(target_index)+".json"
    with open(json_name,'r') as load_f:
        load_json = json.load(load_f)
        return load_json
def calculate(image1, image2):
    # 灰度直方图算法
    # 计算单通道的直方图的相似值
    hist1 = cv2.calcHist([image1], [0], None, [256], [0.0, 255.0])
    hist2 = cv2.calcHist([image2], [0], None, [256], [0.0, 255.0])
    # 计算直方图的重合度
    degree = 0
    for i in range(len(hist1)):
        if hist1[i] != hist2[i]:
            degree = degree + \
                     (1 - abs(hist1[i] - hist2[i]) / max(hist1[i], hist2[i]))
        else:
            degree = degree + 1
    degree = degree / len(hist1)
    return degree

def classify_hist_with_split(image1, image2, size=(256, 256)):
    # RGB每个通道的直方图相似度
    # 将图像resize后，分离为RGB三个通道，再计算每个通道的相似值
    image1 = cv2.resize(image1, size)
    image2 = cv2.resize(image2, size)
    sub_image1 = cv2.split(image1)
    sub_image2 = cv2.split(image2)
    sub_data = 0
    for im1, im2 in zip(sub_image1, sub_image2):
        sub_data += calculate(im1, im2)
    sub_data = sub_data / 3
    return sub_data

def  indexOfSimilarity(get_image,template_image):
        H1 = cv2.calcHist([get_image], [1], None, [256],[0,256])
        H1 = cv2.normalize(H1, H1, 0, 1, cv2.NORM_MINMAX, -1) # 对图片进行归一化处理

        H2 = cv2.calcHist([template_image], [1], None, [256],[0,256])
        H2 = cv2.normalize(H2, H2, 0, 1, cv2.NORM_MINMAX, -1)
        similarity = cv2.compareHist(H1, H2,0)
        return similarity

def dHash(img):
    # 差值哈希算法
    # 缩放8*8
    img = cv2.resize(img, (9, 8))
    # 转换灰度图
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    hash_str = ''
    # 每行前一个像素大于后一个像素为1，相反为0，生成哈希
    for i in range(8):
        for j in range(8):
            if gray[i, j] > gray[i, j + 1]:
                hash_str = hash_str + '1'
            else:
                hash_str = hash_str + '0'
    return hash_str

def cmpHash(hash1, hash2):
    # Hash值对比
    # 算法中1和0顺序组合起来的即是图片的指纹hash。顺序不固定，但是比较的时候必须是相同的顺序。
    # 对比两幅图的指纹，计算汉明距离，即两个64位的hash值有多少是不一样的，不同的位数越小，图片越相似
    # 汉明距离：一组二进制数据变成另一组数据所需要的步骤，可以衡量两图的差异，汉明距离越小，则相似度越高。汉明距离为0，即两张图片完全一样
    n = 0
    # hash长度不同则返回-1代表传参出错
    if len(hash1) != len(hash2):
        return -1
    # 遍历判断
    for i in range(len(hash1)):
        # 不相等则n计数+1，n最终为相似度
        if hash1[i] != hash2[i]:
            n = n + 1
    return n



def surf_feature_point(get_image,template_image):
        y, x = template_image.shape[0:2]
        #get_image = cv2.resize(get_image, (x, y), interpolation=cv2.INTER_CUBIC)
        get_ori = get_image
        template_ori = template_image
        surf = cv2.SIFT_create(1000)
        keypoints1,descriptor1 = surf.detectAndCompute(template_image,None)
        keypoints2,descriptor2 = surf.detectAndCompute(get_image,None)
        try:
             print('descriptor1:',descriptor1.shape,'descriptor2',descriptor2.shape)
        except Exception as e:
             print(e)
             return e
        index_params = dict(algorithm = 0, trees = 10)
        search_params = dict(checks=10000)   # or pass empty dictionary 
        matcher = cv2.FlannBasedMatcher(index_params, search_params)
        matchePoints = matcher.knnMatch(descriptor1, descriptor2, k=2)
        #matchePoints = matcher.knnMatch(np.asarray(descriptor1,np.float32),np.asarray(descriptor2,np.float32), 2)
        print(len(matchePoints))
        goodMatchePoints = []
        for m, n in matchePoints:
    	    if m.distance < 0.4 * n.distance:
       	        goodMatchePoints.append(m)
        outImg = None
        outImg = cv2.drawMatches(template_image,keypoints1,get_image,keypoints2,goodMatchePoints,outImg,matchColor=(0,255,0))
        cv2.imwrite('./sample/result.jpg', outImg)
        print('goodMatchePoints:',len(goodMatchePoints))
        if len(goodMatchePoints) < 4 :  
            print('goodMatchePoints is lower than 4')
            return 0 
        imagePoints1=np.zeros(shape=(len(goodMatchePoints),2),dtype=np.float32)
        imagePoints2=np.zeros(shape=(len(goodMatchePoints),2),dtype=np.float32)

        for i in range(len(goodMatchePoints)):
            imagePoints1[i][0]=keypoints1[goodMatchePoints[i].queryIdx].pt[0]
            imagePoints1[i][1]=keypoints1[goodMatchePoints[i].queryIdx].pt[1]
       	    imagePoints2[i][0]=keypoints2[goodMatchePoints[i].trainIdx].pt[0]
            imagePoints2[i][1]=keypoints2[goodMatchePoints[i].trainIdx].pt[1]
	     #求取单应矩阵
        ransacReprojThreshold = 4
        H,_=cv2.findHomography(imagePoints2,imagePoints1,cv2.RANSAC,ransacReprojThreshold)#
        if H is None:
            print('the H is None')
            return 0
        imagewarp = cv2.warpPerspective(get_ori,H,(template_ori.shape[1],template_ori.shape[0]))
        imagewarp = cv2.resize(imagewarp, (x, y), interpolation=cv2.INTER_CUBIC)
        cv2.imwrite('./sample/convert.jpg', imagewarp)
        print("over")
        return imagewarp

def calculate_angle(img_list,orgImg,clock_class):
#对所有仪表进行挨个遍历
    if len(img_list) == 0:
        return -1
    for i in img_list:
        xmin = i[2]
        ymin = i[3]
        xmax = i[2] + i[4]
        ymax = i[3] + i[5]
        print('ymin:',ymin,'ymax:',ymax)
        #getImg = orgImg[xmin:xmax,ymin:ymax]
        getImg = orgImg[ymin:ymax,xmin:xmax]
        
        samples = load_image("./templateModel",image_size,clock_class)
        max_comfidence = -1
        target_index = 0
        optimal_tempImg = None

        for index, tempImg in enumerate(samples):
            comfidence = indexOfSimilarity(getImg, tempImg)
            print('index = ',index,'  comfidence = ',comfidence)
            if comfidence > max_comfidence:
                max_comfidence = comfidence
                optimal_tempImg = tempImg
                target_index = index + 1
        print(target_index)


        template_json = load_json("./templateModel",target_index,clock_class)
        '''scale_p_tmp = template_json['part_info_list'][0]['scale_points'] #if 'scale_points' in template_json else None
        scale_p_tmp = np.array(scale_p_tmp).reshape(-1, 2)
        circle_point = template_json['part_info_list'][0]['circle_point']
        p1 = Point(scale_p_tmp[0][0],scale_p_tmp[0][1])
        p2 = Point(scale_p_tmp[2][0],scale_p_tmp[2][1])
        p3 = Point(scale_p_tmp[-1][0],scale_p_tmp[-1][1])
        angle1 = math.atan2(p1.y - circle_point[1],p1.x - circle_point[0])* 180/math.pi
        angle2 = math.atan2(p2.y - circle_point[1],p2.x - circle_point[0])* 180/math.pi
        angle3 = math.atan2(p3.y - circle_point[1],p3.x - circle_point[0])* 180/math.pi
        print(angle1)
        print(angle2)
        print(angle3)'''
        convertImg = surf_feature_point(getImg,optimal_tempImg)

        point_list = []
        drawconvert = convertImg.copy()
        point_lab, point_img, point_loc, point_all_list = yolomain.yolo_detect(im=drawconvert,
            label_path='./cfg/number.names',
            config_path='./cfg/yolov4-number.cfg',
            weights_path='./cfg/yolov4-number.weights',
            confidence_thre=0.25,
            nms_thre=0.3)
        print(point_all_list)
        for l in point_all_list:
            if l[0] == 'pointer':
                point_list.append(l)
#虽然此处遍历，但当前只支持单指针仪表识别
        for z in point_list:
            xmin = z[2]
            ymin = z[3]
            xmax = z[2] + z[4]
            ymax = z[3] + z[5]
            pointImg = convertImg[ymin:ymax,xmin:xmax]
            cols = pointImg.shape[1]
            rows= pointImg.shape[0]
            pointImg = cv2.cvtColor(pointImg,cv2.COLOR_BGR2GRAY)
            cv2.imwrite('./ori.jpg',pointImg)
            C_img = np.asarray(pointImg, dtype=np.uint8)
            C_img = pointImg.ctypes.data_as(c_char_p)
            result = lib.mat_new(C_img,c_uint(rows),c_uint(cols))
            scale_range = template_json['part_info_list'][0]['value_range']
            angle_range = template_json['part_info_list'][0]['angle_range']
            value = result/angle_range*scale_range
            print('读数为：',value)

            return value

def calculate_angle_flask(getImg,clock_class,net):
        lib = CDLL("./librectify_tools_try.so")
        lib.mat_new.restype = c_double
        samples = load_image("./templateModel",500,clock_class)
        max_comfidence = -1
        target_index = 0
        optimal_tempImg = None
        for index, tempImg in enumerate(samples):
            comfidence = indexOfSimilarity(getImg, tempImg)
            if comfidence > max_comfidence:
                max_comfidence = comfidence
                optimal_tempImg = tempImg
                target_index = index + 1
        template_json = load_json("./templateModel",target_index,clock_class)
        convertImg = surf_feature_point(getImg,optimal_tempImg)
        point_list = []
        drawconvert = convertImg.copy()
        point_lab, point_img, point_loc, point_all_list = yolomain.yolo_detect(im=drawconvert,
            label_path='./cfg/number.names',
            net = net,
            confidence_thre=0.3,
            nms_thre=0.3)
        print(point_all_list)
        for l in point_all_list:
            if l[0] == 'pointer':
                point_list.append(l)
#虽然此处遍历，但当前只支持单指针仪表识别
        for z in point_list:
            xmin = z[2]
            ymin = z[3]
            xmax = z[2] + z[4]
            ymax = z[3] + z[5]
            pointImg = convertImg[ymin:ymax,xmin:xmax]
            cols = pointImg.shape[1]
            rows= pointImg.shape[0]
            pointImg = cv2.cvtColor(pointImg,cv2.COLOR_BGR2GRAY)
            cv2.imwrite('./ori.jpg',pointImg)
            C_img = np.asarray(pointImg, dtype=np.uint8)
            C_img = pointImg.ctypes.data_as(c_char_p)
            result = lib.mat_new(C_img,c_uint(rows),c_uint(cols))
            print('result',result)
            scale_range = template_json['part_info_list'][0]['value_range']
            angle_range = template_json['part_info_list'][0]['angle_range']
            value = result/angle_range*scale_range
            print('读数为：',value)
            return value



if __name__=='__main__':
    image_size = 500
    lib = CDLL("/home/sy/try_ws/src/kilox_pantilt/src/kilox_libs/rectify/src/librectify_tools_try.so")
    lib.mat_new.restype = c_double

    #clock_class = 'A'
    orgImg = cv2.imread('./img/waitclass7.jpg')
    drawImg = orgImg.copy()
    lab, img, loc, all_list = yolomain.yolo_detect(im = drawImg)
    all_list.sort(key=lambda x:x[2])
    img_list_A = []
    img_list_V = []
    for j in all_list:
        if j[0] == 'point_clock_A':
            img_list_A.append(j)
        
        elif j[0] == 'point_clock_V':
            img_list_V.append(j)

    calculate_angle(img_list_A,orgImg,'A')
    calculate_angle(img_list_V,orgImg,'V')





        
    

    
    
