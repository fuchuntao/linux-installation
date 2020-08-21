package cn.meiot.mapper;

import cn.meiot.entity.WaterRecordError;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 水表抄表记录异常表 Mapper 接口
 * </p>
 *
 * @author fct
 * @since 2020-02-28
 */
@Mapper
public interface WaterRecordErrorMapper extends BaseMapper<WaterRecordError> {



    /**
     *
     * @Title: selectByStatus
     * @Description: 查询异常数据的状态 status==0
     * @param status
     * @return: java.util.List<cn.meiot.entity.WaterRecordError>
     */
    List<WaterRecordError> selectByStatus(@Param("status") Integer status);


}
