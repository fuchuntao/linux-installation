//package cn.meiot.utils;
//
//import cn.meiot.entity.vo.Result;
//import cn.meiot.exception.MyServiceException;
//import lombok.extern.slf4j.Slf4j;
//import net.coobird.thumbnailator.Thumbnails;
//import net.coobird.thumbnailator.geometry.Positions;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.*;
//import java.util.UUID;
//
//@Slf4j
//@Component
//public class UploadImageUtil {
//
//    @Value("${img.path}")
//    private String path;
//
//    @Value("${img.map}")
//    private String map;
//
//    @Value("${img.img}")
//    private String img;
//
//
//    @Value("${img.servername}")
//    private String serverName;
//
//    @Value("${img.thumbnail}")
//    private  String thum ;
//
//    public static final int WIDTH= 200;
//    public static final int HEIGHT= 200;
//
//    private static final int NEWWIDTH = 200;
//
//
//    /**
//     * 上传文件
//     * @param file	上传的文件
//     * @return
//     */
//    public  String uploadImage(MultipartFile  file,String filePath){
//        System.out.println("原图上传");
//        InputStream is = null;
//        OutputStream os = null;
//        String fileName = getFileName(file.getOriginalFilename());
//        try {
//            is = file.getInputStream();
//            String des = filePath +fileName;
//            log.info("文件原路径：{}",des);
//            os = new FileOutputStream(des) ;
//
//            byte[] buffer = new byte[1024];
//            int len = 0;
//
//            while((len = is.read(buffer))>0){
//                os.write(buffer, 0, len);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info("原文件上传失败：{}",e.getMessage());
//            return null ;
//        }finally {
//            if(is!=null){
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if(os!=null){
//                try {
//                    os.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return fileName;
//    }
//
//
////    public  String thumbnailUploadImage(MultipartFile file,  String fileName) throws IOException {
////
////        Image image= ImageIO.read(file.getInputStream());
////        int width=image.getWidth(null); //获取原图宽度
////        log.info("图片原宽度：{}",width);
////        int height=image.getHeight(null);//获取原图高度
////        log.info("图片原高度：{}",height);
////        int wrate=width/WIDTH;    //宽度缩略图
////        log.info("宽度除以100：：{}",wrate);
////        int hrate=height/HEIGHT;//高度缩略图
////        log.info("高度除以100：：{}",hrate);
////        int rate=0;
////        if (wrate>hrate) {//宽度缩略图比例大于高度缩略图，使用宽度缩略图
////            rate=wrate;
////        } else {
////            rate=hrate;
////        }
////        log.info("比例：{}",rate);
////        //计算缩略图最终的宽度和高度
////        int newWidth=width/rate;
////        log.info("新宽度：{}",newWidth);
////        int newHeight=height/rate;
////        log.info("新高度：{}",newHeight);
////
////        String des = imgPath + "/thum/"+fileName;//缩略图实际存储路径
////        log.info("缩略图路径：{}",des);
////        try {
////
////            //Thumbnails.of(file.getInputStream()).sourceRegion(Positions.CENTER, newWidth, newHeight).size(newWidth, newHeight).keepAspectRatio(false).toFile(des);
////
////            Thumbnails.of(file.getInputStream()).size(newWidth, newHeight).toFile(des);
////            log.info("缩略图上传成功");
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        return  "/thum/"+fileName;
////    }
//
//
//    private   String getFileName(String fileName){
//        log.info("文件名称：{}",fileName);
//        String suffixName = "";
//        try {
//            suffixName = fileName.substring(fileName.lastIndexOf("."));
//            log.info("文件后缀名：{}",suffixName);
//        }catch (Exception e){
//            log.info("获取文件后缀名出错");
//            throw  new MyServiceException("请上传正确的文件","请上传正确的文件");
//        }
//        fileName= UUID.randomUUID() +suffixName;
//        log.info("新文件名：{}",fileName);
//        return fileName;
//    }
//
//
//    /**
//     * 按指定高度 等比例缩放图片
//     *
//     * @param imageFile
//     * @throws IOException
//     */
//    public  void zoomImageScale(File imageFile,String fileName) throws IOException {
//        if(!imageFile.canRead())
//            throw new MyServiceException("文件上传失败","文件上传失败");
//        BufferedImage bufferedImage = ImageIO.read(imageFile);
//        if (null == bufferedImage)
//            throw new MyServiceException("文件上传失败","文件上传失败");
//
//        int originalWidth = bufferedImage.getWidth();
//        int originalHeight = bufferedImage.getHeight();
//        double scale = (double)originalWidth / (double)NEWWIDTH;    // 缩放的比例
//
//        int newHeight =  (int)(originalHeight / scale);
//        fileName = path+img+thum+fileName;
//        log.info("缩略图的路径：{}",fileName);
//        zoomImageUtils(imageFile,fileName , bufferedImage, NEWWIDTH, newHeight);
//    }
//
//    private  void zoomImageUtils(File imageFile, String newPath, BufferedImage bufferedImage, int width, int height)
//            throws IOException{
//
//        String suffix = StringUtils.substringAfterLast(imageFile.getName(), ".");
//
//        // 处理 png 背景变黑的问题
//        if(suffix != null && (suffix.trim().toLowerCase().endsWith("png") || suffix.trim().toLowerCase().endsWith("gif"))){
//            BufferedImage to= new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//            Graphics2D g2d = to.createGraphics();
//            to = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
//            g2d.dispose();
//
//            g2d = to.createGraphics();
//            Image from = bufferedImage.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
//            g2d.drawImage(from, 0, 0, null);
//            g2d.dispose();
//
//            ImageIO.write(to, suffix, new File(newPath));
//        }else{
//            // 高质量压缩，其实对清晰度而言没有太多的帮助
////            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
////            tag.getGraphics().drawImage(bufferedImage, 0, 0, width, height, null);
////
////            FileOutputStream out = new FileOutputStream(newPath);    // 将图片写入 newPath
////            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
////            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
////            jep.setQuality(1f, true);    //压缩质量, 1 是最高值
////            encoder.encode(tag, jep);
////            out.close();
//
//            BufferedImage newImage = new BufferedImage(width, height, bufferedImage.getType());
//            Graphics g = newImage.getGraphics();
//            g.drawImage(bufferedImage, 0, 0, width, height, null);
//            g.dispose();
//            log.info("最终的保存路径：{}",newPath);
//            ImageIO.write(newImage, suffix, new File(newPath));
//        }
//    }
//
//
//    public static void inputStreamToFile(InputStream ins, File file) {
//        try {
//            OutputStream os = new FileOutputStream(file);
//            int bytesRead = 0;
//            byte[] buffer = new byte[8192];
//            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
//                os.write(buffer, 0, bytesRead);
//            }
//            os.close();
//            ins.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 删除附件
//     * @param fileName
//     * @return
//     */
//    public Result delFile(String fileName,String path) {
//        String delPath = path+fileName;
//        log.info("需要删除的文件：{}",delPath);
//        File file = new File(delPath);
//        if(!file.exists() ||  !file.isFile()){
//            log.info("文件不存在或者不是文件名");
//           return new Result().Faild("删除文件失败");
//        }
//        boolean flag = file.delete();
//        if(flag){
//            return Result.getDefaultTrue();
//        }
//        return  new Result().Faild("删除文件失败");
//    }
//}
