package cn.meiot.utils;

import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.exception.MyServiceException;

import cn.meiot.utils.file.FileManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

/**
 * 文件管理
 */
@Slf4j
public abstract class AbstractFileManager implements FileManager {

    public  ImgConfigVo imgConfigVo;



    /**
     * 文件上传
     * @param file
     * @return
     */
    public Result upload(MultipartFile file,Integer type){
        throw  new MyServiceException("请重写上传方法","请重写上传方法");
    }

    /**
     * 文件下载
     * @param path  文件绝对路径
     * @return
     */
    public Result download(String path){
        throw  new MyServiceException("请重写下载方法","请重写下载方法");
    }


    public Result upload(String fileBase64, Integer type) {

        return null;
    }

    /**
     * 文件大小
     * @return
     */
   public  Long size(){
       throw  new MyServiceException("请重写大小方法","请重写大小方法");
    }


    public void thunImage(MultipartFile file,String filname) throws IOException {
    }

    public String getFilename(String fileName, Integer type){
        String suffixName = "";
        String folderName = "";
        log.info("原图上传文件名：{}",fileName);
        try {
            suffixName = fileName.substring(fileName.lastIndexOf("."));
            log.info("文件后缀名：{}",suffixName);
        }catch (Exception e){
            log.info("获取文件后缀名出错");
            throw  new MyServiceException("请上传正确的文件","请上传正确的文件");
        }
        if(type.equals(1)){
            if(!suffixName.equals(".bin")){
                throw  new MyServiceException("请重新上传后缀为bin的文件","请重新上传后缀为bin的文件");
            }
            folderName = imgConfigVo.getUpgrade();
        }else if(type.equals(2)){
            folderName = imgConfigVo.getImg();
        }
        log.info("文件夹名称：{}",folderName);
        log.info("文件名称：{}",fileName);
        fileName= UUID.randomUUID() +suffixName;
        log.info("新文件名：{}",fileName);
        return folderName+fileName;
    }


    public String getFolder(Integer type){
        throw  new MyServiceException("请重写","请重写");
    }


    public File getFile(MultipartFile file) throws IOException {
        File f = null;
        if(file.equals("")||file.getSize()<=0){
            file = null;
        }else{
            InputStream ins = file.getInputStream();
            f=new File(file.getOriginalFilename());
            inputStreamToFile(ins, f);
        }
        return f;
    }

    public static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
