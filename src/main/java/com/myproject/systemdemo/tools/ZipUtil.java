package com.myproject.systemdemo.tools;


import com.myproject.systemdemo.domain.FileHandleResponse;
import com.myproject.systemdemo.domain.User;
import com.myproject.systemdemo.mapper.UserMapper;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;



@Service
public class ZipUtil {
    @Autowired
    private UserMapper userMapper;

    @Value("${weightDirectory}")
    private String weightDirectory;

    public static void downloadZipFiles(HttpServletResponse response, List<String> srcFiles, String zipFileName) {
        try {
            response.reset(); // 重点突出
            response.setCharacterEncoding("UTF-8"); // 重点突出
            response.setContentType("application/x-msdownload"); // 不同类型的文件对应不同的MIME类型 // 重点突出
            // 对文件名进行编码处理中文问题
            zipFileName = new String(zipFileName.getBytes(), StandardCharsets.UTF_8);
            // inline在浏览器中直接显示，不提示用户下载
            // attachment弹出对话框，提示用户进行下载保存本地
            // 默认为inline方式
            response.setHeader("Content-Disposition", "attachment;filename=" + zipFileName);

            // --设置成这样可以不用保存在本地，再输出， 通过response流输出,直接输出到客户端浏览器中。
            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
            zipFile(srcFiles, zos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 压缩文件
     *
     * @param filePaths 需要压缩的文件路径集合
     * @throws IOException
     */
    private static void zipFile(List<String> filePaths, ZipOutputStream zos) {
        //设置读取数据缓存大小
        byte[] buffer = new byte[4096];
        try {
            //循环读取文件路径集合，获取每一个文件的路径
            for (String filePath : filePaths) {
                File inputFile = new File(filePath);
                //判断文件是否存在
                if (inputFile.exists()) {
                    //判断是否属于文件，还是文件夹
                    if (inputFile.isFile()) {
                        //创建输入流读取文件
                        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
                        //将文件写入zip内，即将文件进行打包
                        zos.putNextEntry(new ZipEntry(inputFile.getName()));
                        //写入文件的方法，同上
                        int size = 0;
                        //设置读取数据缓存大小
                        while ((size = bis.read(buffer)) > 0) {
                            zos.write(buffer, 0, size);
                        }
                        //关闭输入输出流
                        zos.closeEntry();
                        bis.close();
                    } else {  //如果是文件夹，则使用穷举的方法获取文件，写入zip
                        File[] files = inputFile.listFiles();
                        List<String> filePathsTem = new ArrayList<String>();
                        for (File fileTem : files) {
                            filePathsTem.add(fileTem.toString());
                        }
                        zipFile(filePathsTem, zos);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != zos) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String suffixOfFile(MultipartFile file){
        if(!file.isEmpty()){
            int begin = file.getOriginalFilename().indexOf(".");
            int last = file.getOriginalFilename().length();
            String a = file.getOriginalFilename().substring(begin, last);
            return a;
        }
        else{
            return null;
        }
    }

    public static FileHandleResponse unZipFiles(String unZipPath, String fileName, MultipartFile multipartFile,boolean random)throws IOException{
        FileHandleResponse fileHandleResponse = new FileHandleResponse();
        String randomName = UUID.randomUUID().toString().replaceAll("-", "");
        String unZipRealPath;
        if(random){
            unZipRealPath = unZipPath + randomName + "/";//+fileName + "/"
        }else
            unZipRealPath = unZipPath +  "/";//+fileName + "/"

        //如果保存解压缩文件的目录不存在，则进行创建，并且解压缩后的文件总是放在以fileName命名的文件夹下
        File unZipFile = new File(unZipRealPath);
        if (!unZipFile.exists()) {
            unZipFile.mkdirs();
        }
        //ZipInputStream用来读取压缩文件的输入流
        ZipInputStream zipInputStream = new ZipInputStream(multipartFile.getInputStream());
        //压缩文档中每一个项为一个zipEntry对象，可以通过getNextEntry方法获得，zipEntry可以是文件，也可以是路径，比如abc/test/路径下
        ZipEntry zipEntry;
        try {
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String zipEntryName = zipEntry.getName();
                //将目录中的1个或者多个\置换为/，因为在windows目录下，以\或者\\为文件目录分隔符，linux却是/
                String outPath = (unZipRealPath + zipEntryName).replaceAll("\\+", "/");
                //判断所要添加的文件所在路径或者
                // 所要添加的路径是否存在,不存在则创建文件路径
                File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
                if (!file.exists()) {
                    file.mkdirs();
                }
                //判断文件全路径是否为文件夹,如果是,在上面三行已经创建,不需要解压
                if (new File(outPath).isDirectory()) {
                    continue;
                }

                OutputStream outputStream = new FileOutputStream(outPath);
                byte[] bytes = new byte[4096];
                int len;
                //当read的返回值为-1，表示碰到当前项的结尾，而不是碰到zip文件的末尾
                while ((len = zipInputStream.read(bytes)) > 0) {
                    outputStream.write(bytes, 0, len);
                }
                outputStream.close();
                //必须调用closeEntry()方法来读入下一项
                zipInputStream.closeEntry();
            }
            zipInputStream.close();
            fileHandleResponse.setSuccess(1);
            fileHandleResponse.setMessage("解压完毕");
            fileHandleResponse.setUrl((randomName).replaceAll("\\+", "/"));
            System.out.println("******************解压完毕********************");

        } catch (Exception e) {
            fileHandleResponse.setSuccess(0);
            fileHandleResponse.setMessage("服务器异常");
            e.printStackTrace();
            return fileHandleResponse;
        }
        return fileHandleResponse;
    }

//获取路径中倒数第二个路径的文件夹名称
    public static String subStr(String path){
        if(path != null){
            String sub = path.substring(path.lastIndexOf("/",path.lastIndexOf("/")-1), path.lastIndexOf("/"));
            return sub;
        }
        else
            return null;
    }

//获取路径中倒数第二个路径的文件夹路径
    public static String subPath(String path){
    if(path != null){
        String sub = path.substring(0,path.lastIndexOf("/",path.lastIndexOf("/")));
        return sub;
    }
    else
        return null;
}
    public static Integer deletePath(String path) throws IOException {
        File file = new File(path);
        if(file.exists()){
            FileUtils.deleteDirectory(file);
            return 1;
        }else
            return 0;
    }

    public static String cutFilePreName(String name){
        return name.substring(0,name.lastIndexOf("."));
    }

    public static String cutFileSuffixName(String name){
        return name.substring(name.lastIndexOf("/") + 1);
    }

    public static Comparator<File> comparatorName =new Comparator <File>(){
        public int compare(File p1,File p2){
            String realName1 = cutFilePreName(p1.getName());
            String realName2 = cutFilePreName(p2.getName());
            if (Integer.parseInt(realName1)>Integer.parseInt(realName2))
                return 1;
            else if (Integer.parseInt(realName1)<Integer.parseInt(realName2))
                return -1;
            else
                return 0;
        }
    };

    public static String copyFileToNewPath(String fromPath, String toPath){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fromPath));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(toPath));
            int len;
            char[] chs = new char[1024];
            String line = null;

            while((len = bufferedReader.read(chs)) != -1) {
                bufferedWriter.write(chs,0,len);
            }
            bufferedReader.close();
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return toPath;
    }




}
