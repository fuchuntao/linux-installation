package cn.meiot.mapper;

import cn.meiot.entity.PcLeakageYears;
import cn.meiot.entity.vo.StatisticsDto;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 企业平台年数据统计表 Mapper 接口
 * </p>
 *
 * @author 凌志颖
 * @since 2019-10-21
 */
@Mapper
public interface PcLeakageYearsMapper extends BaseMapper<PcLeakageYears> {

	/**
	 * 添加数据到年表
	 * @param list
	 * @param tableName
	 */
	void insertList(@Param("list")List<StatisticsDto> list,@Param("tableName") String tableName);

}
