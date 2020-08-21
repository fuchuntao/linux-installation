package cn.meiot.client;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.meiot.client.hystrix.CommunicationHystrix;
import cn.meiot.client.hystrix.UserHystrix;
import cn.meiot.entity.vo.Result;
/**
 * @author lingzhiying
 * @title: CommunicationClient.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月5日
 */
@FeignClient(value = "meiot-communication",fallback = CommunicationHystrix.class)
public interface CommunicationClient {
	/**
     * 通过设备号查询用户id
     * @param serialNumber
     * @return Result
     */
    @RequestMapping(value = "/communication/sendSwitch", method = RequestMethod.POST)
    Result sendSwitch(@RequestBody Map map);
}
