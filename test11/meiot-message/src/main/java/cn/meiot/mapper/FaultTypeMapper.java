package cn.meiot.mapper;

import cn.meiot.entity.FaultType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-24
 */
public interface FaultTypeMapper extends BaseMapper<FaultType> {
    @Select("select * from fault_type")
    List<FaultType> selectTypeList();
}
