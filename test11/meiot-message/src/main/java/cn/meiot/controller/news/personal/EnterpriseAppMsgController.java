package cn.meiot.controller.news.personal;

import cn.meiot.controller.BaseController;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.IEnterpriseUserFaultMsgAlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @Package cn.meiot.controller.news.personal
 * @Description:企业系统消息控制器
 * @author: 武有
 * @date: 2020/4/2 10:00
 * @Copyright: www.spacecg.cn
 */

@RestController
@RequestMapping("EnterpriseMsg")
public class EnterpriseAppMsgController extends BaseController {


    @Autowired
    private IEnterpriseUserFaultMsgAlarmService enterpriseUserFaultMsgAlarmService;

    /**
     * 根据用户ID查询系统未读消息总数
     */
    @GetMapping("getUnreadTotal")
    public Result getUnreadTotal() {
        Integer total = enterpriseUserFaultMsgAlarmService.getUnreadTotal(getUserId());
        return Result.OK(total);
    }


}
