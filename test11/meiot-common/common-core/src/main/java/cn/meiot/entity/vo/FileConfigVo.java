package cn.meiot.entity.vo;

import cn.meiot.enums.FileTypeEnum;
import cn.meiot.utils.FileTypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Configuration
@Order(0)
public class FileConfigVo {

    /**
     * 代理名
     */

    public static String map;

    /**
     * 文件保存的根目录
     */
    public static String path;

    /**
     * 服务器域名
     */
    public static String servername;

    /**
     * 图片地址
     */
    public static String img;

    /**
     * 固件升级地址
     */
    public static String upgrade;

    /**
     * apk
     */
    public static String apk;

    /**
     * 缩略图前缀
     */
    public static String thumbnail;


    public String getMap() {
        return map;
    }

    @Value("${img.map}")
    public void setMap(String map) {
        this.map = map;
    }

    public String getPath() {
        return path;
    }

    @Value("${img.path}")
    public void setPath(String path) {
        this.path = path;
    }

    public String getServername() {
        return servername;
    }

    @Value("${img.servername}")
    public void setServername(String servername) {
        this.servername = servername;
    }

    public String getImg() {
        return img;
    }


    @Autowired
    public void setImg(@Value("${img.img}")String img) {
        FileTypeFactory.map.put(FileTypeEnum.IMG.value(),img);
        this.img = img;
    }

    public String getUpgrade() {
        return upgrade;
    }

    @Value("${img.upgrade}")
    public void setUpgrade(String upgrade) {
        FileTypeFactory.map.put(FileTypeEnum.FIRMWARE_UPGRADE.value(),upgrade);
        this.upgrade = upgrade;
    }

    public String getApk() {
        return apk;
    }

    @Value("${img.apk}")
    public void setApk(String apk) {
        FileTypeFactory.map.put(FileTypeEnum.APK.value(),apk);
        this.apk = apk;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    @Value("${img.thumbnail}")
    public void setThumbnail(String thumbnail) {
        FileTypeFactory.map.put(FileTypeEnum.THUM.value(),thumbnail);
        this.thumbnail = thumbnail;
    }

    /**
     * 获取图片路径
     *
     * @param relpath
     * @return
     */
    public static String getMPath(String relpath) {

        return getLocation(relpath, FileTypeEnum.IMG.value());
    }

    /**
     * 获取图片路径
     *
     * @param relpath
     * @return
     */
    public static String getThuPath(String relpath) {

        return getLocation(relpath, FileTypeEnum.THUM.value());
    }

    /**
     * 获取文件路径
     *
     * @param relPath 相对路径
     * @param type    文件类型
     * @return
     */
    public static String getLocation(String relPath, Integer type) {
        if(type == FileTypeEnum.THUM.value()){
            return servername + map + img+FileTypeFactory.map.get(type) + relPath;
        }

        return servername + map + FileTypeFactory.map.get(type) + relPath;
    }

    /**
     * 获取固件升级路径
     *
     * @param relpath
     * @return
     */
    public static String getFirmwareUpgradePath(String relpath) {

        return getLocation(relpath, FileTypeEnum.FIRMWARE_UPGRADE.value());
    }


    /**
     * 获取apk路径
     *
     * @param relpath
     * @return
     */
    public static String getApkPath(String relpath) {

        return getLocation(relpath, FileTypeEnum.APK.value());
    }


    /**
     * 获取apk路径
     *
     * @param relPath
     * @return
     */
    public static String getsavePath(String relPath, Integer type) {
        System.out.println("hahahaha");
        if(type == FileTypeEnum.THUM.value()){
            return path +img+ FileTypeFactory.map.get(type) + relPath;
        }
        return path + FileTypeFactory.map.get(type) + relPath;
    }


}
