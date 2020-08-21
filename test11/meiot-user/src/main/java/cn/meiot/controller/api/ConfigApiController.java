package cn.meiot.controller.api;


import cn.meiot.service.IConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api")
public class ConfigApiController {

    private IConfigService configService;

    ConfigApiController(IConfigService configService){

        this.configService = configService;
    }

    /**
     * 更具key获取配置信息value
     * @param cKey
     * @return
     */
    @RequestMapping(value = "/getConfigValueByKey",method = RequestMethod.GET)
    public String getConfigValueByKey(@RequestParam(value = "key") String cKey){

        return configService.getConfigValueByKey(cKey);
    }
}
