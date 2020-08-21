package cn.meiot.entity.vo;

import cn.meiot.enums.FileTypeEnum;
import cn.meiot.utils.FileTypeFactory;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Data
@Component
@Deprecated
public class ImgConfigVo implements Serializable {


    private static final Map<Integer,String> mapType = new HashMap<Integer, String>();


    /**
     *代理名
     */
    @Value("${img.map}")
    private  String map;


    /**
     * 文件保存的根目录
     */
    @Value("${img.path}")
    private String path;

    /**
     * 服务器域名
     */
    @Value("${img.servername}")
    private String servername;

    /**
     * 图片地址
     */
    @Value("${img.img}")
    private String img;

    /**
     * 固件升级地址
     */
    @Value("${img.upgrade}")
    private String upgrade;

    /**
     * apk
     *
     */
    @Value("${img.apk}")
    private String apk;

    /**
     * 缩略图前缀
     */
    @Value("${img.thumbnail}")
    private  String thumbnail;


    /**
     * 获取图片路径
     * @param relpath
     * @return
     */
    public String  getMPath(String relpath){

        return getPath(relpath, FileTypeEnum.IMG.value());
    }

    /**
     * 获取文件路径
     * @param relPath 相对路径
     * @param type 文件类型
     * @return
     */
    public String getPath(String relPath,Integer type){

        return servername+map+FileTypeFactory.map.get(type)+relPath;
//        return null;
    }

    /**
     * 获取固件升级路径
     * @param relpath
     * @return
     */
    public String getFirmwareUpgradePath(String relpath){

        return getPath(relpath, FileTypeEnum.FIRMWARE_UPGRADE.value());
    }


    /**
     * 获取apk路径
     * @param relpath
     * @return
     */
    public String getApkPath(String relpath){

        return getPath(relpath, FileTypeEnum.APK.value());
    }




}
