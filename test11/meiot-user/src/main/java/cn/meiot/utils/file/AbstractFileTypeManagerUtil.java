package cn.meiot.utils.file;

import cn.meiot.entity.vo.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public class AbstractFileTypeManagerUtil implements FileManager {
    @Override
    public Result upload(MultipartFile file, Integer type) {
        return null;
    }

    @Override
    public Result upload(MultipartFile file, String path) {
        return null;
    }

    @Override
    public Result upload(MultipartFile file) {
        return null;
    }

    @Override
    public Result upload(String fileBase64, Integer type) {
        return null;
    }

    @Override
    public Result download(String path) {
        return null;
    }

    @Override
    public Long size() {
        return null;
    }


    @Override
    @Deprecated
    public String getFilename(String fileName, Integer type) {
        return null;
    }

    public String getFilename(String fileName) {
        String suffixName = "";
        suffixName = fileName.substring(fileName.lastIndexOf("."));
        fileName= UUID.randomUUID() +suffixName;
        return fileName;
    }

    @Override
    public void thunImage(MultipartFile file, String filname) throws IOException {

    }

    @Override
    public String getFolder(Integer type) {
        return null;
    }

    @Override
    public void createDirectory(String path) {

    }
}
