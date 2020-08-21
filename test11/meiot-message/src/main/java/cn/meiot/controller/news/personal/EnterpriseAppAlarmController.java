package cn.meiot.controller.news.personal;

import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.EnterpriseAlarm;
import cn.meiot.entity.EnterpriseUserFaultMsgAlarm;
import cn.meiot.entity.vo.*;
import cn.meiot.enums.FaultTitleEnum;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.feign.StatisticsFeign;
import cn.meiot.feign.UserFeign;
import cn.meiot.service.EnterpriseAlarmService;
import cn.meiot.service.IEnterpriseUserFaultMsgAlarmService;
import cn.meiot.utils.ConstantUtil;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.RedisConstantUtil;
import cn.meiot.utils.enums.TypeEnum;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Package cn.meiot.controller.news.personal
 * @Description:企业报警控制器
 * @author: 武有
 * @date: 2020/4/2 9:22
 * @Copyright: www.spacecg.cn
 */
@RestController
@SuppressWarnings("all")
@Slf4j
@RequestMapping("EnterpriseAlarm")
public class EnterpriseAppAlarmController extends BaseController {


    @Autowired
    private FileConfigVo fileConfigVo;

    @Autowired
    private UserFeign userFeign;


    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private IEnterpriseUserFaultMsgAlarmService enterpriseUserFaultMsgAlarmService;

    @Autowired
    private StatisticsFeign statisticsFeign;

    @Autowired
    private EnterpriseAlarmService alarmService;


    /**
     * 查询未读的预警数的总和
     *
     * @return
     */
    @GetMapping("findAlarmEarlyWarningTotal")
    @Log(operateContent = "查询未读的预警数的总和",operateModule = "消息服务")
    public Result findAlarmEarlyWarningTotal() {
        Integer total = enterpriseUserFaultMsgAlarmService.getUnreadNoticeTotal(getUserId(),getProjectId(), TypeEnum.EARLYWARNING.value());
        return Result.OK(total);
    }



    /**
     *  获取故障排行前10 top10
     */
    @GetMapping("getTopTen")
    @Log(operateContent = "获取故障排行前10",operateModule = "消息服务")
    public Result getTopTen(@RequestParam(value = "timestamp") Long timestamp,
                            @RequestParam(value = "index") Integer index) {
//        Long userId=getUserId();
        Long userId = userFeign.getMainUserIdByUserId(getUserId());
        Map<String, Object> map = getMap(timestamp, index);
        map.put("userId",userId);
        map.put("projectId",getProjectId());
        List<TopTenVo> topTenVos= enterpriseUserFaultMsgAlarmService.getTopTen(map);
        for (int i = 0; i < topTenVos.size(); i++) {
            String jsonString= (String) redisTemplate.opsForHash().get(RedisConstantUtil.NIKNAME_SERIALNUMBER,userId+"_"+topTenVos.get(i).getSerNumber());
            topTenVos.get(i).setName(jsonString);
        }
        Result result = Result.getDefaultTrue();
        result.setData(topTenVos);
        return  result;
    }


    /**
     * 统计所有类型的报警预警次数 饼状图
     */
    @GetMapping("getTotal")
    @Log(operateContent = "统计所有类型的报警预警次数 饼状图",operateModule = "消息服务")
    public Result getTotal(@RequestParam(value = "timestamp") Long timestamp,
                           @RequestParam(value = "index") Integer index){
        Map<String,Object> map=getMap(timestamp,index);
        Long userId = userFeign.getMainUserIdByUserId(getUserId());
        map.put("userId",userId);
        map.put("projectId",getProjectId());
        map.put("type",TypeEnum.ALARM.value());
        List<StatisticsEventTimeVo> alarm  = enterpriseUserFaultMsgAlarmService.getTotal(map);
        map.put("type",TypeEnum.EARLYWARNING.value());
        List<StatisticsEventTimeVo> early = enterpriseUserFaultMsgAlarmService.getTotal(map);
        Map<String,Object> rest=new HashMap<>();
        rest.put("alarm",alarm);
        rest.put("early",early);
        Result result = Result.getDefaultTrue();
        result.setData(rest);
        return result;
    }

    /**
     * 获取所有类型 详细内容 柱状图统计
     * @return
     */
    @GetMapping("getTotalDetailed")
    @Log(operateContent = "获取所有类型 详细内容 柱状图统计",operateModule = "消息服务")
    public Result getTotalDetailed(@RequestParam(value = "timestamp") Long timestamp,
                                   @RequestParam(value = "index") Integer index){
        Map<String, Object> map = getMap(timestamp, index);
        Long userId = userFeign.getMainUserIdByUserId(getUserId());

        map.put("userId",userId);
        map.put("projectId",getProjectId());
        map.put("index",index);
        map.put("type",TypeEnum.ALARM.value());
        List<StatisticsEventTimeVo> alarm = enterpriseUserFaultMsgAlarmService.getTotalDetailed(map);
        map.put("type",TypeEnum.EARLYWARNING.value());
        List<StatisticsEventTimeVo> early = enterpriseUserFaultMsgAlarmService.getTotalDetailed(map);
        Map<String,Object> rest=new HashMap<>();
        rest.put("alarm",alarm);
        rest.put("early",early);
        Result result = Result.getDefaultTrue();
        result.setData(rest);
        return result;
    }


    /**
     * 企业版app 预警报警列表查询 修改查询出来的改为已读状态
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
        Integer project=getProjectId();
        currentPage = (currentPage - 1) * pageSize;
        List<FaultMessageAndTypeVo> faultMessageAndTypeVoList =enterpriseUserFaultMsgAlarmService.getFaultByUserId(type, userId, currentPage, pageSize,project);
        //准备要修改的id组 修改为已读
        List<Long> faultIds=new ArrayList<>();
        for (int i = 0; i < faultMessageAndTypeVoList.size(); i++) {
            faultIds.add(faultMessageAndTypeVoList.get(i).getId());
            DecimalFormat decimalFormat=new DecimalFormat("0.00");
            if (null == faultMessageAndTypeVoList.get(i).getFaultValue()) {
                continue;
            }
            Double d=Double.valueOf(faultMessageAndTypeVoList.get(i).getFaultValue());
            faultMessageAndTypeVoList.get(i).setFaultValue(decimalFormat.format(d));
            String key= ConstantUtil.ALARM_DELAUT_IMG +faultMessageAndTypeVoList.get(i).getTypeId();
            faultMessageAndTypeVoList.get(i).setFImg(fileConfigVo.getMPath(userFeign.getConfigValueByKey(key)));
        }
        Integer total = enterpriseUserFaultMsgAlarmService.getFaultByUserIdTotal(type, userId, currentPage, pageSize,project);
        //将此类型下的所有未读标记为已读
//        EnterpriseUserFaultMsgAlarm faultMessage = EnterpriseUserFaultMsgAlarm.builder().isRead(1).build();
//        enterpriseUserFaultMsgAlarmService.update(faultMessage, new UpdateWrapper<EnterpriseUserFaultMsgAlarm>()
//                .eq("type", type).eq("user_id", userId).eq("is_read", 0));
        if (null != faultIds && faultIds.size() != 0) {
            EnterpriseAlarm enterpriseAlarm=EnterpriseAlarm.builder().isRead(1).build();
            alarmService.updateByFaultMessageId(faultIds,userId,enterpriseAlarm,type);
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
     * 获取当前用户的预警和报警率
     */
    @RequestMapping(value = "warningRate",method = RequestMethod.GET)
    @Log(operateContent = "获取当前用户的预警和报警率",operateModule = "消息服务")
    public Result warningRate(){
        List<PersonalSerialVo> personalSerialVos = deviceFeign.querySerialAndMasterByProjectId(getProjectId());
        Long userId = userFeign.getMainUserIdByUserId(getUserId());
        Result result = Result.getDefaultTrue();
        Map<String, Object> rt = new HashMap<>();
        //当前没有设备不往下走
        if(CollectionUtils.isEmpty(personalSerialVos)){
            rt.put("alarm",0);
            rt.put("warning",0);
            rt.put("total",0);
            rt.put("online",0);
            result.setData(rt);
            return result;
        }
        List<Map<String, Integer>> maps = enterpriseUserFaultMsgAlarmService.warningRate(personalSerialVos, getProjectId());
        for (Map<String, Integer> map: maps) {
            if(map.get("type").equals(1)){
                rt.put("alarm",map.get("count"));
            }else{
                rt.put("warning",map.get("count"));
            }
        }
        BigDecimal deviceLine = statisticsFeign.getDeviceLine(userId, getProjectId());
        rt.put("online",deviceLine);
        rt.put("total",personalSerialVos.size());
        if(rt.get("warning") ==null){
            rt.put("warning",0);
        }
        if(rt.get("alarm") ==null){
            rt.put("alarm",0);
        }
        result.setData(rt);
        return result;
    }

    /**
     * 获取当前用户的预警和报警柱状图
     */
    @RequestMapping(value = "warningNumber",method = RequestMethod.GET)
    @Log(operateContent = "获取当前用户的预警和报警柱状图",operateModule = "消息服务")
    public Result warningNumber(Integer type){
        log.info("获取当前用户的预警和报警柱状图");
//        List<PersonalSerialVo> personalSerialVos = deviceFeign.querySerialAndMaster(getUserId());
//        log.info("获取到的设备列表：{}",null);
        Result result = Result.getDefaultTrue();
        List<Map<String, Integer>> maps = enterpriseUserFaultMsgAlarmService.warningNumber(null, getProjectId(), type);
        List<Map<String, Object>> data = new ArrayList<>();
        int number;
        if(type.equals(1)){
            number = 1;
        }else{
            number = 10;
        }

        for (int i = number; i < number + 7 ; i++) {
            Map<String, Object> rt = new HashMap<>();
            String s = String.valueOf(i);
            Integer event = i;
            Map<String, Integer> map2 = maps.stream().filter(map -> map.get("event").equals(event)).findAny().orElse(null);
            String key = FaultTitleEnum.getTitle2(i);
            if(map2 == null){
                rt.put("name",key);
                rt.put("count",0);
            }else{
                rt.put("name",key);
                rt.put("count",map2.get("count"));
            }
            data.add(rt);
        }
        result.setData(data);
        return result;
    }






    private Map<String, Object> getMap(Long t, Integer index) {
        Map<String, Object> map = new HashMap<>();
        SimpleDateFormat simpleDateFormat = null;
        if (null!=t && null!=index){
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
     * @return
     */

    @PostMapping("addNote")
    public Result addNote(@RequestBody @Valid ClickRepairVo clickRepairVo){
        boolean flag= enterpriseUserFaultMsgAlarmService.updateNoteById(clickRepairVo,getUserId(),0);
        return flag?Result.OK():Result.faild(ErrorCodeUtil.UNKNOWN_MISTAKE);
    }


    /**
     * 判断 是否有未读的预警报警接口
     */
    @GetMapping("isRead")
    public Result isRead(){
        Long userId=getUserId();
        ReadNumber readNumber = alarmService.isRead(userId,getProjectId());
        return Result.OK(readNumber);
    }

}
