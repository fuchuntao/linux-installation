package cn.meiot.utils.file;

import cn.meiot.entity.vo.FileConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.enums.FileTypeEnum;
import cn.meiot.exception.MyServiceException;
import cn.meiot.utils.LocalFileManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 图片上传操作
 */
@Component
@Slf4j
public class ImgFileUtil extends AbstractFileTypeManagerUtil {

    @Override
    public Result upload(MultipartFile file)  {
        String fileName = getFilename(file.getOriginalFilename());
        //获取上传图片的绝对路径
        String path = FileConfigVo.getsavePath(fileName, FileTypeEnum.IMG.value());
        log.info("原图保存的路径：{}",path);
        FileManager fileManager = new LocalFileManager();

        Result upload = fileManager.upload(file, path);
        //获取压缩图的绝对路径
        String thumPath = FileConfigVo.getsavePath(fileName,FileTypeEnum.THUM.value());
        log.info("缩略图保存的路径：{}",thumPath);
        try {
             fileManager.thunImage(file, thumPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("savePath", fileName);
        map.put("showPath", FileConfigVo.getMPath(fileName));
        upload.setData(map);
        return upload;
    }


    @Override
    public String getFilename(String fileName) {
        String suffixName = "";
        suffixName = fileName.substring(fileName.lastIndexOf("."));
        if(!".jpg".equals(suffixName) && !".png".equals(suffixName)){
            throw  new MyServiceException("仅支持后缀为jpg/png格式的图片","仅支持后缀为jpg/png格式的图片");
        }
        fileName= UUID.randomUUID() +suffixName;
        return fileName;
    }
}
