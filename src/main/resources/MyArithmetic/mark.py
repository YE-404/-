import os

def mark(trainPicDirectory,testPicDirectory,darknetPath,serialNumber):
    source_folder_train = trainPicDirectory
    source_folder_test = testPicDirectory
    dest = darknetPath + 'VOC' + str(serialNumber) + '/ImageSets/Main/train.txt'
    dest2= darknetPath + 'VOC' + str(serialNumber) + '/ImageSets/Main/val.txt' 
    if os.path.exists(dest):
        os.remove(dest)
    if os.path.exists(dest2):
        os.remove(dest2)
    file_list_train=os.listdir(source_folder_train)
    file_list_test=os.listdir(source_folder_test)
    train_file=open(dest,'a')
    val_file=open(dest2,'a')
    for file_obj in file_list_train:                
        file_path=os.path.join(source_folder_train,file_obj) 
        file_name,file_extend=os.path.splitext(file_obj)
        file_num=int(file_name) 
        train_file.write(file_name+'\n')

    for file_obj in file_list_test:                
        file_path=os.path.join(source_folder_test,file_obj) 
        file_name,file_extend=os.path.splitext(file_obj)
        file_num=int(file_name)
        val_file.write(file_name+'\n')
    

'''source_folder='./VOCdevkit/VOC2007/JPEGImages/'
dest='./VOCdevkit/VOC2007/ImageSets/Main/train.txt'
dest2='./VOCdevkit/VOC2007/ImageSets/Main/val.txt' 
document = 5360
if os.path.exists(dest):
    os.remove(dest)
if os.path.exists(dest2):
    os.remove(dest2)
file_list=os.listdir(source_folder) 
train_file=open(dest,'a')
val_file=open(dest2,'a')
for file_obj in file_list:                
    file_path=os.path.join(source_folder,file_obj) 
    file_name,file_extend=os.path.splitext(file_obj)
    file_num=int(file_name) 
    if(file_num<=document):                     
        train_file.write(file_name+'\n')  
    else:
        val_file.write(file_name+'\n')
train_file.close()
val_file.close()'''
