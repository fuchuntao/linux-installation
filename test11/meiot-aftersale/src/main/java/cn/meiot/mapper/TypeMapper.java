package cn.meiot.mapper;

import cn.meiot.entity.Type;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Package cn.meiot.mapper
 * @Description:
 * @author: 武有
 * @date: 2019/9/17 15:16
 * @Copyright: www.spacecg.cn
 */
@Mapper
public interface TypeMapper extends BaseMapper<Type> {
    /**
     * 获取所有的类型
     * @return
     */
    @Select("select * from `type`")
    List<Type> selecTypeList();

    Integer addType(String name);
}
