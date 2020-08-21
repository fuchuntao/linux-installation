package cn.meiot.utils;

import cn.meiot.config.HttpConstart;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.exception.MyServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
@Slf4j
public class NetworkingUtlis {

	@Autowired
	private HttpConstart httpConstart;

	@Autowired
	private RedisTemplate redisTemplate;


	private static  final int perCount = 50;


	private static final String URI = "/api/device/inOnlineBatch?device_id=";

	//只要不联网就抛异常
	public void isNetworkingThrowException(String serialNumber) {
		//return;
		// return 1;
		log.error("设备号"+serialNumber);
		Integer i = (Integer) redisTemplate.opsForValue().get(RedisConstantUtil.SERIAL_ONLINE+serialNumber);
		log.error("联网状态"+i);
		if(i != null){
			if(!i.equals(1)) {
				throw new MyServiceException(ResultCodeEnum.NETWORK_LONGTIME_ERROR.getCode(), ResultCodeEnum.NETWORK_LONGTIME_ERROR.getMsg());
			}else{
				return;
			}
		}
		try {
			RestTemplate restTemplate = new RestTemplate();
			String str = httpConstart.getHttp() + "/api/device/inOnline?device_id=" + serialNumber;
			log.info("请求地址为:{}", str);
			Map notice = restTemplate.getForObject(str, Map.class);
			Map map2 = (Map) notice.get("data");
			Integer online = (Integer) map2.get("isOnline");
			if(!online.equals(1)){
				throw new MyServiceException(ResultCodeEnum.NETWORK_LONGTIME_ERROR.getCode(),ResultCodeEnum.NETWORK_LONGTIME_ERROR.getMsg());
			}
		} catch (Exception ex) {
			throw new MyServiceException(ResultCodeEnum.NETWORK_ERROR.getCode(),ResultCodeEnum.NETWORK_ERROR.getMsg());
		}
	}

	public Integer getNetworkingStatus(String url,String serialNumber) {
		 //return 1;
		Integer i = (Integer) redisTemplate.opsForValue().get(RedisConstantUtil.SERIAL_ONLINE+serialNumber);
		if(i != null){
			return i;
		}
		try {
			RestTemplate restTemplate = new RestTemplate();
			url = httpConstart.getHttp();
			String str = url + "/api/device/inOnline?device_id=" + serialNumber;
			log.info("请求地址为:{}", str);
			Map notice = restTemplate.getForObject(str, Map.class);
			Map map2 = (Map) notice.get("data");
			Integer online = (Integer) map2.get("isOnline");
			return online;
		} catch (Exception ex) {
			log.info("联网状态异常:{}", ex);
			return 1;
		}
	}

	public Integer getNetworkingStatus(String url,Object serialNumber) {
		Integer i = (Integer) redisTemplate.opsForValue().get(RedisConstantUtil.SERIAL_ONLINE+serialNumber);
		if(i != null){
			return i;
		}
		try {
			RestTemplate restTemplate = new RestTemplate();
			url = httpConstart.getHttp();
			String str = url + "/api/device/inOnline?device_id=" + serialNumber;
			log.info("请求地址为:{}", str);
			Map notice = restTemplate.getForObject(str, Map.class);
			Map map2 = (Map) notice.get("data");
			Integer online = (Integer) map2.get("isOnline");
			return online;
		} catch (Exception ex) {
			log.info("联网状态异常:{}", ex);
			return 1;
		}
	}

	public static void main(String[] args) {

		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 101; i++){
			list.add("AD-"+(i+1));
		}
		Integer result = new NetworkingUtlis().deviceOnLineNum(list);
		System.out.println("结果："+result);
	}

	/**
	 * 通过一个设备号集合分段为多个设备号集合
	 * @param list
	 * @return List<List<String>>
	 */
	private static List<List<String>>  limitDevice(List<String> list){
		int index = 0;
		log.info("设备划分开始==============>");
		List<List<String>> result = new ArrayList<List<String>>();
		while ( index < list.size()){
			List<String> tmp = new ArrayList<String>();
			int lenth = list.size();
			if(lenth - index < perCount){
				tmp.addAll(list.subList(index,lenth));
			}else{
				tmp.addAll(list.subList(index,(index+perCount)));
			}
			index = index+perCount;

			result.add(tmp);
		}
		log.info("设备划分结束==============>：{}",result);
		return result;

	}


	/**
	 * 计算设备的在线数量
	 * @param list
	 * @return
	 */
	public  Integer  deviceOnLineNum(List<String> list){
		try {
			if(list == null ){
				log.info("查询的设备为空！！！！！");
				return 0;
			}
			int sum = 0;
			log.info("需要校验在线的设备：{}",list);
			RestTemplate restTemplate = new RestTemplate();
			if(CollectionUtils.isEmpty(list)){
				return 0;
			}
			for (int i = list.size() -1 ; i >= 0 ; i--) {
				String serialNumber = list.get(i);
				Integer online = (Integer) redisTemplate.opsForValue().get(RedisConstantUtil.SERIAL_ONLINE+serialNumber);
				if(online != null){
					if(online.equals(1)){
						sum +=1;
					}
					list.remove(i);
				}
			}
			List<List<String>> limitDevice = limitDevice(list);
			if(CollectionUtils.isEmpty(list)){
				return sum;
			}
			for (List device : limitDevice){
				//按规定将设备号封装成指定格式
				String serialNumbers = StringUtils.join(device, "|");
				log.info("请求的参数：{}",serialNumbers);
				String url = httpConstart.getHttp() + URI + serialNumbers;
				log.info("发送的url：{}",url);
				Map map = restTemplate.getForObject(url, Map.class);
				Map data = (Map) map.get("data");
				if(null == data || data.size() == 0){
					log.info("返回数据为空！！！！！！！！！");
					continue;
				}
				for(Object o : data.values()){
					Integer isOnline = (Integer) ((LinkedHashMap) o).get("isOnline");
					if(1 == isOnline){
						sum +=1;
					}
				}
			}
			return sum;
		}catch (Exception e){
			log.info("查询在线率发生错误：{},{}",e.getMessage(),e.getStackTrace());
			return 0;
		}

	}


	/**
	 * 查询在线设备
	 * @param list
	 * @return
	 */
	public List<String> deviceOnLineNumList(List<String> list2){

		List<String> stringList = new ArrayList<>();
		List<String> list = new ArrayList<>(list2);
		if(CollectionUtils.isEmpty(list)){
			log.info("查询的设备为空！！！！！");
			return null;
		}
		for (int i = list.size() -1 ; i >= 0 ; i--) {
			String serialNumber = list.get(i);
			Integer online = (Integer) redisTemplate.opsForValue().get(RedisConstantUtil.SERIAL_ONLINE+serialNumber);
			if(online != null){
				if(online.equals(1)){
					stringList.add(serialNumber);
				}
				list.remove(i);
			}
		}
		if(CollectionUtils.isEmpty(list)){
			log.info("查询的设备为空！！！！！");
			return stringList;
		}
		RestTemplate restTemplate = new RestTemplate();
		List<List<String>> limitDevice = limitDevice(list);
		if(!CollectionUtils.isEmpty(limitDevice)) {
			for (List device : limitDevice){
				//按规定将设备号封装成指定格式
				String serialNumbers = StringUtils.join(device, "|");
				String url = httpConstart.getHttp() + URI + serialNumbers;
				Map<String, Object> map = restTemplate.getForObject(url, Map.class);
				Map<String, Object> data = (Map<String, Object>) map.get("data");
				if(null == data || data.size() == 0){
					log.info("返回数据为空！！！！！！！！！");
					continue;
				}

				for (Map.Entry<String, Object> vo: data.entrySet()) {
					String key = vo.getKey();
					boolean flage = false;
					Collection<Integer> values = ((LinkedHashMap) vo.getValue()).values();
					for(Integer online : values){
						if(online == 0){
							flage = true;
						}
					}
					if(flage) {
						stringList.add(key);
					}
				}
			}
		}
		return stringList;
	}
}
