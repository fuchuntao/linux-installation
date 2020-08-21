package cn.meiot.mapper;

import cn.meiot.entity.Config;
import cn.meiot.entity.bo.ConfigUserBo;
import cn.meiot.entity.vo.ConfigVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2020-02-28
 */
@Mapper
public interface ConfigMapper extends BaseMapper<Config> {

    /**
     * 通过id修改配置信息
     * @param configVo
     * @return
     */
    Integer updateConfigById(ConfigVo configVo);

    /**
     * 通过类型获取配置项
     * @param type
     * @return
     */
    @Select(" select title, c_key  as ckey,value as value,key_type as keyType,description from config where type=#{type}  or type = 0 ")
    List<ConfigUserBo> getListByType(int type);

    /**
     * 通过key获取value
     * @param key
     * @return
     */
    String getValueByKey(String key);
}
