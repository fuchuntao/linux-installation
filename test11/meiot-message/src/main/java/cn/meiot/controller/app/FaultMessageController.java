package cn.meiot.controller.app;


import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.FaultMessage;
import cn.meiot.entity.vo.FaultMessageAndTypeVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.IFaultMessageService;
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
 * 故障消息 前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-12
 */
@RestController
@RequestMapping("/app/fault-msg")
@Slf4j
public class FaultMessageController extends BaseController {

    @Autowired
    private IFaultMessageService faultMessageService;

    public FaultMessageController(IFaultMessageService faultMessageService){
        this.faultMessageService = faultMessageService;
    }

    /**
     * 系统消息列表
     * @param currentPage  当前页
     * @param pageSize  每页展示多少行
     * @param type 故障类型
     * @return
     */
    @RequestMapping(value = "list",method = RequestMethod.GET)
    @Log(operateContent = "查询故障消息列表",operateModule = "消息服务")
    public Result list(@RequestParam("currentPage") Integer currentPage, @RequestParam("pageSize") Integer pageSize,
                       @RequestParam("type") Integer type){
        Result result = Result.getDefaultFalse();
        if(null == type){
            result.setMsg("类型不可为空");
            return result;
        }
        //获取指定类型下的所有消息列表
        Page<FaultMessage> page = new Page<FaultMessage>(currentPage,pageSize);
        Long userId = getUserId();
        currentPage= (currentPage-1)*pageSize;
        //TODO
//        IPage<FaultMessage> faultMessageIPage = faultMessageService.page(page,new QueryWrapper<FaultMessage>()
//                .eq("switch_event",type).eq("user_id",userId).orderByDesc("create_time"));
       List<FaultMessageAndTypeVo> faultMessageAndTypeVoList =  faultMessageService.getFaultMsgByTypeAndUserId(type,userId,currentPage,pageSize);
        Integer total = faultMessageService.getFaultMsgByTypeAndUserIdTotal(type, userId, currentPage, pageSize);
        //将此类型下的所有未读标记为已读
        FaultMessage faultMessage = FaultMessage.builder().isRead(1).build();
        faultMessageService.update(faultMessage,new UpdateWrapper<FaultMessage>()
                .eq("switch_event",type).eq("user_id",userId).eq("is_read",0));
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
        return faultMessageService.statisticsWarn(getUserId(),serialNumber);
    }



    /**
     * 获取报警的总记录数以及是否包含唯独消息
     * @return
     */
    @RequestMapping(value = "reportTotal",method = RequestMethod.GET)
    @Log(operateContent = "获取报警的总记录数以及是否包含唯独消息",operateModule = "消息服务")
    public Result reportTotal(){
        return faultMessageService.getReportTotal(getUserId());
    }


    /**
     * 获取当前用户的未读消息
     * @return
     */
    @RequestMapping(value = "unread",method = RequestMethod.GET)
    @Log(operateContent = "获取当前用户的未读消息",operateModule = "消息服务")
    public Result unread(){
        return faultMessageService.unread(getUserId());
    }

}
