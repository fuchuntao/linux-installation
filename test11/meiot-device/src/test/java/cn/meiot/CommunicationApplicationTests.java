package cn.meiot;


import cn.meiot.constart.ProjectConstart;
import cn.meiot.entity.Building;
import cn.meiot.entity.PowerAppUser;
import cn.meiot.entity.TimerMode;
import cn.meiot.entity.dto.sw.SendSwitch;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.BuildingService;
import cn.meiot.service.SwitchService;
import cn.meiot.service.TimerModeService;
import cn.meiot.service.UseTimeService;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommunicationApplicationTests {

	@Autowired
	private UseTimeService useTimeService;

	@Autowired
	private BuildingService buildingService;
	@Autowired
	private Environment environment;


	@Autowired
	private SwitchService switchService;

	private static  Integer i = 0;

	public static void main(String[] args){
		String rule = "{\"P\":{\"bigKey\":\"\",\"smalKey\":\"\"}}";
		Map<String,Map<String,String>> maps = (Map) JSON.parse(rule);
		System.out.println(maps);
		System.out.println(rule.substring(0,1));
	}



	@Test
	public void deleteBuilding(){
		Building building = new Building();
		building.setProjectId(24);
		building.setUserId(10000124L);
		building.setId(546L);
		buildingService.delete(building);
	}

	@Test
	public void sendSwitchLoadMax(){
		SendSwitch map = new SendSwitch();
		map.setSwitchIndex(ProjectConstart.SWITCH_INDEX_ALL);
		map.setSerialNumber("M22020032000111");
		map.setLoadMax(10000);
        map.setStatus(1);
		switchService.sendSwitchLoadmaxAll(map);
	}

	@Test
	public void sendSwitchOpen(){
		SendSwitch map = new SendSwitch();
		map.setSwitchSn(860116423L);
		map.setSerialNumber("P2202005180010");
		map.setStatus(0);
		switchService.sendSwitch(map);
	}


	@Autowired
	private TimerModeService timerModeService;
	@Test
	public void timeMode(){
		TimerMode timerMode = new TimerMode();
		timerMode.setIsSwitch(true);
		timerMode.setName("二代测试");
		timerMode.setType(3);
		Set<PowerAppUser> powerAppUserList = new HashSet<>();
		PowerAppUser p = new PowerAppUser();
		p.setSwitchSn("860116423");
		powerAppUserList.add(p);
		timerMode.setPowerAppUserList(powerAppUserList);
		timerMode.setSerialNumber("P2202005180010");
		timerMode.setTimes("[{\"end\":4726450763,\"mode\":2,\"num\":0,\"on\":\"11.16\",\"cycle\":[1,1,1,1,1,1,1],\"off\":\"11.17 \",\"start \":1590629509}]");
		timerModeService.insert(timerMode);
	}

    @Test
    public void sendSwitchLoadmaxAll(){
        SendSwitch map = new SendSwitch();
        map.setSwitchIndex(ProjectConstart.SWITCH_INDEX_ALL);
        map.setSerialNumber("M22020032000111");
        map.setLoadMax(10000);
        switchService.sendSwitchLoadmaxAll(map);
    }


	@Test
	public void switchStatus(){
		Result m2202003200002 = switchService.switchStatus("M2202003200002", 10000124L);
		System.out.println(m2202003200002);

	}

	@Test
	public void timemoder(){
		Result m2201912300004 = timerModeService.querySerial("M2201912300004", 10000985L);
		System.out.println(m2201912300004);
	}

	@Test
	public void timemoder2(){
		Set<PowerAppUser> invalidPowerAppUserList = new HashSet<>();
		PowerAppUser powerAppUser = new PowerAppUser();
		powerAppUser.setSwitchSn("1912300421");
		invalidPowerAppUserList.add(powerAppUser);
		timerModeService.updateInvalidSwitch(invalidPowerAppUserList, "M2201912300004",null);
	}



	@Test
	public void test(){
		System.out.println("12");
//		List<Long> ids = new ArrayList<>();
//		ids.add(762L);
//		ids.add(762L);
//		ids.add(588L);
//		ids.add(587L);
//		ids.add(439L);
//		ids.add(439L);
//		ids.add(409L);
//		ids.add(408L);
//		ids.add(401L);
//		ids.add(401L);
//		ids.add(396L);
//		ids.add(123L);
//		Result result = buildingService.querySerialByBuildingIds(ids);
//		System.out.println(result);
	}


}
	
