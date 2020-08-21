package cn.meiot.service;

import cn.meiot.entity.AppMeterHours;
import cn.meiot.entity.PcMeterHours;
import cn.meiot.entity.device.DeviceBase;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 企业平台天数据统计表 服务类
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
public interface IPcMeterHoursService extends IService<PcMeterHours> {


    /**
     *
     * @Title: getPcMeterHours
     * @Description: 队列拉取获取企业的电量
     * @param
     * @return: cn.meiot.entity.vo.Result
     */
    void getPcMeterHours(Map<String, Object> map, DeviceBase deviceBase, Date date, String serialNumber, Integer switchIndex,
                         int year, int month, int day, int hour,Integer userType);

    /**
     * 通过条件查询信息
     * @param map
     * @return
     */
    Long  getCountByConditionPc(Map<String, Object> map);

    /**
     * 获取上一个小时之前的数据
     * @param map
     * @return
     */
    PcMeterHours getLastInfoByConditionPc(Map<String, Object> map);

    /**
     * 获取列表
     * @param appMeterVo
     * @return
     */
    List<Map<String,Object>> getListPc(AppMeterVo appMeterVo);

    /**
     * 通过设备型号获取
     * @param userId
     * @param serialNumber
     * @return
     */
    Result gettotalMeterBySerialNumberPc(Long userId, String serialNumber);



}
