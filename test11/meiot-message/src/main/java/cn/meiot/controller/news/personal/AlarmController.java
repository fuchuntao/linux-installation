package cn.meiot.controller.news.personal;

import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.AppUserFaultMsgAlarm;
import cn.meiot.entity.UserAlarm;
import cn.meiot.entity.vo.*;
import cn.meiot.enums.AccountType;
import cn.meiot.enums.FaultTitleEnum;
import cn.meiot.exception.MyServiceException;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.feign.StatisticsFeign;
import cn.meiot.feign.UserFeign;
import cn.meiot.service.IAppUserFaultMsgAlarmService;
import cn.meiot.service.PersonalNoticeService;
import cn.meiot.service.UserAlarmService;
import cn.meiot.utils.ConstantUtil;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.RedisConstantUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Package cn.meiot.controller.news.personal
 * @Description: 新版个人app接口控制器 报警   2020/6/2企业版的也加在了这个接口里
 * @author: 武有
 * @date: 2020/2/13 15:15
 * @Copyright: www.spacecg.cn
 */
@RestController
@RequestMapping("personalAlarm")
@Slf4j
@SuppressWarnings("all")
public class AlarmController extends BaseController {

    @Autowired
    private UserFeign userFeign;
    @Autowired
    private FileConfigVo fileConfigVo;

    @Autowired
    private PersonalNoticeService personalNoticeService;

    @Autowired
    private IAppUserFaultMsgAlarmService appUserFaultMsgAlarmService;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private StatisticsFeign statisticsFeign;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserAlarmService userAlarmService;

    @Autowired
    private EnterpriseAppAlarmController enterpriseAppAlarmController;


    /**
     * 查询未读的预警数的总和
     *
     * @return
     */
    @GetMapping("findAlarmEarlyWarningTotal")
    public Result findAlarmEarlyWarningTotal() {
        Integer total = personalNoticeService.getUnreadNoticeTotal(getUserId());
        Result result = Result.getDefaultTrue();
        result.setData(total);
        return result;
    }

    /**
     * 个人app动态信息轮播
     *
     * @return
     */
    @GetMapping("getNewsNotice")
    public Result findNewsNotice() {
        List<AppUserFaultMsgAlarm> appUserFaultMsgAlarms = personalNoticeService.newsNotice(getUserId());
        Result re = Result.getDefaultTrue();
        re.setData(appUserFaultMsgAlarms);
        return re;
    }

    /**
     * 个人版app 预警报警列表查询 修改查询出来的改为已读状态
     */
    @GetMapping("getNoticeList")
    public Result getNoticeList(@RequestParam("currentPage") Integer currentPage,
                                @RequestParam("pageSize") Integer pageSize,
                                @RequestParam("type") Integer type) {
        Result result = Result.getDefaultFalse();
        if (null == type) {
            result.setMsg("类型不可为空");
            return result;
        }
        Long userId = getUserId();
        currentPage = (currentPage - 1) * pageSize;
        List<FaultMessageAndTypeVo> faultMessageAndTypeVoList = personalNoticeService.getFaultMsgByTypeAndUserId(type, userId, currentPage, pageSize);
        //准备要修改的id组 修改为已读
        List<Long> faultIds = new ArrayList<>();
        for (int i = 0; i < faultMessageAndTypeVoList.size(); i++) {
            faultIds.add(faultMessageAndTypeVoList.get(i).getId());
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            if (null == faultMessageAndTypeVoList.get(i).getFaultValue()) {
                continue;
            }
            Double d = Double.valueOf(faultMessageAndTypeVoList.get(i).getFaultValue());
            faultMessageAndTypeVoList.get(i).setFaultValue(decimalFormat.format(d));
            String key = ConstantUtil.ALARM_DELAUT_IMG + faultMessageAndTypeVoList.get(i).getTypeId();
            faultMessageAndTypeVoList.get(i).setFImg(fileConfigVo.getMPath(userFeign.getConfigValueByKey(key)));
        }
        Integer total = personalNoticeService.getFaultMsgByTypeAndUserIdTotal(type, userId, currentPage, pageSize);
        //将此类型下的所有未读标记为已读
//        AppUserFaultMsgAlarm faultMessage = AppUserFaultMsgAlarm.builder().isRead(1).build();
//        appUserFaultMsgAlarmService.update(faultMessage, new UpdateWrapper<AppUserFaultMsgAlarm>()
//                .eq("type", type).eq("user_id", userId).eq("is_read", 0));

        if (null != faultIds && faultIds.size() != 0) {
            UserAlarm userAlarm = new UserAlarm();
            userAlarm.setIsRead(1);
            userAlarmService.updateByFaultMessageId(faultIds, userId, userAlarm, type);
        }
        Map<String, Object> rt = new HashMap<>();
        rt.put("records", faultMessageAndTypeVoList);
        rt.put("total", total);
        result = Result.getDefaultTrue();
        result.setData(rt);
        log.info("查询故障消息列表{}", userId);
        return result;
    }

    /**
     * 判断 是否有未读的预警报警接口
     */
    @GetMapping("isRead")
    public Result isRead() {
        Long userId = getUserId();
        ReadNumber readNumber = userAlarmService.isRead(userId);
        return Result.OK(readNumber);
    }

    /**
     * 一键报修按钮
     */
    @PostMapping("clickRepair")
    public Result clickRepair(@RequestBody ClickRepairVo clickRepairVo) {
        boolean success = personalNoticeService.updateStatusAndNoteById(clickRepairVo, getUserId());
        return success ? Result.OK() : Result.faild(ErrorCodeUtil.UNKNOWN_MISTAKE);
    }


    /**
     * 获取当前用户的预警和报警率
     */
    @RequestMapping(value = "warningRate", method = RequestMethod.GET)
    @Log(operateContent = "获取当前用户的预警和报警率", operateModule = "消息服务")
    public Result warningRate() {

        Long userId = getUserId();
        //判断账号类型
        Integer type = getUserType(userId);
        //判断如果是企业执行一下控制器
        if (AccountType.ENTERPRISE.value().equals(type)) {
            return enterpriseAppAlarmController.warningRate();
        } else if (AccountType.PERSONAGE.value().equals(type)) {


            //如果是个人执行如下代码
            List<PersonalSerialVo> personalSerialVos = deviceFeign.querySerialAndMaster(getUserId());
            Result result = Result.getDefaultTrue();
            Map<String, Object> rt = new HashMap<>();
            //当前没有设备不往下走
            if (CollectionUtils.isEmpty(personalSerialVos)) {
                rt.put("alarm", 0);
                rt.put("warning", 0);
                rt.put("total", 0);
                rt.put("online", 0);
                result.setData(rt);
                return result;
            }
            List<Map<String, Integer>> maps = personalNoticeService.warningRate(personalSerialVos, getUserId());
            for (Map<String, Integer> map : maps) {
                if (map.get("type").equals(1)) {
                    rt.put("alarm", map.get("count"));
                } else {
                    rt.put("warning", map.get("count"));
                }
            }
            BigDecimal deviceLine = statisticsFeign.getDeviceLine(getUserId(), null);
            rt.put("online", deviceLine);
            rt.put("total", personalSerialVos.size());
            if (rt.get("warning") == null) {
                rt.put("warning", 0);
            }
            if (rt.get("alarm") == null) {
                rt.put("alarm", 0);
            }
            result.setData(rt);
            return result;
        } else {
            throw new MyServiceException("sys error");
        }
    }

    /**
     * 获取当前用户的预警和报警柱状图
     */
    @RequestMapping(value = "warningNumber", method = RequestMethod.GET)
    @Log(operateContent = "获取当前用户的预警和报警柱状图", operateModule = "消息服务")
    public Result warningNumber(Integer type) {
        Long userId=getUserId();
        //判断账号类型
        Integer accountType = getUserType(userId);
        if (AccountType.ENTERPRISE.value().equals(accountType)) {
            return enterpriseAppAlarmController.warningNumber(type);
        } else if (AccountType.PERSONAGE.value().equals(accountType)) {
            List<PersonalSerialVo> personalSerialVos = deviceFeign.querySerialAndMaster(getUserId());
            Result result = Result.getDefaultTrue();
            //当前没有设备不往下走
            if (CollectionUtils.isEmpty(personalSerialVos)) {
                return result;
            }
            List<Map<String, Integer>> maps = personalNoticeService.warningNumber(personalSerialVos, getUserId(), type);
            List<Map<String, Object>> data = new ArrayList<>();
            int number;
            if (type.equals(1)) {
                number = 1;
            } else {
                number = 10;
            }

            for (int i = number; i < number + 7; i++) {
                Map<String, Object> rt = new HashMap<>();
                String s = String.valueOf(i);
                Integer event = i;
                Map<String, Integer> map2 = maps.stream().filter(map -> map.get("event").equals(event)).findAny().orElse(null);
                String key = FaultTitleEnum.getTitle2(i);
                if (map2 == null) {
                    rt.put("name", key);
                    rt.put("count", 0);
                } else {
                    rt.put("name", key);
                    rt.put("count", map2.get("count"));
                }
                data.add(rt);
            }
            result.setData(data);
            return result;
        } else {
            throw new MyServiceException("sys error");
        }
    }


    /**
     * 统计所有类型的报警预警次数 饼状图
     */
    @GetMapping("getTotal")
    public Result getTotal(@RequestParam(value = "timestamp") Long timestamp,
                           @RequestParam(value = "index") Integer index) {

        Integer accountType = getUserType(getUserId());
        if (AccountType.ENTERPRISE.value().equals(accountType)) {
            return enterpriseAppAlarmController.getTotal(timestamp,index);
        } else if (AccountType.PERSONAGE.value().equals(accountType)) {
            Map<String, Object> map = getMap(timestamp, index);
            map.put("userId", getUserId());
            map.put("type", 1);
            List<StatisticsEventTimeVo> alarm = personalNoticeService.getTotal(map);
            map.put("type", 1);
            map.put("type", 2);
            List<StatisticsEventTimeVo> early = personalNoticeService.getTotal(map);
            Map<String, Object> rest = new HashMap<>();
            rest.put("alarm", alarm);
            rest.put("early", early);
            Result result = Result.getDefaultTrue();
            result.setData(rest);
            return result;
        } else {
            throw new MyServiceException("SYS ERROR");
        }
    }

    /**
     * 获取所有类型 详细内容 柱状图统计
     *
     * @return
     */
    @GetMapping("getTotalDetailed")
    public Result getTotalDetailed(@RequestParam(value = "timestamp") Long timestamp,
                                   @RequestParam(value = "index") Integer index) {
        Integer accountType = getUserType(getUserId());
        if (AccountType.ENTERPRISE.value().equals(accountType)) {
            return enterpriseAppAlarmController.getTotalDetailed(timestamp,index);
        } else if (AccountType.PERSONAGE.value().equals(accountType)) {
            Map<String, Object> map = getMap(timestamp, index);
            map.put("userId", getUserId());
            map.put("type", 1);
            map.put("index", index);
            List<StatisticsEventTimeVo> alarm = personalNoticeService.getTotalDetailed(map);
            map.put("type", 2);
            List<StatisticsEventTimeVo> early = personalNoticeService.getTotalDetailed(map);
            Map<String, Object> rest = new HashMap<>();
            rest.put("alarm", alarm);
            rest.put("early", early);
            Result result = Result.getDefaultTrue();
            result.setData(rest);
            return result;
        } else {
            throw new MyServiceException("sys error");
        }
    }


    /**
     * 获取故障排行前10 top10
     */
    @GetMapping("getTopTen")
    public Result getTopTen(@RequestParam(value = "timestamp") Long timestamp,
                            @RequestParam(value = "index") Integer index) {
//        Long userId=getUserId();
        Integer accountType = getUserType(getUserId());
        if (AccountType.ENTERPRISE.value().equals(accountType)) {
            Result topTen = enterpriseAppAlarmController.getTopTen(timestamp, index);
            log.info("controller控制器中topTen：{}",topTen);
            return topTen;
        } else if (AccountType.PERSONAGE.value().equals(accountType)) {
            Map<String, Object> map = getMap(timestamp, index);
            map.put("userId", getUserId());
            List<TopTenVo> topTenVos = personalNoticeService.getTopTen(map);
            for (int i = 0; i < topTenVos.size(); i++) {
                topTenVos.get(i).setName(JSON.parseObject((String) redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SERIALNUMBER, getUserId() + "_" + topTenVos.get(i).getSerNumber()), String.class));
            }
            Result result = Result.getDefaultTrue();
            result.setData(topTenVos);
            return result;
        } else {
            throw new MyServiceException("sys error");
        }
    }

    private Map<String, Object> getMap(Long t, Integer index) {
        Map<String, Object> map = new HashMap<>();
        SimpleDateFormat simpleDateFormat = null;
        if (null != t && null != index) {
            switch (index) {
                case 0: {
                    simpleDateFormat = new SimpleDateFormat("yyyy");
                    String currentFormat = simpleDateFormat.format(new Date(t));
                    String dateFormat = "%Y";
                    String groupFormat = "%m";
                    map.put("currentFormat", currentFormat);
                    map.put("dateFormat", dateFormat);
                    map.put("groupFormat", groupFormat);
                    return map;
                }
                case 1: {
                    simpleDateFormat = new SimpleDateFormat("yyyy-MM");
                    String currentFormat = simpleDateFormat.format(new Date(t));
                    String dateFormat = "%Y-%m";
                    String groupFormat = "%d";
                    map.put("currentFormat", currentFormat);
                    map.put("dateFormat", dateFormat);
                    map.put("groupFormat", groupFormat);
                    return map;
                }
                case 2: {
                    simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String currentFormat = simpleDateFormat.format(new Date(t));
                    String dateFormat = "%Y-%m-%d";
                    String groupFormat = "%H";
                    map.put("currentFormat", currentFormat);
                    map.put("dateFormat", dateFormat);
                    map.put("groupFormat", groupFormat);
                    return map;
                }
            }
        }
        return map;
    }

    /**
     * 添加备注
     *
     * @return
     */

    @PostMapping("addNote")
    public Result addNote(@RequestBody @Valid ClickRepairVo clickRepairVo) {
        boolean flag = personalNoticeService.updateNoteById(clickRepairVo, getUserId(), 0);
        return flag ? Result.OK() : Result.faild(ErrorCodeUtil.UNKNOWN_MISTAKE);
    }
}
