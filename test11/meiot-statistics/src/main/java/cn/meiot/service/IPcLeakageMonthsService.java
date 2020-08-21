package cn.meiot.service;

import cn.meiot.entity.PcLeakageMonths;
import cn.meiot.entity.vo.Result;

import java.util.Calendar;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 企业平台月数据统计表 服务类
 * </p>
 *
 * @author 凌志颖
 * @since 2019-10-21
 */
public interface IPcLeakageMonthsService extends IService<PcLeakageMonths> {
	/**
	 * 统计天表数据  添加到月表中
	 * tableName 为
	 */
	public Result dayStatistics(String tableName,Integer year,Integer month,Integer day) ;
}
