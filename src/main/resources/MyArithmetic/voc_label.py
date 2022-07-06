import xml.etree.ElementTree as ET
import pickle
import os
from os import listdir, getcwd
from os.path import join

classes = ["red_on","green_on","yellow_on","white_on","red_off",
               "green_off","yellow_off","white_off","switch_right","switch_left",
               "point_clock_V","point_clock_A","number_clock_V","number_clock_A","red_switch_down",
               "red_switch_right","air_switch_down","air_switch_up"]

def writeDataDocument(darknetPath,serialNumber,weightSavePath):
    dataPath = darknetPath + 'VOC%s/%s_obj.data'%(serialNumber, serialNumber)
    list_file = open(dataPath, 'w')
    list_file.write("classes = " + str(len(classes)) + '\n')
    list_file.write("train  = " + darknetPath + 'VOC%s/%s_train.txt'%(serialNumber, serialNumber) + '\n')
    list_file.write("valid  = " + darknetPath + 'VOC%s/%s_val.txt'%(serialNumber, serialNumber) + '\n')
    list_file.write("names  = " + darknetPath + 'VOC%s/%s_obj.names'%(serialNumber, serialNumber) + '\n')
    list_file.write("backup  = " + weightSavePath + '\n')
    return dataPath

def writeNameDocument(darknetPath,serialNumber):
    list_file = open(darknetPath + 'VOC%s/%s_obj.names'%(serialNumber, serialNumber), 'w')
    for i in classes:
        list_file.write(i + '\n')


def convert(size, box):
    dw = 1./(size[0])
    dh = 1./(size[1])
    x = (box[0] + box[1])/2.0 - 1
    y = (box[2] + box[3])/2.0 - 1
    w = box[1] - box[0]
    h = box[3] - box[2]
    x = x*dw
    w = w*dw
    y = y*dh
    h = h*dh
    return (x,y,w,h)

def convert_annotation(year, image_id,darknetPath,image_set):
    if image_set == 'train':
        fileType = 'train_xml'
    elif image_set == 'val':
        fileType = 'test_xml'

    in_file = open(darknetPath + 'VOC%s/Annotations/%s/%s.xml'%(year, fileType, image_id))
    out_file = open(darknetPath + 'VOC%s/labels/train_pic/%s.txt'%(year, image_id), 'w')
    tree = ET.parse(in_file)
    root = tree.getroot()
    size = root.find('size')
    w = int(size.find('width').text)
    h = int(size.find('height').text)

    for obj in root.iter('object'):
        difficult = obj.find('difficult').text
        cls = obj.find('name').text
        if cls not in classes or int(difficult)==1:
            continue
        cls_id = classes.index(cls)
        xmlbox = obj.find('bndbox')
        b = (float(xmlbox.find('xmin').text), float(xmlbox.find('xmax').text), float(xmlbox.find('ymin').text), float(xmlbox.find('ymax').text))
        bb = convert((w,h), b)
        out_file.write(str(cls_id) + " " + " ".join([str(a) for a in bb]) + '\n')


def theme(serialNumber,darknetPath,weightSavePath):
    sets=[(str(serialNumber), 'train'), (str(serialNumber), 'val')]
    
    #wd = getcwd()

    for year, image_set in sets:
        if image_set == 'train':
            fileType = 'train_pic'
        elif image_set == 'val':
            fileType = 'test_pic'
        if not os.path.exists(darknetPath + 'VOC%s/labels/train_pic/'%(year)):
            os.makedirs(darknetPath + 'VOC%s/labels/train_pic/'%(year))
        image_ids = open(darknetPath + 'VOC%s/ImageSets/Main/%s.txt'%(year, image_set)).read().strip().split()
        list_file = open(darknetPath + 'VOC%s/%s_%s.txt'%(year, year, image_set), 'w')
        for image_id in image_ids:
            list_file.write(darknetPath + 'VOC%s/JPEGImages/%s/%s.jpg\n'%(year, fileType, image_id))
            convert_annotation(year, image_id, darknetPath, image_set)
        list_file.close()
    writeNameDocument(darknetPath,serialNumber)
    dataPath = writeDataDocument(darknetPath,serialNumber,weightSavePath)
    return dataPath

#os.system("cat 2016_train.txt  > train.txt")
#os.system("cat 2016_val.txt  > val.txt")

