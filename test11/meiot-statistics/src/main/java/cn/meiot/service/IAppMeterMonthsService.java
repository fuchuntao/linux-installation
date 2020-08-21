package cn.meiot.service;

import cn.meiot.entity.AppMeterHours;
import cn.meiot.entity.AppMeterMonths;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-19
 */
public interface IAppMeterMonthsService extends IService<AppMeterMonths> {

    /**
     * 查询安于统计的电量使用量
     * @param appMeterVo
     * @return
     */
    Result getList(AppMeterVo appMeterVo);

    /**
     * 将昨天的电流总数统计出来存放到月度表中
     * @return
     */
    Result dayStatistics(Integer year,Integer month,Integer day);


    /**
     *
     * @Title: pullAppDayStatistics
     * @Description: 手动拉取数据
     * @param year
     * @param month
     * @param day
     * @return: cn.meiot.entity.vo.Result
     */
    Result pullAppDayStatistics(int year, int month, int day);
}
