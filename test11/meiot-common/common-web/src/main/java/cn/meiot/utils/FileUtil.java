package cn.meiot.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件操作类
 */
@Slf4j
public class FileUtil {
    private static final String PREFIX = "http://";

    private static final String APP_PREFIX = "https://";

    private static final String PC_PREFIX = "//";

    /**
     * 获取完整路径
     * @param request
     * @param path   拼好的路径，app和pc的路径不一样
     * @return
     */
    public static String  getFullPath(HttpServletRequest request,String path){

        String  agent = request.getHeader("User-Agent");
        String  device = UserAgentUtils.getDeviceName(agent);
        log.info("当前设备：{}",device);
        if(path.equals(PREFIX)){
            return path;
        }
        if("pc".equals(device)){
            return PC_PREFIX+path;
        }
        return APP_PREFIX+path;

    }
}
