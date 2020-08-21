package cn.meiot.service;

import cn.meiot.entity.PcLeakageYears;
import cn.meiot.entity.vo.Result;

import java.util.Calendar;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 企业平台年数据统计表 服务类
 * </p>
 *
 * @author 凌志颖
 * @since 2019-10-21
 */
public interface IPcLeakageYearsService extends IService<PcLeakageYears> {
	 /**
     * 将上个月的电流总数统计出来存放到年度表中
     * @return
     */
    Result monthStatistics(String tableName, Integer year,Integer month);
}
