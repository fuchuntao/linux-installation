package cn.meiot.mapper;

import cn.meiot.entity.Files;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wuyou
 * @since 2019-11-21
 */
@Mapper
public interface FilesMapper extends BaseMapper<Files> {

    /**
     *
     * @param version
     * @return
     */
    List<String> selectNameByVersion(String version);
}
