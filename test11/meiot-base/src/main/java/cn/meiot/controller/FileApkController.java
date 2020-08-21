package cn.meiot.controller;


import cn.meiot.entity.FileApk;
import cn.meiot.entity.vo.ApkInfoVo;
import cn.meiot.entity.vo.FileConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.UpdateApkInfoVo;
import cn.meiot.exception.MyServiceException;
import cn.meiot.service.IFileApkService;
import cn.meiot.utils.MultipartFileToFile;
import cn.meiot.utils.ReadApkUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wuyou
 * @since 2020-03-16
 */
@RestController
@RequestMapping("/file-apk")
@Slf4j
public class FileApkController {
    @Autowired
   private IFileApkService fileApkService;

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public Result upload(@RequestParam("file")MultipartFile file){
      log.info("size:{}",file.getSize());
      log.info("name:{}",file.getName());
      log.info("getOriginalFilename:{}",file.getOriginalFilename());
        try {
            File file2 = MultipartFileToFile.multipartFileToFile(file);
            Map<String, String> map = ReadApkUtils.apkFile(file2);
            //版本号
            String versionName = map.get(ReadApkUtils.VERSION_NAME);
            MultipartFileToFile.delteTempFile(file2);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.getDefaultTrue();
    }

    @PostMapping("saveApkInfo")
    @Transactional
    public Result saveApkInfo(@RequestBody @Valid ApkInfoVo apkInfoVo){
        FileApk fileApk=new FileApk();
        BeanUtils.copyProperties(apkInfoVo,fileApk);
        fileApk.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        //判断 如果上传的FileApk是默认 就要查询出 数据库当前默认的版本修改为默认失效
        if (fileApk.getIsDefault()) {
            FileApk updateFileApk= fileApkService.getOne(new QueryWrapper<FileApk>().lambda().eq(FileApk::getType,fileApk.getType())
                   .eq(FileApk::getIsDefault,true));
            if (null !=updateFileApk ) {
                updateFileApk.setIsDefault(false);
                fileApkService.updateById(updateFileApk);
            }
        }
        return fileApkService.save(fileApk)?Result.getDefaultTrue():Result.getDefaultFalse();
    }

    /**
     * 分页查询列表
     * @param currentPage
     * @param pageSize
     * @return
     */
    @GetMapping("/list")
    public Result list(@RequestParam("currentPage") Integer currentPage,
                       @RequestParam("pageSize") Integer pageSize) {
        Page page=new Page(currentPage,pageSize);
        IPage iPage = fileApkService.page(page,new QueryWrapper<FileApk>().lambda().orderByDesc(FileApk::getId));
        Result result = Result.getDefaultTrue();
        result.setData(iPage);
        return result;
    }

    /**
     * 更具id删除
     * @param map
     * @return
     */
    @PostMapping("deleteById")
    public Result deleteById(@RequestBody Map<String,Integer> map ){
        if (null == map.get("id")) {
            throw  new MyServiceException("500","缺少请求参数");
        }
        return fileApkService.removeById(map.get("id"))?Result.getDefaultTrue():Result.getDefaultFalse();
    }
    @PostMapping("updateById")
    public Result updateById(@RequestBody @Valid UpdateApkInfoVo apkInfoVo){
        FileApk fileApk=new FileApk();
        if (apkInfoVo.getIsDefault()) {
            FileApk updateFileApk= fileApkService.getOne(new QueryWrapper<FileApk>().lambda().eq(FileApk::getType,apkInfoVo.getType())
                    .eq(FileApk::getIsDefault,true));
            if (null !=updateFileApk ) {
                updateFileApk.setIsDefault(false);
                fileApkService.updateById(updateFileApk);
            }
        }
        BeanUtils.copyProperties(apkInfoVo,fileApk);
        return fileApkService.updateById(fileApk)?Result.getDefaultTrue():Result.getDefaultFalse();
    }

    @GetMapping("/nofilter/getApkUrl/{type}")
    public Result getApkUrl(@PathVariable("type") Integer type){
        FileApk fileApk=fileApkService.getOne(new QueryWrapper<FileApk>().lambda().eq(FileApk::getType,type).eq(FileApk::getIsDefault,true));
        if (null == fileApk) {
            Result result = Result.getDefaultFalse();
            result.setMsg("当前没有APK下载");
            return result;
        }
        String apkPath = FileConfigVo.getApkPath(fileApk.getFileUrl());
        Result result = Result.getDefaultTrue();
        result.setData(apkPath);
        return result;

    }


}
