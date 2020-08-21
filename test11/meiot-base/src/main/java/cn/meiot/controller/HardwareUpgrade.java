package cn.meiot.controller;

import cn.meiot.entity.Firmware;
import cn.meiot.entity.vo.*;
import cn.meiot.enums.FileTypeEnum;
import cn.meiot.service.IFirmwareService;
import cn.meiot.utils.CRC;
import cn.meiot.utils.CommonUtil;
import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utils.RedisComm;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Package cn.meiot.controller
 * @Description:
 * @author: 武有
 * @date: 2019/11/13 12:23
 * @Copyright: www.spacecg.cn
 */
@RestController
@SuppressWarnings("all")
@Slf4j
public class HardwareUpgrade {

    @Autowired
    private IFirmwareService firmwareService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;

//    public static FileOutputStream filetream;

//    static {
//        try {
//            filetream = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\testbin\\123.bin");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 字节数据
     */


    @RequestMapping("nofilter/upload")
    public void upload(@RequestParam("skip") Long skip,
                       @RequestParam("size") Integer size, HttpServletResponse response,
                       @RequestParam(value = "region", required = true) Integer region,
                       @RequestParam(value = "version", required = true) String version,
                       @RequestParam(value = "serialNumber", required = true) String serialNumber) throws IOException {
        InputStream fileInputStream = null;
        HttpURLConnection urlConnection = null;
        ServletOutputStream out = response.getOutputStream();
        byte[] bytes;
        /**
         * 读到的字节数
         */
        Integer readBytes;

        Long fileLengh;
        try {
            bytes = (byte[]) redisTemplate.opsForValue().get(version + "_" + skip + "_" + size + "_" + region);
            readBytes = (Integer) redisTemplate.opsForValue().get(RedisComm.READ_BYTE + version + "_" + skip + "_" + size + "_" + region);
            fileLengh = (Long) redisTemplate.opsForValue().get(RedisComm.FILE_LENGH + version + "_" + skip + "_" + size + "_" + region);
            if (null == bytes || null == readBytes || null == fileLengh) {
                synchronized (HardwareUpgrade.class) {
                    bytes = (byte[]) redisTemplate.opsForValue().get(version + "_" + skip + "_" + size + "_" + region);
                    readBytes = (Integer) redisTemplate.opsForValue().get(RedisComm.READ_BYTE + version + "_" + skip + "_" + size + "_" + region);
                    fileLengh = (Long) redisTemplate.opsForValue().get(RedisComm.FILE_LENGH + version + "_" + skip + "_" + size + "_" + region);
                    if (null == bytes || null == readBytes || null == fileLengh) {
                        log.info("======>:读磁盘");
                        FirmwareFileVo firmwareFileVo = firmwareService.getFirmwareUrl(version, region);
//                        ImgConfigVo imgConfigVo = commonUtil.getImgConfig();
//                        String originaPath = imgConfigVo.getPath() + imgConfigVo.getUpgrade() + firmwareFileVo.getFiles().getAddress();
//                        String originaPath = "C:\\Users\\Administrator\\Desktop\\name\\moudle_commflash1.bin";
                        String originaPath = FileConfigVo.getsavePath(firmwareFileVo.getFiles().getAddress(),FileTypeEnum.FIRMWARE_UPGRADE.value());
                        log.info("====>文件地址：{}", originaPath);
                        File file = new File(originaPath);
                        fileInputStream = new FileInputStream(file);
                        fileLengh = file.length();
                        fileInputStream.skip(skip);
                        byte fileBytes[] = new byte[size];
                        int read = fileInputStream.read(fileBytes);
                        String crc = JSON.toJSONString(new UploadVo(read, CRC.getCRC16(fileBytes, read), fileLengh, size, skip));
                        log.info("===硬盘===>>CRC校验码:{}", crc);
                        if (read != -1) {
                            redisTemplate.opsForValue().set(RedisComm.READ_BYTE + version + "_" + skip + "_" + size + "_" + region, read, 60, TimeUnit.MINUTES);
                            redisTemplate.opsForValue().set(version + "_" + skip + "_" + size + "_" + region, fileBytes, 60, TimeUnit.MINUTES);
                            redisTemplate.opsForValue().set(RedisComm.FILE_LENGH + version + "_" + skip + "_" + size + "_" + region, fileLengh, 60, TimeUnit.MINUTES);
                        }
                        bytes = (byte[]) redisTemplate.opsForValue().get(version + "_" + skip + "_" + size + "_" + region);
                        readBytes = (Integer) redisTemplate.opsForValue().get(RedisComm.READ_BYTE + version + "_" + skip + "_" + size + "_" + region);
                        fileLengh = (Long) redisTemplate.opsForValue().get(RedisComm.FILE_LENGH + version + "_" + skip + "_" + size + "_" + region);
                    }
                }

            }
            UploadVo uploadVo = new UploadVo(readBytes, CRC.getCRC16(bytes, readBytes), fileLengh, size, skip);
            String crc = JSON.toJSONString(uploadVo);
            log.info("======>>【CRC校验码】:{}===>:【{}】 【区域】：{}", crc, serialNumber, region);
            out.write("str".getBytes());
            out.write(crc.getBytes());
            out.write(bytes);
//            filetream.write(bytes);
            out.write("end".getBytes());
            UpVo upVo = new UpVo(serialNumber, uploadVo);
            rabbitTemplate.convertAndSend(QueueConstantUtil.PROGRESS_BAR, upVo);
            log.info("消息已经发送：队列：{} 参数：{}",QueueConstantUtil.PROGRESS_BAR, upVo);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                try {
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != fileInputStream) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @GetMapping("getCurrentVersion")
    public String getCurrentVersion() {
        Firmware firmware = firmwareService.getNewFirmware();
        return firmware == null ? "" : firmware.getVersion();
    }


    public static void main(String[] args) throws IOException {
//        RestTemplate restTemplate = new RestTemplate();
//        Integer skip = 0;
//        Integer size = 64;
//        String version = "1";
//        Integer region = 0;
//        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\test.png");
//        for (int i = 0; i < 54; i++) {
//            String uri = "http://127.0.0.1:9910/nofilter/upload?skip=" + skip + "&size=" + size + "&version=" + version + "&region=" + region;
//            skip = skip + size;
//            System.out.println(uri);
//            restTemplate.getForObject(uri, byte[].class);
//            ResponseEntity<byte[]> res = restTemplate.getForEntity(
//                    uri,
//                    byte[].class);
//            byte[] body = res.getBody();
//            System.out.println(res.getBody());
//            fileOutputStream.write(res.getBody());
//            System.out.println("==========>i:" + i);
//        }

        System.out.println("v".compareTo("V"));

    }


}
