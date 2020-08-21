package cn.meiot.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.meiot.client.hystrix.CommunicationHystrix;
import cn.meiot.client.hystrix.MessageHystrix;

import java.util.List;
import java.util.Map;

@FeignClient(value = "meiot-message",fallback =MessageHystrix.class)
public interface MessageCilent {
	@RequestMapping(value="api/deleteMsg",method = RequestMethod.GET)
    public void deleteMsg(@RequestParam(value = "userId",required = false) Long userId,
                          @RequestParam("serialNumber") String serialNumber);
	
	@RequestMapping(value="api/faultNumber",method = RequestMethod.GET)
    public int faultNumber( @RequestParam("serialNumber") String serialNumber,@RequestParam("userId") Long userId);

    @RequestMapping(value="api/queryFaultSwitch",method = RequestMethod.GET)
    public Map queryFaultSwitch(@RequestParam("serialNumber") String serialNumber,
                                @RequestParam("switchSn") String switchSn,
                                @RequestParam("projectId") Integer projectId);

    @RequestMapping(value="api/queryFaultSerial",method = RequestMethod.GET)
    public List<String> queryFaultSerial(@RequestParam("serialNumberList[]") List<String> serialNumberList,
                                         @RequestParam("projectId") Integer projectId,
                                         @RequestParam("type") Integer type);
}
