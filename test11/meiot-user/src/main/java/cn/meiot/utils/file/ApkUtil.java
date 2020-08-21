package cn.meiot.utils.file;

import cn.meiot.entity.vo.FileConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.enums.FileTypeEnum;
import cn.meiot.exception.MyServiceException;
import cn.meiot.utils.LocalFileManager;
import cn.meiot.utils.ReadApkUtils;
import cn.meiot.utils.RedisConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class ApkUtil extends AbstractFileTypeManagerUtil {



    @Override
    public Result upload(MultipartFile file)  {
        Map<String,Object> map = new HashMap<String, Object>();
        String fileName = getFilename(file.getOriginalFilename());
        //获取上传固件升级的绝对路径
        String path = FileConfigVo.getsavePath(fileName, FileTypeEnum.APK.value());
        FileManager fileManager = new LocalFileManager();
        Result upload = fileManager.upload(file, path);
        File apkFile = new File(path);
        Map<String, String> apkV = ReadApkUtils.apkFile(apkFile);
        //版本号
        String apkVersion = apkV.get(ReadApkUtils.VERSION_NAME);

        //redisTemplate.opsForHash().put(RedisConstantUtil.FILE_SIZE,fileName,file.getSize());
        map.put("savePath", fileName);
        map.put("showPath", FileConfigVo.getApkPath(fileName));
        map.put("apkVersion",apkVersion);
        upload.setData(map);
        return upload;
    }


    @Override
    public String getFilename(String fileName) {
        String suffixName = "";
        suffixName = fileName.substring(fileName.lastIndexOf("."));
        if(!suffixName.equals(".apk")){
            throw  new MyServiceException("请重新上传后缀为apk的文件","请重新上传后缀为apk的文件");
        }
        fileName= UUID.randomUUID() +suffixName;
        return fileName;
    }


}
