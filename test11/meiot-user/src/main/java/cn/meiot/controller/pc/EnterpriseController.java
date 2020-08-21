package cn.meiot.controller.pc;


import cn.meiot.aop.Log;
import cn.meiot.entity.bo.EnterpriseBo;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.pc.IEnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 企业信息 前端控制器（弃用）
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-18
 */
@RestController
@RequestMapping("/enterprise")
public class EnterpriseController {
    @Autowired
    private IEnterpriseService enterpriseService;

    /**
     * 获取企业列表
     * @return
     */
    @GetMapping(value = "/list")
    @Log(operateContent = "查看公司列表",operateModule = "用户中心")
    public Result getList(){
        Result result = Result.getDefaultTrue();
        List<EnterpriseBo> list = enterpriseService.getList();
        result.setData(list);
        return result;
    }

}
