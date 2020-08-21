package cn.meiot.utils.file;

import cn.meiot.enums.FileTypeEnum;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FileUploadTypeFactory  {

    public static final Map<Integer,FileManager> FileType = new HashMap<Integer, FileManager>();



    static {
        FileType.put(FileTypeEnum.IMG.value(),new ImgFileUtil());
        FileType.put(FileTypeEnum.FIRMWARE_UPGRADE.value(),new FirmwareUpgradeUtil());
        FileType.put(FileTypeEnum.APK.value(),new ApkUtil());


    }




}
