package cn.meiot.controller.api;

import cn.meiot.service.IAppUserFaultMsgAlarmService;
import cn.meiot.service.IEnterpriseUserFaultMsgAlarmService;
import cn.meiot.service.IFaultMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Package cn.meiot.controller.api
 * @Description:
 * @author: 武有
 * @date: 2019/9/29 17:29
 * @Copyright: www.spacecg.cn
 */
@RestController
@RequestMapping("api")
public class ApiController {

    @Autowired
    private IFaultMessageService iFaultMessageService;
    @Autowired
    private IEnterpriseUserFaultMsgAlarmService enterpriseUserFaultMsgAlarmService;
    @Autowired
    private IAppUserFaultMsgAlarmService iAppUserFaultMsgAlarmService;
    @RequestMapping("deleteMsg")
    public void deleteMsg(@RequestParam(value = "userId",required = false) Long userId,
                          @RequestParam("serialNumber") String serialNumber){
        iAppUserFaultMsgAlarmService.deleteMsgByUserIdAndSerialNumber(userId,serialNumber);
    }

    @RequestMapping(value="faultNumber",method = RequestMethod.GET)
    public int faultNumber( @RequestParam("serialNumber") String serialNumber,
                            @RequestParam("userId") Long userId){
        return enterpriseUserFaultMsgAlarmService.getUnprocessed(serialNumber,userId);
    }

}
