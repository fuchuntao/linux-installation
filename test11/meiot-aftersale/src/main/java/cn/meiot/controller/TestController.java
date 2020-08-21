package cn.meiot.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Map;

@RestController
@Slf4j
public class TestController {

    @Value("${img.path}")
    private String value;

    @GetMapping(value = "nofilter/test")
    public String  test(){
        return "ok";
    }

    @PostMapping("nofilter/test02")
    public String test02(@RequestBody Map map){
        log.info("==============================>>:{}",map);
        System.out.println(JSONObject.toJSONString(map));
        return "ok";
    }

}
