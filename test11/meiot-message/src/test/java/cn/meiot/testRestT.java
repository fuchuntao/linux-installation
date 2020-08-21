package cn.meiot;

import cn.meiot.entity.vo.WXMessageVo;
import cn.meiot.enums.FaultTitleEnum;
import cn.meiot.feign.UserFeign;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

/**
 * @Package cn.meiot
 * @Description:
 * @author: 武有
 * @date: 2020/1/14 15:49
 * @Copyright: www.spacecg.cn
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@SuppressWarnings("all")
//@ActiveProfiles("test")
public class testRestT {

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private RestTemplate restTemplate;
    @Test
    public void test() {
//        RestTemplate restTemplate=new RestTemplate();
        WXMessageVo wxMessageVo=new WXMessageVo();
        WXMessageVo.Entry first=wxMessageVo.new Entry("您的设备发生"+ "<a color=#E47834>高温报警</a>" + ",请尽快处理。检修设备时注意安全，谨防触电。\n","#363636");
        WXMessageVo.Entry keyword1=wxMessageVo.new Entry("测试设备","#363636");
        WXMessageVo.Entry keyword2=wxMessageVo.new Entry("空调","#363636");
        WXMessageVo.Entry keyword3=wxMessageVo.new Entry("300A","#363636");
        WXMessageVo.Entry keyword4=wxMessageVo.new Entry("15:13:29","#363636");
        WXMessageVo.Entry remark=null;
        WXMessageVo.WXData wxData=wxMessageVo.new WXData(first,keyword1,keyword2,keyword3,keyword4,remark);
        wxMessageVo.setData(wxData);
        wxMessageVo.setTemplate_id("alKeNG6Oaw6tFafxrg9Swz9KQ7cTrMEqZUtIb1F4cMc");
        wxMessageVo.setTouser("o6z9m54ZWPGB2Ll3NEfO7J-y1t-Q");
        String accessToken = "29_RD5m7P0BORWmm4UP1z5h15JCJ8aMGy6KgntN04pBsoTVTX32FCUvBZ8coBgyAMAM0p8WYeKhJ_SSjm0ymYM_GcXyyJuSnW7NyEMGZQoZtPPiQ8T5M6e6Oke1GYpuIfWLv1s6g9WRP_yC7y1-JCMaAEAOHR";
        if (StringUtils.isEmpty(accessToken)) {
            log.info("accessToken获取失败");
            return;
        }
        String openid = wxMessageVo.getTouser();
        if (StringUtils.isEmpty(openid)) {
            log.info("openid获取失败");
            return;
        }
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;

        String jsonString = JSONObject.toJSONString(wxMessageVo);
        System.out.println(jsonString);



        ResponseEntity<String> entity = restTemplate.postForEntity(url, jsonString, String.class);
        String body = entity.getBody();
        log.info("获取ticket结果：{}", body);
    }
}
