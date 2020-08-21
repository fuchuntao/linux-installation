package cn.meiot.mapper;

import cn.meiot.entity.PcDeviceStatistics;
import cn.meiot.entity.vo.AppMeterHoursVo;
import cn.meiot.entity.vo.ParametersDto;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 设备数据统计表 Mapper 接口
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
@Mapper
@DS("master")
public interface PcDeviceStatisticsMapper extends BaseMapper<PcDeviceStatistics> {


    /**
     *
     * @Title: selectDeviceStatistics
     * @Description: 查询设备和开关
     * @param
     * @return: java.util.List<cn.meiot.entity.vo.AppMeterHoursVo>
     */
    @DS("db_2")
    List<AppMeterHoursVo> selectDeviceStatistics();


    /**
     *
     * @Title: selectListTime
     * @Description: TODO 功能描述
     * @param parametersDto
     * @return: java.util.List<java.lang.Integer>
     */
    List<Integer> selectListTime(ParametersDto parametersDto);




}



