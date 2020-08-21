package cn.meiot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HealthController {


    /**
     * 健康检查
     */
    @RequestMapping(value = "/health")
    public String health(){

        return "it's OK!";
    }



    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String test(@RequestParam("name") String name){
        log.info("天雷滚滚");
        return " I'm  "+name;
    }
}
