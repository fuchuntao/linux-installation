package cn.meiot.service.impl;

import cn.meiot.entity.*;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.vo.*;
import cn.meiot.enums.FaultTitleEnum;
import cn.meiot.exception.MyServiceException;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.feign.UserFeign;
import cn.meiot.mapper.AppUserFaultMsgAlarmMapper;
import cn.meiot.mapper.EnterpriseAlarmDao;
import cn.meiot.mapper.EnterpriseUserFaultMsgAlarmMapper;
import cn.meiot.mapper.FaultTypeMapper;
import cn.meiot.service.IEnterpriseUserFaultMsgAlarmService;
import cn.meiot.service.ISystemMessageService;
import cn.meiot.utils.*;
import cn.meiot.utils.enums.AlarmStatusEnum;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wuyou
 * @since 2019-10-22
 */
@Service
@Slf4j
@SuppressWarnings("all")
public class EnterpriseUserFaultMsgAlarmServiceImpl extends ServiceImpl<EnterpriseUserFaultMsgAlarmMapper, EnterpriseUserFaultMsgAlarm> implements IEnterpriseUserFaultMsgAlarmService {

    @Autowired
    private EnterpriseUserFaultMsgAlarmMapper enterpriseUserFaultMsgAlarmMapper;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private DeviceFeign deviceFeign;
    private final NumberFormat numberFormat = NumberFormat.getInstance();
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserFeign userFeign;

    @Autowired
    private ISystemMessageService systemMessageService;

    @Autowired
    private FaultTypeMapper faultTypeMapper;

    @Autowired
    private EnterpriseAlarmDao enterpriseAlarmDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserInfoUtil userInfoUtil;





    @Override
    public List<FaultMessageAndTypeVo> getFaultMsgByTypeAndUserId(Integer type, Long userId, Integer currentPage, Integer pageSize, Integer projectId) {
        return enterpriseUserFaultMsgAlarmMapper.selectFaultMsgByTypeAndUserId(type, userId, currentPage, pageSize, projectId);
    }

    @Override
    public Integer getFaultMsgByTypeAndUserIdTotal(Integer type, Long userId, Integer currentPage, Integer pageSize, Integer projectId) {
        return enterpriseUserFaultMsgAlarmMapper.selectFaultMsgByTypeAndUserIdTotal(type, userId, currentPage, pageSize, projectId);
    }

    @Override
    public Result statisticsWarn(Long userId, String serialNumber, Integer projectId) {
        Result result = Result.getDefaultTrue();
        log.info("查询的用户id：{}", userId);
        DeviceInfoVo deviceInfoVo = DeviceInfoVo.builder().userId(userId).projectId(projectId).serialNumber(serialNumber).build();
        Integer num = enterpriseUserFaultMsgAlarmMapper.getAllFaultNumBySerialNumber(deviceInfoVo);
        deviceInfoVo.setTotalNum(num);
        List<Map<String, Object>> list = statistics(deviceInfoVo, projectId);
        log.info("返回结果：{}", list);
        //获取某个设备号的所有故障总数
        // List<Map<String, Object>> map = faultMessageMapper.statisticsWarn(userId,serialNumber);
        result.setData(list);
        return result;
    }

    @Override
    public Result unread(Long userId, Integer projectId) {
        Result result = Result.getDefaultTrue();
        //获取未读故障消息的数量
        Integer count = enterpriseUserFaultMsgAlarmMapper.getUnreadNum(userId, projectId);
        //获取故障未读消息列表（前五条）
        List<Map<String, Object>> list = enterpriseUserFaultMsgAlarmMapper.getUnread(userId, ConstantUtil.UNREAD_MSG_SHOW_NUM, projectId);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("unreadNum", count);
        map.put("list", list);
        result.setData(map);
        return result;
    }

    @Override
    public Result getReportTotal(Long userId, Integer projectId) {
        Result result = Result.getDefaultTrue();
        DeviceInfoVo deviceInfoVo = DeviceInfoVo.builder().userId(userId).projectId(projectId).build();
        List<Map<String, Object>> list = statistics(deviceInfoVo, projectId);
        result.setData(list);
        return result;
    }


    /**
     * 统计事件与总数
     *
     * @param deviceInfoVo
     * @return
     */
    private List<Map<String, Object>> statistics(DeviceInfoVo deviceInfoVo, Integer projectId) {
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
                count = enterpriseUserFaultMsgAlarmMapper.findCountByEvent(deviceInfoVo);
                Integer unradNUm = enterpriseUserFaultMsgAlarmMapper.findUnreadNumByEvent(deviceInfoVo);
                map.put("unreadNum", unradNUm);
            } else {
                // 设置精确到小数点后2位
                numberFormat.setMaximumFractionDigits(2);
                if (deviceInfoVo.getTotalNum() == 0) {
                    map.put("percentage", 0 + "%");
                } else {
                    count = enterpriseUserFaultMsgAlarmMapper.findCountByEvent(deviceInfoVo);
                    map.put("percentage", numberFormat.format((float) count / (float) deviceInfoVo.getTotalNum() * 100) + "%");

                }
            }
            map.put("switchEvent", faultType.getId());
            map.put("eventName", faultType.getFName());
            map.put("fAlias", faultType.getFAlias());
            map.put("fAymbol", faultType.getFAymbol());
            map.put("total", count);
            if (null != imgConfigVo) {
                map.put("img", imgConfigVo.getServername() + imgConfigVo.getMap()+imgConfigVo.getImg() + faultType.getFImg());
                log.info(imgConfigVo.getServername() + imgConfigVo.getMap()+imgConfigVo.getImg() + faultType.getFImg());
            }
            list.add(map);
        });
        return list;

    }

    @Override
    public Result getFaultMessageList(Map<String, Object> map) {
        log.info(JSONObject.toJSONString(map));
        String distributionBoxName = (String) map.get("distributionBoxName");
        List<FaultMessageVo> faultMessageVoList = enterpriseUserFaultMsgAlarmMapper.getFaultMessageList(map);
        List<FaultMessageVo> newFaultMessageVo=new ArrayList<>();
        for (FaultMessageVo faultMessageVo:faultMessageVoList){
            String name = FaultMsgUtils.getName(faultMessageVo.getFaultTypeId(), faultMessageVo.getFaultValue());
            faultMessageVo.setHouseholdAppliancesName(name);
            newFaultMessageVo.add(faultMessageVo);
        }
        //查询total
        Integer total = enterpriseUserFaultMsgAlarmMapper.getFaultMessageListTotal(map);
        Result result = Result.getDefaultTrue();
        Map<String, Object> resultMap = new HashMap();
        resultMap.put("faultMessageVoList", newFaultMessageVo);
        resultMap.put("total", total);
        result.setData(resultMap);
        return result;
    }


    @Override
    public Result getStatisticalAlarm(Map<String, Object> map) {
        List<StatisticsEventTimeVo> statisticsEventTimeVos = enterpriseUserFaultMsgAlarmMapper.selectStatisticalAlarm(map);
        List<StatisticsEventTimeVo> resut = new ArrayList<>();
        Integer index = (Integer) map.get("index");
        String date = (String) map.get("currentFormat");
        if (index == 0) {

            for (int i = 0; i < 12; i++) {
                add(statisticsEventTimeVos, resut, i, "0");
                sub(resut,"月",i);
            }

        }
        if (index == 1) {
            for (int i = 0; i < getDayOfMonth(date); i++) {
                add(statisticsEventTimeVos, resut, i, "0");
                sub(resut,"日",i);
            }
        }
        if (index == 2) {
            for (int i = 0; i < 24; i++) {
                add(statisticsEventTimeVos, resut, i, "0");
                sub(resut,"",i);
            }
        }
        Result result = Result.getDefaultTrue();

        result.setData(resut);
        return result;
    }

private void sub(List<StatisticsEventTimeVo> resut,String s,Integer i){


        String time = resut.get(i).getTime();

        String name="";
        if (time.toCharArray()[0]=='0'){
            name=time.substring(1)+s;
            resut.get(i).setTime(name);
        }else {
            name=time+s;
            resut.get(i).setTime(name);
        }

}
    @Override
    public Result getStatisticalAlarmAll(Map<String, Object> map) {
        List<StatisticsEventTimeVo> statisticsEventTimeVos = enterpriseUserFaultMsgAlarmMapper.selectStatisticalAlarmAll(map);
        Result result = Result.getDefaultTrue();
        result.setData(statisticsEventTimeVos);
//        if (null == statisticsEventTimeVos || statisticsEventTimeVos.size() < 1) {
//            result.setData(null);
//            return result;
//        }
        List<StatisticsEventTimeVo> rest = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
//            if ((i + 1) != 2) {
                add(statisticsEventTimeVos, rest, i, "");
//            }
        }
        for (int i = 0; i < rest.size(); i++) {
            rest.get(i).setTime(FaultTitleEnum.getTitle(Integer.valueOf(rest.get(i).getTime())));
        }
        result.setData(rest);
        return result;
    }

    private void add(List<StatisticsEventTimeVo> statisticsEventTimeVos, List<StatisticsEventTimeVo> resut, int i, String s) {
        if (null == statisticsEventTimeVos || statisticsEventTimeVos.size() == 0) {
            resut.add(new StatisticsEventTimeVo("" + (i + 1), 0));
        } else {
            Map<String, StatisticsEventTimeVo> map = new HashMap();
            for (StatisticsEventTimeVo vo : statisticsEventTimeVos) {
                map.put(vo.getTime(), vo);
            }
            String key = String.valueOf(i + 1);
            if (key.length() == 1) {
                key = s + key;
            }
            StatisticsEventTimeVo eventTimeVo = map.get(key);
            if (null != eventTimeVo) {
                resut.add(eventTimeVo);
            } else {
                resut.add(new StatisticsEventTimeVo(key, 0));
            }
        }
    }


    public int getDayOfMonth(String date) {
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM");
        try {
            rightNow.setTime(simpleDate.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rightNow.getActualMaximum(Calendar.DAY_OF_MONTH);//根据年月 获取月份天数;
    }

    @Override
    public Integer getStatisticalAlarmTotal(Map<String, Object> map) {
        return enterpriseUserFaultMsgAlarmMapper.selectTotal(map);
    }

    @Override
    public Integer getFaultNumber(Map<String, Object> map) {
        Integer projectId= (Integer) map.get("projectId");
        Long userId=(Long) map.get("userId");
        //根据项目Id 用户ID查询出所有的故障消息
        List<String> strings = enterpriseUserFaultMsgAlarmMapper.selectFaultNumber(map);
        if (null == strings || strings.size()==0) {
            return 0;
        }
        List<String> strings1 = deviceFeign.querySerialByProjectId(projectId);
        if (null == strings1 || strings1.size()==0) {
            return 0;
        }
        int i=0;
        int size = strings1.size();  // 现有的1 2 3 4   所有的 2 3 4 5  之前的长度4
        strings1.removeAll(strings); // 1
        int size1 = strings1.size(); // 现有的长度  1   4-1  =3
        i = size - size1;
        /*for (String string : strings) {
            for (String s : strings1) {
                if (StringUtils.equals(string,s)){
                    i++;
                }
            }
        }*/
        return i;
    }

    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        strings.add("1");
        strings.add("2");
        strings.add("3");
        strings.add("4");
        List<String> strings1 = new ArrayList<>();
        strings1.add("2");
        strings1.add("3");
        strings1.add("4");
        int i=0;
        int size = strings1.size();  // 现有的1 2 3 4   所有的 2 3 4 5  之前的长度4
        strings1.removeAll(strings); // 1
        int size1 = strings1.size(); // 现有的长度  1   4-1  =3
        i = size - size1;
        System.out.println(i);
    }
    @Override
    public int getUnprocessed(String serialNumber,Long userId) {
        log.info(">>>>>>>>>>>>>>>>>>查询成功");
        return enterpriseUserFaultMsgAlarmMapper.getUnprocessed(serialNumber,userId);
    }

    @Override
    public Result getStatisticsFaultMessage(Map<String, Object> map) {
        List<FaultMessageVo> faultMessageVoList = enterpriseUserFaultMsgAlarmMapper.selectStatisticsFaultMessage(map);
        Integer total=enterpriseUserFaultMsgAlarmMapper.selectStatisticsFaultMessageTotal(map);
        Result result = Result.getDefaultTrue();
        Map<String, Object> resultMap = new HashMap();
        resultMap.put("faultMessageVoList", faultMessageVoList);
        resultMap.put("total", total);
        log.info("total=" + total);
        log.info("faultMessageVoList.size=" + faultMessageVoList.size());
        result.setData(resultMap);
        return result;
    }

    @Override
    public Integer getTotalByFaultType(Integer type,Long userId) {
        return enterpriseUserFaultMsgAlarmMapper.selectTotalByFaultType(type,userId);
    }

    @Override
    public Integer getUnreadNoticeTotal(Long userId, Integer project, Integer type) {
        return enterpriseUserFaultMsgAlarmMapper.getUnreadNoticeTotal(userId,project,type);
    }

    @Override
    public Integer getUnreadTotal(Long userId) {
        return systemMessageService.selectUnreadTotal(userId);
    }

    @Override
    public List<TopTenVo> getTopTen(Map<String, Object> map) {
        List<TopTenVo> topTenVos = enterpriseUserFaultMsgAlarmMapper.selectTopTen(map);
        log.info("===========================================>{}",topTenVos);
        return topTenVos;
    }

    @Override
    public List<StatisticsEventTimeVo> getTotal(Map<String, Object> map) {
        List<StatisticsEventTimeVo> statisticsEventTimeVos = enterpriseUserFaultMsgAlarmMapper.getTotal(map);
        List<StatisticsEventTimeVo> rest = new ArrayList<>();
        if (!map.get("type").equals(2)) {
            for (int i = 0; i < 7; i++) {
                add(statisticsEventTimeVos, rest, i, "");
            }
        } else {
            for (int i = 9; i < 16; i++) {
                add(statisticsEventTimeVos, rest, i, "");
            }
        }
        for (int i = 0; i < rest.size(); i++) {
            rest.get(i).setTime(FaultTitleEnum.getTitle(Integer.valueOf(rest.get(i).getTime())));
        }
        return rest;
    }

    @Override
    public List<StatisticsEventTimeVo> getTotalDetailed(Map<String, Object> map) {
        List<StatisticsEventTimeVo> statisticsEventTimeVos =enterpriseUserFaultMsgAlarmMapper.getTotalDetailed(map);
        List<StatisticsEventTimeVo> resut = new ArrayList<>();
        Integer index = (Integer) map.get("index");
        String date = (String) map.get("currentFormat");
        if (index == 0) {

            for (int i = 0; i < 12; i++) {
                add(statisticsEventTimeVos, resut, i, "0");
                sub(resut, "月", i);
            }

        }
        if (index == 1) {
            for (int i = 0; i < getDayOfMonth(date); i++) {
                add(statisticsEventTimeVos, resut, i, "0");
                sub(resut, "日", i);
            }
        }
        if (index == 2) {
            for (int i = 0; i < 24; i++) {
                add(statisticsEventTimeVos, resut, i, "0");
                sub(resut, "", i);
            }
        }
        return resut;
    }

    @Override
    public List<FaultMessageAndTypeVo> getFaultByUserId(Integer type, Long userId, Integer currentPage, Integer pageSize, Integer project) {
        return enterpriseUserFaultMsgAlarmMapper.getFaultByUserId(type,userId,currentPage,pageSize,project);
    }

    @Override
    public Integer getFaultByUserIdTotal(Integer type, Long userId, Integer currentPage, Integer pageSize, Integer project) {
        return enterpriseUserFaultMsgAlarmMapper.getFaultByUserIdTotal(type,userId,currentPage,pageSize,project);
    }

    @Override
    public List<Map<String, Integer>> warningRate(List<PersonalSerialVo> personalSerialVos, Integer projectId) {
        //当前时间-30天
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -30);
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now.getTime());
        return enterpriseUserFaultMsgAlarmMapper.warningSum(personalSerialVos, projectId, startTime);
    }

    @Override
    public boolean updateStatusAndNoteById(ClickRepairVo clickRepairVo,Long userId,Integer projectId) {
        //先查询出改记录的status是否为一键报修状态 如果不是 提示该故障已经被提交
        EnterpriseUserFaultMsgAlarm msgAlarm = enterpriseUserFaultMsgAlarmMapper.selectById(clickRepairVo.getId());
        //提示记录不存在
        if (null == msgAlarm) {
            throw new MyServiceException(ErrorCodeUtil.RECORD_DOES_NOT_EXIST);
        }
        //提示已经被提交
        if (!msgAlarm.getSwitchStatus().equals(AlarmStatusEnum.YIJIANBAOXIU.value())) {
            throw new MyServiceException(ErrorCodeUtil.HAS_BEEN_SUBMITTED);
        }
        //修改记录
        EnterpriseUserFaultMsgAlarm appUserFaultMsgAlarm = EnterpriseUserFaultMsgAlarm
                .builder().id(clickRepairVo.getId())
                .note(clickRepairVo.getNote())
                .switchStatus(AlarmStatusEnum.DAISHOULI.value()).
                        build();
        int i = enterpriseUserFaultMsgAlarmMapper.updateById(appUserFaultMsgAlarm);

        FaultType faultType = faultTypeMapper.selectById(msgAlarm.getEvent());

        List<EnterpriseAlarm> userAlarmList= enterpriseAlarmDao.selectByAlarmId(msgAlarm.getId());
        // 生成故障工单
        TroubleTicketVo troubleTicketVo = new TroubleTicketVo();
        List<TroubleTicketVo.TroubleTicketVoUserAlarm> userAlarms= new ArrayList<>();
        for (EnterpriseAlarm userAlarm : userAlarmList) {
            TroubleTicketVo.TroubleTicketVoUserAlarm troubleTicketVoUserAlarm=troubleTicketVo.new TroubleTicketVoUserAlarm();
            BeanUtils.copyProperties(userAlarm,troubleTicketVoUserAlarm);
            userAlarms.add(troubleTicketVoUserAlarm);
        }
        Long mainUserId = userFeign.getMainUserIdByUserId(userId);
        troubleTicketVo.setUserAlarms(userAlarms);
        AuthUserBo authUserBo = userInfoUtil.getAuthUserBo(userId);
        String tel = authUserBo.getUser().getUserName();
        troubleTicketVo.setAlarmTime(msgAlarm.getFaultTime());
        troubleTicketVo.setTel(tel);
        troubleTicketVo.setAlarmType(msgAlarm.getEvent());
        troubleTicketVo.setAlarmTypeName(FaultTitleEnum.getTitle(msgAlarm.getEvent()));
        troubleTicketVo.setDeviceId(msgAlarm.getSerialNumber());
        troubleTicketVo.setIsShow(0);
        troubleTicketVo.setUserId(mainUserId);
        troubleTicketVo.setDeviceName(msgAlarm.getEquipmentAlias());
        troubleTicketVo.setType(TroubleStatus.BAOXIU.value());
        troubleTicketVo.setNote(clickRepairVo.getNote());
        troubleTicketVo.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        troubleTicketVo.setSn(msgAlarm.getSwitchSn());
        troubleTicketVo.setSnName(msgAlarm.getSwitchAlias());
        troubleTicketVo.setIsApp(1);
        troubleTicketVo.setFAlias(faultType.getFAlias());
        troubleTicketVo.setFAymbol(faultType.getFAymbol());
        troubleTicketVo.setAlarmId(msgAlarm.getId());
        troubleTicketVo.setAlarmValue(msgAlarm.getFaultValue());
        troubleTicketVo.setProjectId(projectId);
        troubleTicketVo.setAddress(msgAlarm.getAddress());
        troubleTicketVo.setDeviceName(msgAlarm.getEquipmentAlias());
        troubleTicketVo.setSnName(msgAlarm.getSwitchAlias());
        rabbitTemplate.convertAndSend(QueueConstantUtil.TROUBLE_TICKET, JSONObject.toJSONString(troubleTicketVo));
        return i > 0;
    }

    @Override
    public boolean updateNoteById(ClickRepairVo clickRepairVo, Long userId, Integer projectId) {
        Long id = clickRepairVo.getId();
        String note = clickRepairVo.getNote();
        EnterpriseUserFaultMsgAlarm alarm=EnterpriseUserFaultMsgAlarm.builder().id(id).note(note).build();
        int i = enterpriseUserFaultMsgAlarmMapper.updateById(alarm);
        return i>0;
    }

    @Override
    public List<EnterpriseUserFaultMsgAlarm> newsNotice(Long userId, Integer projectId) {
        Integer total=5;
       return enterpriseUserFaultMsgAlarmMapper.newsNotice(userId,projectId,total);
    }

    @Override
    public List<Map<String, Integer>> warningNumber(List<PersonalSerialVo> personalSerialVos, Integer projectId, Integer type) {
        return enterpriseUserFaultMsgAlarmMapper.warningNumber(personalSerialVos, projectId, type);
    }
}
