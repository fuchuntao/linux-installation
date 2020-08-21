package cn.meiot.utils.file;


import cn.meiot.entity.vo.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件管理
 */
public interface FileManager {

    /**
     * 文件上传
     * @param file 文件
     * @param type  类型
     * @return
     */
    @Deprecated
    Result upload(MultipartFile file,Integer type);

    /**
     * 文件上传
     * @param file 文件
     * @param path  路径
     * @return
     */
    Result upload(MultipartFile file,String path);

    /**
     * 文件上传
     * @param file 文件
     * @return
     */
    Result upload(MultipartFile file);

    /**
     * 文件上传(base64)
     * @param fileBase64 文件
     * @param type  类型
     * @return
     */
    Result upload(String fileBase64,Integer type);


    /**
     * 文件下载
     * @param path  文件绝对路径
     * @return
     */
    Result download(String path);

    /**
     * 文件大小
     * @return
     */
    Long size();

    /**
     * 获取新的文件名
     * @param fileName 上传的文件名
     * @return
     */
    String getFilename(String fileName, Integer type);

    /**
     * 缩略图处理
     * @param file
     * @return
     */
    void thunImage(MultipartFile file,String filname) throws IOException;

    /**
     * 根据类型获取到当前文件属于哪个文件夹下
     * @param type
     * @return
     */
    String getFolder(Integer type);

    /**
     * 创建目录
     * @param path
     */
    void createDirectory(String path);
}
