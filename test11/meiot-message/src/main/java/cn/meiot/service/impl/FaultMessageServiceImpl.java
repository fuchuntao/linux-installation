package cn.meiot.service.impl;

import cn.meiot.entity.FaultMessage;
import cn.meiot.entity.FaultType;
import cn.meiot.entity.vo.*;
import cn.meiot.exception.MyServiceException;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.feign.UserFeign;
import cn.meiot.jg.JPushClientExample;
import cn.meiot.mapper.FaultMessageMapper;
import cn.meiot.service.IFaultMessageService;
import cn.meiot.utils.CommonUtil;
import cn.meiot.utils.ConstantUtil;
import cn.meiot.enums.FaultTitleEnum;
import cn.meiot.utils.RedisConstantUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 故障消息 服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-12
 */
@Service
@Slf4j
@SuppressWarnings("all")
public class FaultMessageServiceImpl extends ServiceImpl<FaultMessageMapper, FaultMessage> implements IFaultMessageService {

    @Autowired
    private FaultMessageMapper faultMessageMapper;

    @Value("${jpush.repetitionSendTime}")
    private Integer repetitionSendTime;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DeviceFeign deviceFeign;


    private final NumberFormat numberFormat = NumberFormat.getInstance();


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
                count = faultMessageMapper.findCountByEvent(deviceInfoVo);
                Integer unradNUm = faultMessageMapper.findUnreadNumByEvent(deviceInfoVo);
                map.put("unreadNum", unradNUm);
            } else {
                // 设置精确到小数点后2位
                numberFormat.setMaximumFractionDigits(2);
                if (deviceInfoVo.getTotalNum() == 0) {
                    map.put("percentage", 0 + "%");
                } else {
                    count = faultMessageMapper.findCountByEvent(deviceInfoVo);
                    map.put("percentage", numberFormat.format((float) count / (float) deviceInfoVo.getTotalNum() * 100) + "%");

                }
            }
            map.put("switchEvent", faultType.getId());
            map.put("eventName", faultType.getFName());
            map.put("fAlias",faultType.getFAlias());
            map.put("fAymbol",faultType.getFAymbol());
            map.put("total", count);
            if (null != imgConfigVo) {
                map.put("img", imgConfigVo.getServername() + imgConfigVo.getMap()+imgConfigVo.getImg() +imgConfigVo.getThumbnail()+ faultType.getFImg());
            }
            list.add(map);
        });
        return list;

    }


    @Override
    public Result getReportTotal(Long userId) {

        Result result = Result.getDefaultTrue();
        DeviceInfoVo deviceInfoVo = DeviceInfoVo.builder().userId(userId).build();
        List<Map<String, Object>> list = statistics(deviceInfoVo);
        result.setData(list);
        return result;
    }

//    @Override
//    public Result repetitionSend() {
//        GregorianCalendar gc=new GregorianCalendar();
//        gc.setTime(new Date());
//        gc.add(Calendar.MINUTE,-repetitionSendTime);
//        String dateTime = df.format(gc.getTime());
//        log.info("上一次推送的时间：{}",dateTime);
//                //查询出所有的未读消息
//        List<FaultMessage> faultMessageList = faultMessageMapper.selectList(new QueryWrapper<FaultMessage>().eq("is_read", 0).lt("send_time",dateTime));
//        if (null == faultMessageList) {
//            log.info("没有未读消息");
//            return Result.getDefaultTrue();
//        }
//        List<String> list = null;
//        //便利未读消列表
//        for (FaultMessage f : faultMessageList) {
//            list = new ArrayList<String>();
//            list.add(f.getUserId().toString());
//            jPushClientExample.sendMsg(list, FaultTitleEnum.getTitle(f.getSwitchEvent()), FaultTitleEnum.getTitle(f.getSwitchEvent()),
//                    f.getMsgContent(), JpushTypeEnum.NOTIFICATION.value(), null);
//        }
//
//        return Result.getDefaultTrue();
//    }

    @Override
    public Result statisticsWarn(Long userId, String serialNumber) {
        Result result = Result.getDefaultTrue();
        log.info("查询的用户id：{}", userId);
        DeviceInfoVo deviceInfoVo = DeviceInfoVo.builder().userId(userId).serialNumber(serialNumber).build();
        Integer num = faultMessageMapper.getAllFaultNumBySerialNumber(deviceInfoVo);
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
        Integer count = faultMessageMapper.getUnreadNum(userId);
        //获取故障未读消息列表（前五条）
        List<Map<String, Object>> list = faultMessageMapper.getUnread(userId, ConstantUtil.UNREAD_MSG_SHOW_NUM);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("unreadNum", count);
        map.put("list", list);
        result.setData(map);
        return result;
    }

    @Override
    public Result getFaultMessageList(Map map) {
        log.info(JSONObject.toJSONString(map));
        String distributionBoxName= (String) map.get("distributionBoxName");
        //获取主账户id
//        Long userId=userFeign.getMainUserIdByUserId((Long) map.get("userId"));
        Long userId=(Long) map.get("userId");
        log.info("用户主id是"+userId);
        map.put("userId",userId);
        //获取redis里的名称 然后获取key
        List<String> keyList =null;
        //判断内是根据前端穿过来的值在redis内匹配名称获取设备号
        if (StringUtils.isNotEmpty(distributionBoxName)){
            keyList=deviceFeign.getSerialNumberListByName(distributionBoxName);
            //拿取所有key的集合
//            Set<Object> keys = redisTemplate.opsForHash().keys(RedisConstantUtil.NIKNAME_SERIALNUMBER);
//            //判断key不为空
//            if (!keys.isEmpty()) {
//                keyList=new ArrayList<>();
//                //遍历keys redis里的value包含传来的值就把 key加载keyList
//                for (Object tempSerialNumber : keys) {
//                    String name = (String) redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SERIALNUMBER, tempSerialNumber);
//                    if (name.indexOf(distributionBoxName.trim())!=-1){
//                        String s= (String) tempSerialNumber;
//                        String[] s1 = s.split("_");
//                        //将设备号加入集合准备查询
//                        keyList.add(s1[1]);
//                    }
//                }
//
//            }
        }
        //将设备号加入查询参数中
       if (null!=keyList && keyList.size()>0){
           map.put("keyList",keyList);
           log.info("搜索redis的设备号"+JSONObject.toJSONString(keyList));
       }else if (StringUtils.isNotEmpty(distributionBoxName)){
           return Result.getDefaultTrue();
       }
        List<FaultMessageVo> faultMessageVoList = faultMessageMapper.getFaultMessageList(map);
        //把查询出来的设备号于开关号替换成名字
        for (FaultMessageVo faultMessageVo:faultMessageVoList) {
            String address =deviceFeign.getAddressBySerialNumber(faultMessageVo.getDistributionBoxName());

            if (null != address) {
                String substring = address.substring(1);
                faultMessageVo.setAddress(substring);
            }
            faultMessageVo.setAddress(address);
            String tempDistributionBoxName =(String)  redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SERIALNUMBER,userId+"_"+faultMessageVo.getDistributionBoxName());
            String switchName =(String) redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SWITCH,userId+"_"+faultMessageVo.getSwitchName());
            log.info("配电箱名称="+tempDistributionBoxName+"-------开关名称="+switchName);
            faultMessageVo.setDistributionBoxName(tempDistributionBoxName);
            faultMessageVo.setSwitchName(switchName);
        }
        //查询total
        Integer total = faultMessageMapper.getFaultMessageListTotal(map);
        Result result = Result.getDefaultTrue();
        Map<String, Object> resultMap = new HashMap();
        resultMap.put("faultMessageVoList", faultMessageVoList);
        resultMap.put("total", total);
        log.info("total="+total);
        log.info("faultMessageVoList.size="+faultMessageVoList.size());
        result.setData(resultMap);
        return result;
    }

    @Override
    @Transactional
    public Integer deleteMsgByUserIdAndSerialNumber(Long userId, String serialNumber) {
        return  faultMessageMapper.deleteMsgByUserIdAndSerialNumber(userId,serialNumber);
    }

    @Override
    public List<FaultMessageAndTypeVo> getFaultMsgByTypeAndUserId(Integer type, Long userId, Integer currentPage, Integer pageSize) {
        return faultMessageMapper.selectFaultMsgByTypeAndUserId(type,userId,currentPage,pageSize);
    }

    @Override
    public Integer getFaultMsgByTypeAndUserIdTotal(Integer type, Long userId, Integer currentPage, Integer pageSize) {
        return faultMessageMapper.selectFaultMsgByTypeAndUserIdTotal(type,userId,currentPage,pageSize);
    }

    @Override
    public List<StatisticsEventTimeVo> getCountByEventAndTime(String time, Integer event,Long userId,Integer projectId) {
        List<StatisticsEventTimeVo> eventTimeVos = faultMessageMapper.getCountByEventAndTime(time, event, userId, projectId);
        return eventTimeVos;
    }
}
