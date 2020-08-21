package cn.meiot.utils;

import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.exception.MyServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

@Slf4j
@Component

public class LocalFileManager extends AbstractFileManager{

    private static final int NEWWIDTH = 200;

    public LocalFileManager() {
    }

    public LocalFileManager(ImgConfigVo imgConfigVo) {
        super.imgConfigVo = imgConfigVo;
    }

    /**
     * 文件上传
     * @param file
     * @return
     */
    public Result upload(MultipartFile file,Integer type){
        System.out.println("原图上传");
        InputStream is = null;
        OutputStream os = null;
        String fileName = getFilename(file.getOriginalFilename(),type);

        try {
            is = file.getInputStream();
            String des = imgConfigVo.getPath()+fileName;
            log.info("文件原路径：{}",des);
            os = new FileOutputStream(des) ;

            byte[] buffer = new byte[1024];
            int len = 0;

            while((len = is.read(buffer))>0){
                os.write(buffer, 0, len);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("原文件上传失败：{}",e);
            throw  new MyServiceException("文件上传失败","文件上传失败");
        }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Result result = Result.getDefaultTrue();
        result.setData(fileName.split("/")[1]);
        return result;
    }

    @Override
    public Result upload(MultipartFile file, String path) {
        System.out.println("原图上传");
        InputStream is = null;
        OutputStream os = null;
        try {
            createDirectory(path);
            is = file.getInputStream();
            os = new FileOutputStream(path);
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = is.read(buffer))>0){
                os.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("原文件上传失败：{}",e);
            throw  new MyServiceException("文件上传失败","文件上传失败");
        }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Result result = Result.getDefaultTrue();
        return result;
    }

    @Override
    public Result upload(MultipartFile file) {
        return null;
    }

    @Override
    public Result upload(String fileBase64, Integer type) {
        File file = null;
        //创建文件目录
        File dir = new File(imgConfigVo.getPath());
        if(!dir.exists()&&!dir.isDirectory()) {
            dir.mkdirs();
        }
        String fileName = getFilename("baseImages.jpg",type);
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        byte[] bytes = Base64.getDecoder().decode(fileBase64);
        file = new File(imgConfigVo.getPath()+"\\"+fileName);
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if(bos!=null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(fos!=null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        Result result = Result.getDefaultTrue();
        result.setData(fileName.split("/")[1]);
        return result;
    }

    /**
     * 文件下载
     * @param path  文件绝对路径
     * @return
     */
    public Result download(String path){
        throw  new MyServiceException("请重写下载方法","请重写下载方法");
    }

    /**
     * 文件大小
     * @return
     */
    public  Long size(){
        throw  new MyServiceException("请重写大小方法","请重写大小方法");
    }

    /**
     * 按指定高度 等比例缩放图片
     * @param srcFile
     * @param path
     * @return
     */
    public void thunImage(MultipartFile srcFile, String path) throws IOException {
        File file = getFile(srcFile);
        createDirectory(path);
        if(!file.canRead())
            throw new MyServiceException("文件上传失败","文件上传失败");
        BufferedImage bufferedImage = ImageIO.read(file);
        if (null == bufferedImage)
            throw new MyServiceException("文件上传失败","文件上传失败");

        int originalWidth = bufferedImage.getWidth();
        int originalHeight = bufferedImage.getHeight();
        double scale = (double)originalWidth / (double)NEWWIDTH;    // 缩放的比例

        int newHeight =  (int)(originalHeight / scale);
        //fileName = imgConfigVo.getPath()+imgConfigVo.getImg()+imgConfigVo.getThumbnail()+fileName;
        log.info("缩略图的路径：{}",path);
        zoomImageUtils(file,path , bufferedImage, NEWWIDTH, newHeight);
    }



    private  void zoomImageUtils(File imageFile, String newPath, BufferedImage bufferedImage, int width, int height)
            throws IOException{

        String suffix = StringUtils.substringAfterLast(imageFile.getName(), ".");

        // 处理 png 背景变黑的问题
        if(suffix != null && (suffix.trim().toLowerCase().endsWith("png") || suffix.trim().toLowerCase().endsWith("gif"))){
            BufferedImage to= new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = to.createGraphics();
            to = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
            g2d.dispose();

            g2d = to.createGraphics();
            Image from = bufferedImage.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
            g2d.drawImage(from, 0, 0, null);
            g2d.dispose();

            ImageIO.write(to, suffix, new File(newPath));
        }else{
            // 高质量压缩，其实对清晰度而言没有太多的帮助
//            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//            tag.getGraphics().drawImage(bufferedImage, 0, 0, width, height, null);
//
//            FileOutputStream out = new FileOutputStream(newPath);    // 将图片写入 newPath
//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
//            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
//            jep.setQuality(1f, true);    //压缩质量, 1 是最高值
//            encoder.encode(tag, jep);
//            out.close();

            BufferedImage newImage = new BufferedImage(width, height, bufferedImage.getType());
            Graphics g = newImage.getGraphics();
            g.drawImage(bufferedImage, 0, 0, width, height, null);
            g.dispose();
            log.info("最终的保存路径：{}",newPath);
            ImageIO.write(newImage, suffix, new File(newPath));
        }
    }


    public String getFolder(Integer type){
        String folder = "";
        if(type.equals(1)){

            folder = imgConfigVo.getUpgrade();
        }else if(type.equals(2)){
            folder = imgConfigVo.getImg();
        }
        return folder;

    }

    @Override
    public void createDirectory(String path) {
        String directory = path.substring(0,path.lastIndexOf("/"));
        File f = new File(directory);
        if(!f.exists() && !f.isDirectory() ){
            f.mkdirs();
        }
    }


}
