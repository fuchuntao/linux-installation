package cn.meiot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Package cn.meiot.controller
 * @Description:
 * @author: 武有
 * @date: 2019/11/19 11:40
 * @Copyright: www.spacecg.cn
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public String health(){
        return "ok";
    }
}
