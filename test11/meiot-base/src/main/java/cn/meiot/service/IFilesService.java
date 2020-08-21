package cn.meiot.service;

import cn.meiot.entity.Files;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wuyou
 * @since 2019-11-21
 */
public interface IFilesService extends IService<Files> {
    /**
     *根据版本号查询真实文件名 UUID
     * @param version
     * @return
     */
    List<String> getNameByVersion(String version);
}
