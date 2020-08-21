package cn.meiot.service.impl;

import cn.meiot.entity.AppUserFaultMsgAlarm;
import cn.meiot.entity.FaultType;
import cn.meiot.entity.TroubleTicketVo;
import cn.meiot.entity.UserAlarm;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.vo.*;
import cn.meiot.enums.FaultTitleEnum;
import cn.meiot.exception.MyServiceException;
import cn.meiot.mapper.AppUserFaultMsgAlarmMapper;
import cn.meiot.mapper.FaultTypeMapper;
import cn.meiot.mapper.UserAlarmDao;
import cn.meiot.service.PersonalNoticeService;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utils.UserInfoUtil;
import cn.meiot.utils.enums.AlarmStatusEnum;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Package cn.meiot.service.impl
 * @Description: 新版个人app的业务层实现类
 * @author: 武有
 * @date: 2020/2/13 15:24
 * @Copyright: www.spacecg.cn
 */
@Service
public class PersonalNoticeServiceImpl implements PersonalNoticeService {

    @Autowired
    private AppUserFaultMsgAlarmMapper appUserFaultMsgAlarmMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private FaultTypeMapper faultTypeMapper;

    @Autowired
    private UserAlarmDao userAlarmDao;

    @Autowired
    private UserInfoUtil userInfoUtil;

    @Override
    public Integer getUnreadNoticeTotal(Long userId) {
        return appUserFaultMsgAlarmMapper.getUnreadNoticeTotal(userId);
    }

    @Override
    public List<AppUserFaultMsgAlarm> newsNotice(Long userId) {
        return appUserFaultMsgAlarmMapper.getNewsNotice(userId, 5);
    }

    @Override
    public List<Map<String, Integer>> warningRate(List<PersonalSerialVo> personalSerialVos, Long userId) {
        //当前时间-30天
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -30);
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now.getTime());
        return appUserFaultMsgAlarmMapper.warningSum(personalSerialVos, userId, startTime);
    }

    @Override
    public List<Map<String, Integer>> warningNumber(List<PersonalSerialVo> personalSerialVos, Long userId, Integer type) {
        return appUserFaultMsgAlarmMapper.warningNumber(personalSerialVos, userId, type);
    }

    @Override
    public List<StatisticsEventTimeVo> getTotal(Map<String, Object> map) {
        List<StatisticsEventTimeVo> statisticsEventTimeVos = appUserFaultMsgAlarmMapper.getTotal(map);
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
        List<StatisticsEventTimeVo> statisticsEventTimeVos = appUserFaultMsgAlarmMapper.getTotalDetailed(map);
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

    private void sub(List<StatisticsEventTimeVo> resut, String s, Integer i) {


        String time = resut.get(i).getTime();

        String name = "";
        if (time.toCharArray()[0] == '0') {
            name = time.substring(1) + s;
            resut.get(i).setTime(name);
        } else {
            name = time + s;
            resut.get(i).setTime(name);
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

    @Override
    public List<FaultMessageAndTypeVo> getFaultMsgByTypeAndUserId(Integer type, Long userId, Integer currentPage, Integer pageSize) {

        return appUserFaultMsgAlarmMapper.getFaultMsgByTypeAndUserId(type, userId, currentPage, pageSize);
    }

    @Override
    public Integer getFaultMsgByTypeAndUserIdTotal(Integer type, Long userId, Integer currentPage, Integer pageSize) {
        return appUserFaultMsgAlarmMapper.getFaultMsgByTypeAndUserIdTotal(type, userId, currentPage, pageSize);
    }

    @Override
    public List<TopTenVo> getTopTen(Map map) {
        return appUserFaultMsgAlarmMapper.selectTopTen(map);
    }

    /**
     * 根据ID修改备注和状态
     *
     * @param clickRepairVo
     * @return
     */
    @Override
    public boolean updateStatusAndNoteById(ClickRepairVo clickRepairVo,Long userId) {
        //先查询出改记录的status是否为一键报修状态 如果不是 提示该故障已经被提交
        AppUserFaultMsgAlarm msgAlarm = appUserFaultMsgAlarmMapper.selectById(clickRepairVo.getId());
        //提示记录不存在
        if (null == msgAlarm) {
            throw new MyServiceException(ErrorCodeUtil.RECORD_DOES_NOT_EXIST);
        }
        //提示已经被提交
        if (!msgAlarm.getStatus().equals(AlarmStatusEnum.YIJIANBAOXIU.value())) {
            throw new MyServiceException(ErrorCodeUtil.HAS_BEEN_SUBMITTED);
        }
        //修改记录
        AppUserFaultMsgAlarm appUserFaultMsgAlarm = AppUserFaultMsgAlarm
                .builder().build()
                .setId(clickRepairVo.getId())
                .setNote(clickRepairVo.getNote())
                .setStatus(AlarmStatusEnum.DAISHOULI.value());
        int i = appUserFaultMsgAlarmMapper.updateById(appUserFaultMsgAlarm);

        FaultType faultType = faultTypeMapper.selectById(msgAlarm.getEvent());

       List<UserAlarm> userAlarmList= userAlarmDao.selectByAlarmId(msgAlarm.getId());
        // 生成故障工单
        TroubleTicketVo troubleTicketVo = new TroubleTicketVo();
       List<TroubleTicketVo.TroubleTicketVoUserAlarm> userAlarms= new ArrayList<>();
        for (UserAlarm userAlarm : userAlarmList) {
            TroubleTicketVo.TroubleTicketVoUserAlarm troubleTicketVoUserAlarm=troubleTicketVo.new TroubleTicketVoUserAlarm();
            BeanUtils.copyProperties(userAlarm,troubleTicketVoUserAlarm);
            userAlarms.add(troubleTicketVoUserAlarm);
        }
        troubleTicketVo.setUserAlarms(userAlarms);
        AuthUserBo authUserBo = userInfoUtil.getAuthUserBo(userId);
        String tel = authUserBo.getUser().getUserName();
        troubleTicketVo.setAlarmTime(msgAlarm.getFaultTime());
        troubleTicketVo.setTel(tel);
        troubleTicketVo.setAlarmType(msgAlarm.getEvent());
        troubleTicketVo.setAlarmTypeName(FaultTitleEnum.getTitle(msgAlarm.getEvent()));
        troubleTicketVo.setDeviceId(msgAlarm.getSerialNumber());
        troubleTicketVo.setIsShow(0);
        troubleTicketVo.setUserId(userId);
        troubleTicketVo.setDeviceName(msgAlarm.getEquipmentAlias());
        troubleTicketVo.setType(TroubleStatus.BAOXIU.value());
        troubleTicketVo.setNote(clickRepairVo.getNote());
        troubleTicketVo.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        troubleTicketVo.setSn(msgAlarm.getSwitchSn());
        troubleTicketVo.setSnName(msgAlarm.getSwitchAlias());
        troubleTicketVo.setIsApp(0);
        troubleTicketVo.setFAlias(faultType.getFAlias());
        troubleTicketVo.setFAymbol(faultType.getFAymbol());
        troubleTicketVo.setAlarmId(msgAlarm.getId());
        troubleTicketVo.setAlarmValue(msgAlarm.getFaultValue());
        rabbitTemplate.convertAndSend(QueueConstantUtil.TROUBLE_TICKET, JSONObject.toJSONString(troubleTicketVo));
        return i > 0;
    }

    @Override
    public boolean updateNoteById(ClickRepairVo clickRepairVo, Long userId, Integer projectId) {
        AppUserFaultMsgAlarm alarm=AppUserFaultMsgAlarm.builder().id(clickRepairVo.getId()).note(clickRepairVo.getNote()).build();
        int i = appUserFaultMsgAlarmMapper.updateById(alarm);
        return i>0;
    }
}
