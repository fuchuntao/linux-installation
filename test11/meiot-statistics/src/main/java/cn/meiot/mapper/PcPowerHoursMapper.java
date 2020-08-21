package cn.meiot.mapper;

import cn.meiot.entity.PcPowerHours;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 企业平台天数据统计表 Mapper 接口
 * </p>
 *
 * @author 凌志颖
 * @since 2019-10-21
 */
@Mapper
public interface PcPowerHoursMapper extends BaseMapper<PcPowerHours> {

	List<Map<String, Object>> queryStatisticsWek(@Param("year")int monYear, @Param("month")int sunMonth, @Param("fristDay")int monDay,@Param("lastDay") int sunDay,@Param("projectId")Integer projectId);

}
