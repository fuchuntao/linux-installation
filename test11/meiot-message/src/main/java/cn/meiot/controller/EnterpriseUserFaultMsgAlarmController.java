package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.entity.Bulletin;
import cn.meiot.entity.EnterpriseUserFaultMsgAlarm;
import cn.meiot.entity.FaultMessage;
import cn.meiot.entity.vo.ClickRepairVo;
import cn.meiot.entity.vo.FaultMessageAndTypeVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.StatisticsEventTimeVo;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.feign.UserFeign;
import cn.meiot.service.IEnterpriseUserFaultMsgAlarmService;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.MsgUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wuyou
 * @since 2019-10-22
 */
@RestController
@RequestMapping("/enterprise-user-fault-msg-alarm")
@SuppressWarnings("all")
@Slf4j
public class EnterpriseUserFaultMsgAlarmController extends BaseController {
    @Autowired
    private IEnterpriseUserFaultMsgAlarmService enterpriseUserFaultMsgAlarmService;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private DeviceFeign deviceFeign;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    @Log(operateContent = "查询故障消息列表", operateModule = "消息服务")
    public Result list(@RequestParam("currentPage") Integer currentPage, @RequestParam("pageSize") Integer pageSize,
                       @RequestParam("type") Integer type) {
        Result result = Result.getDefaultFalse();
        if (null == type) {
            result.setMsg("类型不可为空");
            return result;
        }
        Long userId =getMainUserId();
        Integer projectId = getProjectId();
        currentPage = (currentPage - 1) * pageSize;
        List<FaultMessageAndTypeVo> faultMessageAndTypeVoList = enterpriseUserFaultMsgAlarmService.getFaultMsgByTypeAndUserId(type, userId, currentPage, pageSize, projectId);
        Integer total = enterpriseUserFaultMsgAlarmService.getFaultMsgByTypeAndUserIdTotal(type, userId, currentPage, pageSize, projectId);
        //将此类型下的所有未读标记为已读
        EnterpriseUserFaultMsgAlarm faultMessage = EnterpriseUserFaultMsgAlarm.builder().isRead(1).build();
        enterpriseUserFaultMsgAlarmService.update(faultMessage, new UpdateWrapper<EnterpriseUserFaultMsgAlarm>()
                .eq("event", type).eq("user_id", userId).eq("is_read", 0));
        Map<String, Object> rt = new HashMap<>();
        rt.put("records", faultMessageAndTypeVoList);
        rt.put("total", total);
        result = Result.getDefaultTrue();
        result.setData(rt);
        log.info("查询故障消息列表{}", userId);
        return result;
    }

    /**
     * 根据设备统计报警信息
     *
     * @param serialNumber
     * @return
     */
    @RequestMapping(value = "statisticsWarn", method = RequestMethod.GET)
    @Log(operateContent = "根据设备统计报警信息", operateModule = "消息服务")
    public Result statisticsWarn(@RequestParam("serialNumber") String serialNumber) {
        return enterpriseUserFaultMsgAlarmService.statisticsWarn(getMainUserId(), serialNumber, getProjectId());
    }


    /**
     * 获取报警的总记录数以及是否包含唯独消息
     *
     * @return
     */
    @RequestMapping(value = "reportTotal", method = RequestMethod.GET)
    @Log(operateContent = "获取报警的总记录数以及是否包含唯独消息", operateModule = "消息服务")
    public Result reportTotal() {
        return enterpriseUserFaultMsgAlarmService.getReportTotal(getUserId(), getProjectId());
    }


    /**
     * 获取当前用户的未读消息
     *
     * @return
     */
    @RequestMapping(value = "unread", method = RequestMethod.GET)
    @Log(operateContent = "获取当前用户的未读消息", operateModule = "消息服务")
    public Result unread() {
        return enterpriseUserFaultMsgAlarmService.unread(getUserId(), getProjectId());
    }


    @GetMapping("getFaultMessage")
    @Log(operateContent = "获取故障列表", operateModule = "消息服务")
    public Result getFaultMessage(
            @RequestParam(value = "current", required = true) Integer current,
            @RequestParam(value = "pageSize", required = true) Integer pageSize,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "faultType", required = false) Integer faultType,
            @RequestParam(value = "distributionBoxName", required = false) String distributionBoxName,
            @RequestParam(value = "serialNumber", required = false) String serialNumber) {
        Map<String, Object> map = new HashMap<>();
        map.put("projectId", getProjectId());
        map.put("current", (current - 1) * pageSize);
        map.put("pageSize", pageSize);
        map.put("status", status);
        map.put("faultType", faultType);
        map.put("distributionBoxName", distributionBoxName);
        map.put("userId", getMainUserId());
        map.put("serialNumber",serialNumber);

        return enterpriseUserFaultMsgAlarmService.getFaultMessageList(map);
    }
    @GetMapping("getStatisticsFaultMessage")
    @Log(operateContent = "获取故障列表", operateModule = "消息服务")
    public Result getStatisticsFaultMessage(
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam("timestamp") Long timestamp,
            @RequestParam("index") Integer index,
            @RequestParam(value = "serialNumber",required = false) String serialNumber,
            @RequestParam(value = "current") Integer current,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam(value = "switchSn", required = false) String switchNumber) {

        Long mainUserId = getMainUserId();
        Map<String, Object> map = getMap(timestamp, index);
        map.put("projectId", getProjectId());
        map.put("current", (current - 1) * pageSize);
        map.put("pageSize", pageSize);
        map.put("type", type);
        map.put("userId", mainUserId);
        map.put("serialNumber",serialNumber);
        map.put("switchNumber",switchNumber);
        return enterpriseUserFaultMsgAlarmService.getStatisticsFaultMessage(map);
    }

    @GetMapping("getStatisticsFaultMessageAll")
    @Log(operateContent = "获取故障列表", operateModule = "消息服务")
    public Result getStatisticsFaultMessageAll(
            @RequestParam("timestamp") Long timestamp,
            @RequestParam("index") Integer index,
            @RequestParam(value = "serialNumber",required = false) String serialNumber,
            @RequestParam(value = "current") Integer current,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam(value = "switchSn", required = false) String switchNumber) {

        Long mainUserId = getMainUserId();
        Map<String, Object> map = getMap(timestamp, index);
        map.put("projectId", getProjectId());
        map.put("current", (current - 1) * pageSize);
        map.put("pageSize", pageSize);
        map.put("userId", mainUserId);
        map.put("serialNumber",serialNumber);
        map.put("switchNumber",switchNumber);
        return enterpriseUserFaultMsgAlarmService.getStatisticsFaultMessage(map);
    }
    @PostMapping("updateStatus")
    @Log(operateContent = "更新故障状态", operateModule = "消息服务")
    public Result updateStatus(@RequestBody EnterpriseUserFaultMsgAlarm faultMessage) {
        Result result = Result.getDefaultFalse();
        if (null == faultMessage.getId()) {
            return result;
        }
        if (null == faultMessage.getSwitchStatus()) {
            return result;
        }
//        EnterpriseUserFaultMsgAlarm bean = MsgUtil.getBean(faultMessage, EnterpriseUserFaultMsgAlarm.class);
        if (!enterpriseUserFaultMsgAlarmService.updateById(faultMessage)) {
            return result;
        }
        result = Result.getDefaultTrue();
        return result;
    }

    @GetMapping("statisticalAlarm")
    @Log(operateContent = "获取故障统计", operateModule = "消息服务")
    public Result statisticalAlarm(@RequestParam(value = "type", required = false) Integer type,
                                   @RequestParam("timestamp") Long timestamp,
                                   @RequestParam("index") Integer index,
                                   @RequestParam(value = "switchSn", required = false) String switchNumber,
                                   @RequestParam(value = "serialNumber",required = false) String serialNumber) {
        Long mainUserId = getMainUserId();
        log.info("获取故障统计接口当前用户ID:{},主账户ID：{}",getUserId(),mainUserId);
        Map<String, Object> map = getMap(timestamp, index);
        map.put("mainUserId", mainUserId);
        map.put("type", type);
        map.put("index", index);
        map.put("projectId", getProjectId());
        map.put("switchNumber", switchNumber);
        map.put("serialNumber",serialNumber);
        return enterpriseUserFaultMsgAlarmService.getStatisticalAlarm(map);
    }

    @GetMapping("statisticalAlarmAll")
    @Log(operateContent = "获取所有故障类型的统计", operateModule = "消息服务")
    public Result statisticalAlarm(@RequestParam(value = "timestamp", required = false) Long timestamp,
                                   @RequestParam(value = "index", required = false) Integer index,
                                   @RequestParam(value = "switchNumber", required = false) String switchNumber,
                                   @RequestParam(value = "serialNumber",required = false) String serialNumber) {
        Long mainUserId =getMainUserId();
        Map<String, Object> map = getMap(timestamp, index);
        map.put("userId", mainUserId);
        map.put("index", index);
        map.put("switchNumber", switchNumber);
        map.put("projectId", getProjectId());
        map.put("serialNumber",serialNumber);
        return enterpriseUserFaultMsgAlarmService.getStatisticalAlarmAll(map);
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
     * 查询故障总数
     */

    @GetMapping("getBigDataFaultTotal")
    @Log(operateContent = "查询故障总数", operateModule = "消息服务")
    public Result getFaultTotal() {
        Map<String, Object> map = new HashMap<>();
        Long userId =getMainUserId();
        Integer projectId = getProjectId();
        map.put("userId", userId);
        map.put("projectId", projectId);
        Integer faultNumber = enterpriseUserFaultMsgAlarmService.getStatisticalAlarmTotal(map);
//        Integer faultNumber=152;
        Result result = Result.getDefaultTrue();
        result.setData(faultNumber);
        return result;
    }


    /**
     * 获取故障设备的总数
     *
     * @return
     */
    @GetMapping("getaFaultDeviceNumber")
    @Log(operateContent = "查询故障设备总数", operateModule = "消息服务")
    public Result getFaultDeviceNumber() {
        Map<String, Object> map = new HashMap<>();
        Long userId = getMainUserId();
        Integer projectId = getProjectId();
        map.put("userId", userId);
        map.put("projectId", projectId);
        /**
         *故障设备总数
         */
        Integer faultNumber = enterpriseUserFaultMsgAlarmService.getFaultNumber(map);
        /**
         * 设备总数
         */

        Integer number = deviceFeign.queryDeviceTotal(getProjectId());
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("faultNumber", faultNumber);
        resMap.put("number", number);
        Result result = Result.getDefaultTrue();
        result.setData(resMap);
        return result;
    }

    /**
     * 查询大屏幕的故障列表统计
     *
     * @return
     */
    @GetMapping("getBigDataStatisticalAlarm")
    @Log(operateContent = "查询故障列表统计", operateModule = "消息服务")
    public Result getBigDataStatisticalAlarm() {
        Map<String, Object> map = new HashMap<>();
        Long userId =getMainUserId();
        Integer projectId = getProjectId();
        map.put("userId", userId);
        map.put("projectId", projectId);
        Result statisticalAlarmAll = enterpriseUserFaultMsgAlarmService.getStatisticalAlarmAll(map);
        return statisticalAlarmAll;

    }

    /**
     * 根据不同故障状态查询不同状态的记录总条数
     *
     * @return
     */
    @GetMapping("getBigDataStatus")
    public Result getBigDataStatus() {
        Map<String, Object> map = new HashMap<>();
        Long userId = getMainUserId();
        Integer projectId = getProjectId();
        map.put("userId", userId);
        map.put("projectId", projectId);
        map.put("switchStatus", 1);
        Integer total1 = enterpriseUserFaultMsgAlarmService.getStatisticalAlarmTotal(map);
        log.info("待处理{}", total1);

        /**
         * 处理中
         */
        map.put("switchStatus", 2);
        Integer total2 = enterpriseUserFaultMsgAlarmService.getStatisticalAlarmTotal(map);
        log.info("处理中{}", total2);
        /**
         * 已处理
         */
        map.put("switchStatus", 3);
        Integer total3 = enterpriseUserFaultMsgAlarmService.getStatisticalAlarmTotal(map);
        log.info("已处理{}", total3);

        Map<String, Integer> restMap = new HashMap<>();
        restMap.put("total1", total3);
        restMap.put("total2", total2);
        restMap.put("total3", total1);
        Result result = Result.getDefaultTrue();
        result.setData(restMap);
        return result;
    }

    /**
     * 获取故障设备的总数
     *
     * @return
     */
    @GetMapping("getBigDataFaultDeviceNumber")
    @Log(operateContent = "查询故障设备总数", operateModule = "消息服务")
    public Result getBigDataFaultDeviceNumber() {
        Map<String, Object> map = new HashMap<>();
        Long userId = getMainUserId();
        Integer projectId = getProjectId();
        map.put("userId", userId);
        map.put("projectId", projectId);
        /**
         *故障设备总数
         */
        Integer faultNumber = enterpriseUserFaultMsgAlarmService.getFaultNumber(map);
        Result result = Result.getDefaultTrue();
        result.setData(faultNumber);
//        result.setData(20);

        return result;
    }

    @GetMapping("getBigDataFaultList")
    @Log(operateContent = "查询故障设备列表", operateModule = "消息服务")
    public Result getBigDataFaultList() {
        Map<String, Object> map = new HashMap<>();
        Long userId = getMainUserId();
        Integer projectId = getProjectId();
        //TODO
        map.put("userId", userId);
        map.put("projectId", projectId);
        map.put("current", 1);
        map.put("pageSize", 10);
        Result faultMessageList = enterpriseUserFaultMsgAlarmService.getFaultMessageList(map);
        return faultMessageList;
    }

    @GetMapping("getTotalByFaultType")
    @Log(operateContent = "根据故障类型查询故障数", operateModule = "消息服务")
    public Result getTotalByFaultType(@RequestParam("type")Integer type){
        Integer total = enterpriseUserFaultMsgAlarmService.getTotalByFaultType(type,getMainUserId());
        Result result = Result.getDefaultTrue();
        result.setData(total);
        return result;
    }

    /**
     * 一键报修
     * @return
     */

    @PostMapping("clickRepair")
    public Result clickRepair(@RequestBody ClickRepairVo clickRepairVo){
       boolean flag= enterpriseUserFaultMsgAlarmService.updateStatusAndNoteById(clickRepairVo,getUserId(),getProjectId());
       return flag?Result.OK():Result.faild(ErrorCodeUtil.UNKNOWN_MISTAKE);
    }

    /**
     * 添加备注
     * @return
     */

    @PostMapping("addNote")
    public Result addNote(@RequestBody @Valid ClickRepairVo clickRepairVo){
        boolean flag= enterpriseUserFaultMsgAlarmService.updateNoteById(clickRepairVo,getUserId(),getProjectId());
        return flag?Result.OK():Result.faild(ErrorCodeUtil.UNKNOWN_MISTAKE);
    }

    public Long getMainUserId() {
        Long userId = userFeign.getMainUserIdByUserId(getUserId());
        return userId;
    }
}
