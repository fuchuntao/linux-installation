package com.jeecg.p3.weixin.util;


import com.google.gson.Gson;
import com.jeecg.p3.commonweixin.def.CommonWeixinProperties;
import net.sf.json.JSONObject;
import org.jeewx.api.core.exception.WexinReqException;
import org.jeewx.api.wxsendmsg.model.WxMediaResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 视频处理
 */
public class VideoUtil {

    private static Logger log = LoggerFactory.getLogger(VideoUtil.class);

    private static String uploadimg_url = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN&type=TYPE";

    // 上传媒体资源URL
    private static String upload_media_url = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";

    private static String upload_media_url2 = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=ACCESS_TOKEN&type=TYPE";

    private static String get_media_url =  "https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=ACCESS_TOKEN";

    public static String uploadImgReturnObj(String accesstoken, String filePath) {
        Map<String,Object> map =new HashMap<String, Object>();
        map.put("title","VIDEO_TITLE");
        map.put("introduction","INTRODUCTION");
        JSONObject obj = JSONObject.fromObject(map);
        uploadimg_url = uploadimg_url.replace("access_token", accesstoken);
        uploadimg_url = uploadimg_url.replace("type","video");
        uploadimg_url = uploadimg_url.replace("media","filename");
        JSONObject result = WeixinUtil.httpRequest(uploadimg_url, "POST", obj.toString());
        if(result.containsKey("url")){
            return result.getString("url");
        }
        return null;
    }


    public static String[] getUrls(String content){
        String[] urls = null;
        String str = "";
        Document document = Jsoup.parse(content);
        //ex：抓取图片例子 图片 标签<img src=" " alt=" " width=" " height=" ">
        Elements elements=document.getElementsByTag("embed");
        if(null != elements){
            for(Element element : elements){
                String url=element.attr("src"); //获取src属性的值
                System.out.println(url);
                if("".equals(str)){
                    str = url;
                }else{
                    str = str+","+url;
                }
            }
        }
        if(!"".equals(str)){
            urls = str.split(",");
        }
        return urls;
    }



    /**
     * 上传媒体资源
     *
     * @param filePath
     * @param fileName
     * @param type
     *            媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
     * @return
     * @throws Exception
     */
    public static WxMediaResponse uploadMediaFile(String accesstoken, String filePath, String fileName, String type) throws WexinReqException {
        WxMediaResponse mediaResource = null;
        if (accesstoken != null) {
            String requestUrl = upload_media_url2.replace("ACCESS_TOKEN", accesstoken).replace("TYPE", type);
            File file = new File(filePath +"/"+ fileName);
           // String contentType = VideoUtil.uploadPermanentMaterial(fileName.substring(fileName.lastIndexOf(".") + 1));
           JSONObject result = VideoUtil.uploadVideo(requestUrl,filePath +"/"+ fileName ,"这是标题", "这是描述");
//            System.out.println(result);
//            JSONObjectct result = VideoUtil.uploadVideo(requestUrl, filePath, "这是标题","这是描述");
            System.out.println("微信返回的结果：" + result.toString());
            if (result.containsKey("errcode")) {
                //logger.error("上传媒体资源失败！errcode=" + result.getString("errcode") + ",errmsg = " + result.getString("errmsg"));
            } else {
                // {"type":"TYPE","media_id":"MEDIA_ID","created_at":123456789}
                mediaResource = new WxMediaResponse();
                mediaResource.setMedia_id(result.getString("media_id"));
//                mediaResource.setType(result.getString("type"));
//                mediaResource.setCreated_at(new Date(result.getLong("created_at") * 1000));
            }
             return mediaResource;
        }
        return mediaResource;
    }


    public static String getVideoUrl(String accessToken, String media_id) {
        RestTemplate restTemplate = new RestTemplate();
        String url = get_media_url.replace("ACCESS_TOKEN", accessToken);
        url = url.replace("MEDIA_ID",media_id);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("media_id",media_id);
        ResponseEntity<String> result = restTemplate.postForEntity(url, new Gson().toJson(map), String.class);
        System.out.println("返回结果："+result);
        String body = result.getBody();
        JSONObject jsonObject = JSONObject.fromObject(body);
        System.out.println("获取视频列表结果："+jsonObject);
        return jsonObject.getString("down_url");
    }



    public static String downloadVideo(String url){
        HttpURLConnection con;
        FileOutputStream fs = null;
        InputStream is;
        BufferedInputStream bs = null;
        String savePath = CommonWeixinProperties.imgSave+CommonWeixinProperties.imgWx;
        savePath = savePath+new Date().getTime()+".mp4";
        File file = null;
            file = new File(url);
            if(file.exists()){
                return url;
            }
        file = new File(savePath);
        try {

            con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36");
            //输入流
            is = con.getInputStream();
            bs = new BufferedInputStream(is);
            //outStream
            fs = new FileOutputStream(file);
            byte [] bytes = new byte[1024];

            int line ;
            //write
            while((line = bs.read(bytes))!= -1){
                fs.write(bytes, 0, line);
                fs.flush();
            }
            return savePath;
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            //close
            if(fs!= null){
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bs!=null){
                try {
                    bs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        String token = "29_TYOiKaFQt1iRt_4RW02-ZRSlOn0D148gQUuVSMJ_6n5OqpfQ47dji96jF0L5VoBpt_cOLe4rK63d7fzss8ceq7yWp1ggOEbsJdTWGur2Mq96SxdevRntJFklz8UQTEhAHAZCN";

        RestTemplate restTemplate = new RestTemplate();
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token="+token;
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("type","video");
        map.put("offset",0);
        map.put("count",20);
        String json = new Gson().toJson(map);
        ResponseEntity<String> entity = restTemplate.postForEntity(requestUrl,json , String.class);
        String body = entity.getBody();

        System.out.println("结果："+JSONObject.fromObject(body));

        //String requestUrl = "https://api.weixin.qq.com/cgi-bin/material/add_material?access_token="+token+"&type=video";
        File file = new File("F:\\opt\\source\\file\\wx\\1578280873326.mp4");
        //JSONObject object = uploadMediaFile(requestUrl, file, "video/mp4");
//        JSONObject object = uploadVideo(requestUrl,"" ,"这是标题", "这是描述");
//        System.out.println(object);
        String result = getVideoUrl(token, "MkvcWU8O721l_bHORFXHpMbQBgOIMEL2ZXjOQNB0nVQ");
        System.out.println(result);


    }



    /**
     * 模拟form表单的形式 ，上传文件 以输出流的形式把文件写入到url中，然后用输入流来获取url的响应
     * @param url 请求地址 form表单url地址
     * @param filePath 文件在服务器保存路径
     * @param title 视频标题
     * @param introduction	视频描述
     * @return
     */
    public static JSONObject uploadVideo(String url, String filePath, String title, String introduction) {
        String result = null;

        HttpURLConnection downloadCon = null;
        InputStream inputStream = null;
        try {
            URL urlFile = new URL(filePath);
            downloadCon = (HttpURLConnection) urlFile.openConnection();
            inputStream = downloadCon.getInputStream();

            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            String boundary = "-----------------------------"+System.currentTimeMillis();
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);

            OutputStream output = conn.getOutputStream();
            output.write(("--" + boundary + "\r\n").getBytes());
            String regex = ".*/([^\\.]+)";
            output.write(String.format("Content-Disposition: form-data; name=\"media\"; filename=\"%s\"\r\n", filePath.replaceAll(regex, "$1")).getBytes());
            output.write("Content-Type: video/mp4 \r\n\r\n".getBytes());
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = inputStream.read(bufferOut)) != -1) {
                output.write(bufferOut, 0, bytes);
            }
            inputStream.close();

            output.write(("--" + boundary + "\r\n").getBytes());
            output.write("Content-Disposition: form-data; name=\"description\";\r\n\r\n".getBytes());
            output.write(String.format("{\"title\":\"%s\", \"introduction\":\"%s\"}",title,introduction).getBytes());
            output.write(("\r\n--" + boundary + "--\r\n\r\n").getBytes());
            output.flush();
            output.close();
            inputStream.close();
            InputStream resp = conn.getInputStream();
            StringBuffer sb = new StringBuffer();
            while((bytes= resp.read(bufferOut))>-1)
                sb.append(new String(bufferOut,0,bytes,"utf-8"));
            resp.close();
            result = sb.toString();
        } catch (IOException e) {
            log.info("上传文件： ->",e);
            return null;
        }
        return JSONObject.fromObject(result);
    }






















    public static JSONObject uploadMediaFile(String requestUrl, File file,
                                             String content_type) {

        JSONObject jsonObject = null;
        StringBuffer bufferStr = new StringBuffer();
        String end = "\r\n";
        String twoHyphens = "--"; // 用于拼接
        String boundary = "*****"; // 用于拼接 可自定义
        URL submit = null;
        DataOutputStream dos = null;
        // FileInputStream fis = null;
        BufferedInputStream bufin = null;
        BufferedReader bufferedReader = null;
        try {
            submit = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) submit.openConnection();
//			conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            conn.setConnectTimeout(10000); //连接超时为10秒
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type","multipart/form-data;boundary=" + boundary);

            // 获取输出流对象，准备上传文件
            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"" + file+ "\";filename=\"" + file.getName() + ";Content-Type=\""+ content_type+end);
            dos.write(String.format("{\"title\":\"%s\", \"introduction\":\"%s\"}","标题","描述").getBytes());
            dos.writeBytes(end);
            // 对文件进行传输
            bufin = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            while ((count = bufin.read(buffer)) != -1) {
                dos.write(buffer, 0, count);
            }

            bufin.close(); // 关闭文件流

            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            // 读取URL链接返回字符串
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);

            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                bufferStr.append(str);
            }

            jsonObject = JSONObject.fromObject(bufferStr.toString());
            // System.out.println("-------------读取URL链接返回字符串--------------" +
            // bufferStr.toString());

        } catch (Exception e) {
            //e.printStackTrace();
            System.err.println("异常错误:" + e.toString());
            System.err.println("连接地址是:" + requestUrl);
            // throw new Exception("微信服务器连接错误！" + e.toString());
        } finally {

            try {
                if (dos != null) {
                    dos.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }

            } catch (Exception e2) {

            }
        }
        // 获取到返回Json请自行根据返回码获取相应的结果
        return jsonObject;
    }

}
