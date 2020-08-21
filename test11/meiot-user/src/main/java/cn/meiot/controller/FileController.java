package cn.meiot.controller;

import cn.meiot.aop.Log;
import cn.meiot.entity.vo.FileConfigVo;
import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.enums.FileTypeEnum;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.RedisConstantUtil;
import cn.meiot.utils.file.FileManager;
import cn.meiot.utils.LocalFileManager;
import cn.meiot.utils.file.FileUploadTypeFactory;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件管理
 */
@RestController
@Slf4j
@RequestMapping("/file")
public class FileController {

    @Value("${img.path}")
    private String path;

    @Value("${img.map}")
    private String map;

    @Value("${img.img}")
    private String img;

    @Value("${img.upgrade}")
    private String upgrade;

    @Value("${img.servername}")
    private String serverName;
//    @Autowired
//    private UploadImageUtil uploadImageUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    public ImgConfigVo imgConfigVo;


    /**
     * 文件上传
     *
     * @return
     */
    @RequestMapping(value = "uploadFIle", method = RequestMethod.POST)
    @Log(operateContent = "文件上传",operateModule = "用户中心")
    public Result uploadFIle(@RequestParam("file") MultipartFile srcFile) throws IOException {
        System.out.println("文件上传");
        FileManager fileManager = new LocalFileManager(imgConfigVo);
        Result result = fileManager.upload(srcFile,2);
        if(!result.isResult()){
            return  result;
        }
        String fileName = result.getData().toString();
        log.info("原图上传成功，新文件名：{}",fileName);
        fileManager.thunImage(srcFile, FileConfigVo.getsavePath(fileName, FileTypeEnum.THUM.value()));
        Map<String, String> data = new HashMap<String, String>();
        //data.put("showPath", serverName+map+fileName);
        data.put("savePath", fileName);
        data.put("showPath", serverName+map+img+fileName);
        result.setData(data);
        return result;


    }

    /**
     * 获取保存的地址
     * @param type
     * @return
     */
    private String getsavePath(Integer type) {
        String savePath = path;
        if(type.equals(1)){
            return savePath+upgrade;
        }
        return null ;
    }

    /**
     * 升级包
     * @param srcFile
     * @return
     */
    @Log(operateContent = "文件上传",operateModule = "用户中心")
    @RequestMapping(value = "uploadMaterial/{type}", method = RequestMethod.POST)
    public Result uploadMaterial(@RequestParam("file") MultipartFile srcFile,@PathVariable(value = "type") Integer type)  {
        if (srcFile.isEmpty()) {
            Result result = Result.getDefaultFalse();
            result.setMsg(ErrorCodeUtil.SELECT_UPLOAD_FILE_PLEASE);
            return result;
        }
        FileManager manager = FileUploadTypeFactory.FileType.get(type);
        Result result = manager.upload(srcFile);
        if(FileTypeEnum.FIRMWARE_UPGRADE.value().equals(type) || FileTypeEnum.APK.value().equals(type) ){
            String fileName = (String) ((Map)result.getData()).get("savePath");
            redisTemplate.opsForHash().put(RedisConstantUtil.FILE_SIZE,fileName,srcFile.getSize());
        }
        return result;

    }

    @GetMapping(value = "delFile/{type}")
    public Result delFile(@RequestParam(value = "fileName",required = true) String fileName,@PathVariable("type") Integer type){

        String path = getsavePath(type);
        //return uploadImageUtil.delFile(fileName,path);
        return null;
    }

    @GetMapping(value = "/nofilter/test")
    public  String test(){
        try {
            String path = System.getProperty("user.dir");
            log.info("路径：{}",path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 文件上传  base64
     * @param request
     * @return
     */
    @PostMapping(value = "/uploadBase64")
    public Result uploadBase64(HttpServletRequest request){
        String file= null;
        try {
            BufferedReader streamReader = new BufferedReader( new InputStreamReader(request.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);

            JSONObject jsonObject = JSONObject.parseObject(responseStrBuilder.toString());
            file= (String) jsonObject.get("file");
            FileManager fileManager = new LocalFileManager(imgConfigVo);
            Result result = fileManager.upload(file,2);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
