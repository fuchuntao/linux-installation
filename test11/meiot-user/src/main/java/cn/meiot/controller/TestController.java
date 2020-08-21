package cn.meiot.controller;

import cn.meiot.entity.vo.EmailVo;
import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.feign.TestFeign;
import cn.meiot.utils.QueueConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

@RestController
@RefreshScope
@Slf4j
public class TestController {

    @Autowired
    private TestFeign testFeign;

    public String value;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public ImgConfigVo imgConfigVo;



    @RequestMapping(value = "nofilter/hello",method = RequestMethod.GET)
    public String hello(HttpServletRequest request){
        //获取请求头
        Enumeration enumeration=request.getHeaderNames();
        log.info("请求头：");
        while(enumeration.hasMoreElements()) {
            String name=(String)enumeration.nextElement();
            String value=request.getHeader(name);
            log.info(name+"： "+value);
        }


        return "SUCCESS";
    }

    @RequestMapping(value = "test",method = RequestMethod.GET)
    public String callRe(){
        Result<EmailVo> test = testFeign.test();
        System.out.println(test);
        return "TEST!!!!";

    }

    @RequestMapping(value = "upload",method = RequestMethod.POST)
    public String upload(HttpServletRequest request ,@RequestParam("file") MultipartFile srcFile) throws IOException {
        System.out.println(request);
        return "showImage";
    }
}
