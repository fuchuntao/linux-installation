package cn.meiot.utils.file;

import cn.meiot.entity.vo.FileConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.enums.FileTypeEnum;
import cn.meiot.exception.MyServiceException;
import cn.meiot.utils.LocalFileManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Configuration
public class FirmwareUpgradeUtil extends AbstractFileTypeManagerUtil {


    @Override
    public Result upload(MultipartFile file)  {
        String fileName = getFilename(file.getOriginalFilename());
        //获取上传固件升级的绝对路径
        String path = FileConfigVo.getsavePath(fileName, FileTypeEnum.FIRMWARE_UPGRADE.value());
        FileManager fileManager = new LocalFileManager();
        Result upload = fileManager.upload(file, path);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("savePath", fileName);
        map.put("showPath", FileConfigVo.getFirmwareUpgradePath(fileName));
        upload.setData(map);
        return upload;
    }


    @Override
    public String getFilename(String fileName) {
        String suffixName = "";
        suffixName = fileName.substring(fileName.lastIndexOf("."));
        if(!suffixName.equals(".bin")){
            throw  new MyServiceException("请重新上传后缀为bin的文件","请重新上传后缀为bin的文件");
        }
        fileName= UUID.randomUUID() +suffixName;
        return fileName;
    }


}
