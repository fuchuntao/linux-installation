package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.entity.AppUserFaultMsgAlarm;
import cn.meiot.entity.FaultMessage;
import cn.meiot.entity.vo.FaultMessageAndTypeVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.IAppUserFaultMsgAlarmService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wuyou
 * @since 2019-10-22
 */
@RestController
@RequestMapping("/app-user-fault-msg-alarm")
@SuppressWarnings("all")
@Slf4j
public class AppUserFaultMsgAlarmController extends BaseController{

    @Autowired
    private IAppUserFaultMsgAlarmService appUserFaultMsgAlarmService;
    @RequestMapping(value = "list",method = RequestMethod.GET)
    @Log(operateContent = "查询故障消息列表",operateModule = "消息服务")
    public Result list(@RequestParam("currentPage") Integer currentPage, @RequestParam("pageSize") Integer pageSize,
                       @RequestParam("type") Integer type){
        Result result = Result.getDefaultFalse();
        if(null == type){
            result.setMsg("类型不可为空");
            return result;
        }
        Long userId = getUserId();
        currentPage= (currentPage-1)*pageSize;
        List<FaultMessageAndTypeVo> faultMessageAndTypeVoList =  appUserFaultMsgAlarmService.getFaultMsgByTypeAndUserId(type,userId,currentPage,pageSize);
        Integer total = appUserFaultMsgAlarmService.getFaultMsgByTypeAndUserIdTotal(type, userId, currentPage, pageSize);
        //将此类型下的所有未读标记为已读
        AppUserFaultMsgAlarm faultMessage = AppUserFaultMsgAlarm.builder().isRead(1).build();
        appUserFaultMsgAlarmService.update(faultMessage,new UpdateWrapper<AppUserFaultMsgAlarm>()
                .eq("event",type).eq("user_id",userId).eq("is_read",0));
        Map<String,Object> rt=new HashMap<>();
        rt.put("records",faultMessageAndTypeVoList);
        rt.put("total",total);
        result = Result.getDefaultTrue();
        result.setData(rt);
        log.info("查询故障消息列表{}",userId);
        return result;
    }

    /**
     * 根据设备统计报警信息
     * @param serialNumber
     * @return
     */
    @RequestMapping(value = "statisticsWarn",method = RequestMethod.GET)
    @Log(operateContent = "根据设备统计报警信息",operateModule = "消息服务")
    public Result statisticsWarn(@RequestParam("serialNumber") String serialNumber){
        return appUserFaultMsgAlarmService.statisticsWarn(getUserId(),serialNumber);
    }



    /**
     * 获取报警的总记录数以及是否包含唯独消息
     * @return
     */
    @RequestMapping(value = "reportTotal",method = RequestMethod.GET)
    @Log(operateContent = "获取报警的总记录数以及是否包含唯独消息",operateModule = "消息服务")
    public Result reportTotal(){
        return appUserFaultMsgAlarmService.getReportTotal(getUserId());
    }


    /**
     * 获取当前用户的未读消息
     * @return
     */
    @RequestMapping(value = "unread",method = RequestMethod.GET)
    @Log(operateContent = " ",operateModule = "消息服务")
    public Result unread(){
        return appUserFaultMsgAlarmService.unread(getUserId());
    }

}
