package cn.meiot.service.impl;

import cn.meiot.entity.AppMeterHours;
import cn.meiot.entity.PcMeterHours;
import cn.meiot.entity.device.DeviceBase;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.mapper.PcMeterHoursMapper;
import cn.meiot.service.IPcMeterHoursService;
import cn.meiot.utils.CommonUtil;
import cn.meiot.utils.ConstantsUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 企业平台天数据统计表 服务实现类
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
@Slf4j
@Service
public class PcMeterHoursServiceImpl extends ServiceImpl<PcMeterHoursMapper, PcMeterHours> implements IPcMeterHoursService {

    @Autowired
    private PcMeterHoursMapper pcMeterHoursMapper;

    @Autowired
    private CommonUtil commonUtil;
    /**
     *
     * @Title: getPcMeterHours
     * @Description: 队列拉取获取企业的电量
     * @param
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    public void getPcMeterHours(Map<String, Object> map, DeviceBase deviceBase, Date date,String serialNumber,Integer switchIndex,
    int year, int month, int day, int hour, Integer userType) {
        log.info("根据设备序列号获取用户类型为企业类型");
        PcMeterHours pcMeterHours = null;
        if (0 != hour) {
            log.info("查询数据库是否存在上一次的数据");
            pcMeterHours = pcMeterHoursMapper.getLastInfoByConditionPc(map);

        }
        //将要保存到数据库的电量
        BigDecimal meter = BigDecimal.ZERO;
        //设备上传的电量
        BigDecimal deviceMeter = deviceBase.getPayload().getDesired().getArrays().get(0).getStatus().getMeterd();
        log.info("设备上传的电量：{}", deviceMeter);
        if (null == pcMeterHours) {
            meter = deviceMeter;
        } else {
            log.info("获取到之前的总电量：{}", pcMeterHours.getMeter());
            meter = deviceMeter.subtract(pcMeterHours.getMeter());
        }
        //判断当前时间的数据是否存在
        map.put("s_time", hour + 1);
        Long id = pcMeterHoursMapper.getCountByConditionPc(map);
        if (null == id || 0 == id) {
            log.info("执行了插入操作");
            //获取设备号的主账户
            //Long rtUserId = 1l;
            Long rtUserId = commonUtil.getRtUserIdBySerialNumber(deviceBase.getPayload().getDeviceid());
            if (null == rtUserId) {
                log.info("设备号：{}未获取到主账号id， ！", serialNumber);
                return;
            }
            //数据库没有数据，执行插入操作
            pcMeterHours = PcMeterHours.builder().meter(meter).createTime(ConstantsUtil.DF.format(date)).serialNumber(deviceBase.getPayload().getDeviceid())
                    .switchIndex(switchIndex)
                    .switchSn(deviceBase.getPayload().getDesired().getArrays().get(0).getDevice().getId())
                    .sYear(year)
                    .sMonth(month)
                    .sDay(day)
                    .sTime(hour + 1)
                    .userId(rtUserId)
                    .projectId(Long.valueOf(userType))
                    .build();


            pcMeterHoursMapper.insert(pcMeterHours);
            return;
        }
        log.info("执行了更新操作");
        //执行修改操作
        UpdateWrapper<PcMeterHours> updateWrapper = new UpdateWrapper<PcMeterHours>();
//        updateWrapper.set("project_id",Long.valueOf(userType));
        updateWrapper.set("meter", meter);
        updateWrapper.set("update_time", ConstantsUtil.DF.format(date));
        updateWrapper.eq("id", id);
        this.update(updateWrapper);
    }


    /**
     *
     * @Title: getCountByConditionPc
     * @Description: 通过条件查询信息
     * @param map
     * @return: java.lang.Integer
     */
    @Override
    public Long getCountByConditionPc(Map<String, Object> map) {
        return  pcMeterHoursMapper.getCountByConditionPc(map);
    }


    /**
     *
     * @Title: getLastInfoByConditionPc
     * @Description: 获取上一个小时之前的数据
     * @param map
     * @return: cn.meiot.entity.PcMeterHours
     */
    @Override
    public PcMeterHours getLastInfoByConditionPc(Map<String, Object> map) {
        return pcMeterHoursMapper.getLastInfoByConditionPc(map);
    }


    /**
     *
     * @Title: getListPc
     * @Description: TODO 功能描述
     * @param appMeterVo
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    @Override
    public List<Map<String, Object>> getListPc(AppMeterVo appMeterVo) {
        return null;
    }


    /**
     *
     * @Title: gettotalMeterBySerialNumberPc
     * @Description: TODO 功能描述
     * @param userId
     * @param serialNumber
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    public Result gettotalMeterBySerialNumberPc(Long userId, String serialNumber) {
        return null;
    }
}
