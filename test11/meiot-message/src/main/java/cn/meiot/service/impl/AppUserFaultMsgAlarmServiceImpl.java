package cn.meiot.service.impl;

import cn.meiot.entity.AppUserFaultMsgAlarm;
import cn.meiot.entity.FaultType;
import cn.meiot.entity.vo.DeviceInfoVo;
import cn.meiot.entity.vo.FaultMessageAndTypeVo;
import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.mapper.AppUserFaultMsgAlarmMapper;
import cn.meiot.service.IAppUserFaultMsgAlarmService;
import cn.meiot.utils.CommonUtil;
import cn.meiot.utils.ConstantUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wuyou
 * @since 2019-10-22
 */
@Service
@Slf4j
@SuppressWarnings("ALL")
public class AppUserFaultMsgAlarmServiceImpl extends ServiceImpl<AppUserFaultMsgAlarmMapper, AppUserFaultMsgAlarm> implements IAppUserFaultMsgAlarmService {

    @Autowired
    private AppUserFaultMsgAlarmMapper appUserFaultMsgAlarmMapper;
    @Autowired
    private CommonUtil commonUtil;
    private final NumberFormat numberFormat = NumberFormat.getInstance();
    @Override
    public List<FaultMessageAndTypeVo> getFaultMsgByTypeAndUserId(Integer type, Long userId, Integer currentPage, Integer pageSize) {
        return appUserFaultMsgAlarmMapper.selectFaultMsgByTypeAndUserId(type,userId,currentPage,pageSize);
    }

    @Override
    public Integer getFaultMsgByTypeAndUserIdTotal(Integer type, Long userId, Integer currentPage, Integer pageSize) {
        return appUserFaultMsgAlarmMapper.selectFaultMsgByTypeAndUserIdTotal(type,userId,currentPage,pageSize);
    }

    @Override
    public Result statisticsWarn(Long userId, String serialNumber) {
        Result result = Result.getDefaultTrue();
        log.info("查询的用户id：{}", userId);
        DeviceInfoVo deviceInfoVo = DeviceInfoVo.builder().userId(userId).serialNumber(serialNumber).build();
        Integer num = appUserFaultMsgAlarmMapper.getAllFaultNumBySerialNumber(deviceInfoVo);
        deviceInfoVo.setTotalNum(num);
        List<Map<String, Object>> list = statistics(deviceInfoVo);
        log.info("返回结果：{}", list);
        //获取某个设备号的所有故障总数
        // List<Map<String, Object>> map = faultMessageMapper.statisticsWarn(userId,serialNumber);
        result.setData(list);
        return result;
    }

    @Override
    public Result unread(Long userId) {
        Result result = Result.getDefaultTrue();
        //获取未读故障消息的数量
        Integer count = appUserFaultMsgAlarmMapper.getUnreadNum(userId);
        //获取故障未读消息列表（前五条）
        List<Map<String, Object>> list = appUserFaultMsgAlarmMapper.getUnread(userId, ConstantUtil.UNREAD_MSG_SHOW_NUM);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("unreadNum", count);
        map.put("list", list);
        result.setData(map);
        return result;
    }

    @Override
    public Result getReportTotal(Long userId) {
        Result result = Result.getDefaultTrue();
        DeviceInfoVo deviceInfoVo = DeviceInfoVo.builder().userId(userId).build();
        List<Map<String, Object>> list = statistics(deviceInfoVo);
        result.setData(list);
        return result;
    }

    @Override
    public void deleteMsgByUserIdAndSerialNumber(Long userId, String serialNumber) {
        AppUserFaultMsgAlarm appUserFaultMsgAlarm=new AppUserFaultMsgAlarm();
        appUserFaultMsgAlarm.setIsShow(1);
        appUserFaultMsgAlarmMapper.update(appUserFaultMsgAlarm,new UpdateWrapper<AppUserFaultMsgAlarm>().eq("user_id",userId)
                .eq("serial_number",serialNumber));
    }

    /**
     * 统计事件与总数
     *
     * @param deviceInfoVo
     * @return
     */
    private List<Map<String, Object>> statistics(DeviceInfoVo deviceInfoVo) {
        //获取当前用户有多少事件
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Integer total = 0;
        //获取事件
        List<FaultType> faultTypes = commonUtil.getEvents();
        if (null == faultTypes) {
            return null;
        }
        //获取图片配置信息
        ImgConfigVo imgConfigVo = commonUtil.getImgConfig();
        faultTypes.forEach(faultType -> {
            Map<String, Object> map = new HashMap<String, Object>();
            //获取指定事件的总数
            deviceInfoVo.setEvent(faultType.getId());
            Integer count = 0;
            if (StringUtils.isEmpty(deviceInfoVo.getSerialNumber())) {
                //获取某个事件的未读消息数量
                count = appUserFaultMsgAlarmMapper.findCountByEvent(deviceInfoVo);
                Integer unradNUm = appUserFaultMsgAlarmMapper.findUnreadNumByEvent(deviceInfoVo);
                map.put("unreadNum", unradNUm);
            } else {
                // 设置精确到小数点后2位
                numberFormat.setMaximumFractionDigits(2);
                if (deviceInfoVo.getTotalNum() == 0) {
                    map.put("percentage", 0 + "%");
                } else {
                    count = appUserFaultMsgAlarmMapper.findCountByEvent(deviceInfoVo);
                    map.put("percentage", numberFormat.format((float) count / (float) deviceInfoVo.getTotalNum() * 100) + "%");

                }
            }
            map.put("switchEvent", faultType.getId());
            map.put("eventName", faultType.getFName());
            map.put("fAlias",faultType.getFAlias());
            map.put("fAymbol",faultType.getFAymbol());
            map.put("total", count);
            if (null != imgConfigVo) {
                map.put("img", imgConfigVo.getServername() + imgConfigVo.getMap()+imgConfigVo.getImg() + faultType.getFImg());
                log.info(imgConfigVo.getServername() + imgConfigVo.getMap()+imgConfigVo.getImg() + faultType.getFImg());
            }
            list.add(map);
        });
        return list;

    }
}
