package cn.meiot.utils;

import cn.meiot.entity.vo.FileConfigVo;
import cn.meiot.enums.FileTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@Order(1)
public class FileTypeFactory {

    public static final Map<Integer,String> map = new HashMap<Integer, String>();
//    static {
//        System.out.println("工厂初始化了");
//        map.put(FileTypeEnum.IMG.value(), FileConfigVo.img);
//        map.put(FileTypeEnum.FIRMWARE_UPGRADE.value(),FileConfigVo.upgrade);
//        map.put(FileTypeEnum.APK.value(),FileConfigVo.apk);
//        map.put(FileTypeEnum.THUM.value(),FileConfigVo.thumbnail);
//        log.info("工厂初始化完成。"+map);
//
//
//
//
//    }




}
