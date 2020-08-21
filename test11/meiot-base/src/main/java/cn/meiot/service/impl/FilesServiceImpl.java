package cn.meiot.service.impl;

import cn.meiot.entity.Files;
import cn.meiot.mapper.FilesMapper;
import cn.meiot.service.IFilesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wuyou
 * @since 2019-11-21
 */
@Service
public class FilesServiceImpl extends ServiceImpl<FilesMapper, Files> implements IFilesService {
    @Autowired
    private FilesMapper filesMapper;
    @Override
    public List<String> getNameByVersion(String version) {

//        return filesMapper.selectNameByVersion(version);
        return null;
    }
}
