package cn.meiot.service;

import cn.meiot.entity.AppMeterYears;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-19
 */
public interface IAppMeterYearsService extends IService<AppMeterYears> {

    /**
     * 获取当月的电量
     * @param appMeterVo
     * @return
     */
    Result getNowMonth(AppMeterVo appMeterVo);

    /**
     * 获取按年统计的数据里表
     * @param appMeterVo
     * @return
     */
    Result getList(AppMeterVo appMeterVo);


    /**
     * app获取按年统计的数据里表
     * @param parametersDto
     * @param appMeterVo
     * @return
     */
    Result getListApp(ParametersDto parametersDto, AppMeterVo appMeterVo);

    /**
     * 将上个月的电流总数统计出来存放到年度表中
     * @return
     */
    Result monthStatistics(Integer year,Integer month);

    /**
     *
     * @param serialNumber 设备序列号
     * @param rtuserId 主账户id
     * @param type 类型  1：6个月之内    2：一年之内
     * @return
     */
    Result batteryLeft(String serialNumber, Long rtuserId,Integer type, Integer projectId);



    Result pullAppMonthStatistics(int year, int month);


    /**
     *
     * @Title: getInformation
     * @Description: 运维报告基本信息
     * @param userId
     * @param projectId
     * @return: cn.meiot.entity.vo.Result
     */
    Result getInformation(Long userId,Integer projectId);

    /**
     *
     * @Title: getMonthlyMeter
     * @Description: app的每月用电量
     * @param userId
     * @param projectId
     * @param startTime
     * @param type
     * @return: cn.meiot.entity.vo.Result
     */
    Result getMonthlyMeter(Long userId,Integer projectId, Long startTime, Integer type);


    /**
     *
     * @Title: getPeakValleyMeter
     * @Description: app年度用电谷峰
     * @param userId
     * @param projectId
     * @param startTime
     * @param type
     * @return: cn.meiot.entity.vo.Result
     */
    Result getPeakValleyMeter(Long userId,Integer projectId, Long startTime, Integer type);
}
