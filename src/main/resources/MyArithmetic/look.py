import cv2
import os
from glob import glob
'''config_path = './cfg/yolov4-obj-all.cfg'
weights_path = './cfg/yolov4-obj-all.weights'


net = cv2.dnn.readNetFromDarknet(config_path, weights_path)
del net
a=0
while(1):
    a+=1'''

'''print(os.path.abspath ("a.py") )
os.system("gnome-terminal -- bash -c  'mkdir 111'")
list_file = open("train.sh", 'w')
list_file.write("#!/bin/bash" + '\n')
list_file.write()'''

my_file = glob("./cfg/yolov4-obj-number*")
print(my_file)
print(os.path.exists("./cfg/yolov4-obj-number*"))

#strl = gnome-terminal -t "title-name" -- bash -c "./darknet detector train cfg/obj_all.data cfg/yolov4-obj-all.cfg backup/yolov4-obj-all.weights -clear;sleep 20s"
#strl = 'https://www.baidu.com/pdf/abcdefg.pdf'
#print(strl.split('/')[-1].split('.')[0])
#print(os.path.exists('look1.jpg'))



