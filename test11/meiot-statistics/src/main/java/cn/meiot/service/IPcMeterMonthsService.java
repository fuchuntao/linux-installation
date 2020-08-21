package cn.meiot.service;

import cn.meiot.entity.PcMeterMonths;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 企业平台月统计表 服务类
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
public interface IPcMeterMonthsService extends IService<PcMeterMonths> {

    /**
     * 查询安于统计的电量使用量
     * @param appMeterVo
     * @return
     */
    Result getPcList(AppMeterVo appMeterVo);

    /**
     * 将昨天的电流总数统计出来存放到月度表中
     * @return
     */
    Result dayStatisticsPc(int year, int month, int day);


    /**
     *
     * @Title: pullPcDayStatistics
     * @Description: 手动拉取数据
     * @param year
     * @param month
     * @param day
     * @return: cn.meiot.entity.vo.Result
     */
    Result pullPcDayStatistics(int year, int month, int day);




    /**
     *
     * @Title: pcMonthAndDayStatistics
     * @Description: 查询当月和当天的电量
     * @param serialNumber
     * @param masterSn
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    Map<String, Object> pcMonthAndDayStatistics(String serialNumber, Long masterSn, Integer projectId);


    /**
     *
     * @Title: pcMonthAndDayStatistics
     * @Description: 查询企业app当月
     * @param serialNumber
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    Result appMonthStatistics(String serialNumber, Integer projectId, Long startTime);

}
