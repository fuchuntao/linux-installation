package cn.meiot.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 img:
 map: images/   #nginx映射名称 需要在最后加上斜杠
 path: F:\\work\\file\\ #文件根目录
 img: images\\
 servername: http://images.51epd.com/   #服务器域名，需要在最后加上斜杠
 thumbnail: thum/
 upgrade: upgrade\\ # 固件升级的保存地址
 */
@Component
public class FileUrlUtil {

    @Value("${img.servername}")
    private String servername;

    @Value("${img.map}")
    private String map;

    @Value("${img.path}")
    private String path;

    @Value("${img.img}")
    private String img;

    @Value("${img.thumbnail}")
    private String thumbnail;

    @Value("${img.upgrade}")
    private String upgrade;

    public String getImg(){
        return servername + map + img;
    }

}
