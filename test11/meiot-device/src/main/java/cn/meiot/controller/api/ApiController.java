package cn.meiot.controller.api;

import cn.meiot.dao.EquipmentUserMapper;
import cn.meiot.dao.SwitchMapper;
import cn.meiot.dao.UseTimeMapper;
import cn.meiot.dto.PasswordDto;
import cn.meiot.entity.db.Switch;
import cn.meiot.entity.db.UseTime;
import cn.meiot.entity.vo.PersonalSerialVo;
import cn.meiot.service.*;
import cn.meiot.utils.RedisConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api")
@Slf4j
public class ApiController {
	
	@Autowired
	private EquipmentUserService equipmentUserService;
	
	@Autowired
	private SwitchMapper switchMapper;
	
	@Autowired
	private EquipmentService equipmentService;
	
	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
    private UseTimeMapper useTimeMapper;

	@Autowired
    private EquipmentUserMapper equipmentUserMapper;

	@Autowired
	private HuaweiEquipmentService huaweiEquipmentService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private BuildingService buildingService;

	@Autowired
	private WaterMeterService waterMeterService;

	@Autowired
	private SwitchService switchService;
	
	@GetMapping(value = "querySerialVer")
    public List<Map> querySerialVer(Integer projectId,Long userId) {
		return equipmentUserService.querySerialVer(projectId,userId);
	}


	@GetMapping(value = "querySerialByProjectId")
	public List<String> querySerialByProjectId(Integer projectId) {
		return equipmentUserService.querySerialByProjectId(projectId);
	}

	/*@GetMapping("geihaowenceshi")
	public void sendSwitchProject() {
		String str = "haowenceshi";
		Integer status = (Integer)redisTemplate.opsForValue().get(str);
		if(status!=null && status.equals(1)){
			status = 0;
		}else {
			status = 1;
		}
		redisTemplate.opsForValue().set(str,status,1, TimeUnit.DAYS);
		PcEquipmentUserCond cond = new PcEquipmentUserCond();
		cond.setUserId(10000982L);
		cond.setProjectId(39);
		cond.setStatus(status);
		new Thread(()->switchService.sendSwitchProject(cond)).start();
	}*/

	@GetMapping(value = "queryMasterSn")
    public Long querySerialVer(String serialNumber) {
		Long sn = null;
		try {
			sn = (Long) redisTemplate.opsForHash().get(RedisConstantUtil.DEVICE_MASTER_SN, serialNumber);
		}catch (Exception e) {
			log.info("radis查找设备主开关失败:设备号{}",serialNumber);
		}
		if(sn == null) {
			Switch sw = new Switch();
			sw.setSerialNumber(serialNumber);
			sw.setParentIndex(0);
			Switch select = switchMapper.selectOne(sw);
			if(select == null) {
				return null;
			}
			Long valueOf = Long.valueOf(select.getSwitchSn());
			redisTemplate.opsForHash().put(RedisConstantUtil.DEVICE_MASTER_SN, serialNumber,valueOf);
			return valueOf;
		}else {
			return sn;
		}
	}

	//根据用户查询该用户的所有设备以及设备对应的主账号
	@GetMapping(value = "querySerialAndMaster")
    public List<PersonalSerialVo> querySerialAndMaster(Long userId) {
        return equipmentUserService.querySerialAndMaster(userId);
	}
	@GetMapping(value = "querySerialAndMasterByProjectId")
	public List<PersonalSerialVo> querySerialAndMasterByProjectId(@RequestParam("projectId") Integer projectId){
		return equipmentUserService.querySerialAndMasterByProjectId(projectId);
	}


    //获取用户的使用时间
    @GetMapping(value = "queryUseTime")
    public Map<String,Object> queryUseTime(Long userId,Integer projectId) {
        UseTime useTime = new UseTime(userId,projectId);
        Map map = useTimeMapper.queryUseTime(useTime);
        if(map == null){
            return null;
        }
        if(projectId != null && !projectId.equals(0)){
            Integer integer = equipmentUserService.queryDeviceTotal(projectId);
            map.put("total",integer);
        }else{
            List<String> strings = equipmentUserMapper.querySerialByUserId(userId);
            log.info("查询到的设备数量:{}",strings);
            if (CollectionUtils.isEmpty(strings)){
                map.put("total",0);
            }else{
                map.put("total",strings.size());
            }
        }
        return map;
    }

	/**
	 * 通过设备号查询密码
	 * @param serialNumber
	 * @return
	 */
	@RequestMapping( "passwordDto")
	public PasswordDto getPasswordDto(@RequestParam("serialNumber")String serialNumber ){
		PasswordDto passwordDto = huaweiEquipmentService.queryPasswordDtoBySerial(serialNumber);
		log.info("查询华为账号数据:{}",passwordDto);
		return passwordDto;
	}

	/**
	 * 通过设备号查询密码
	 * @param serialNumber
	 * @return
	 */
	@RequestMapping("querySerialNumber")
	public String querySerialNumber(@RequestParam("deviceId")String deviceId ){
		return  huaweiEquipmentService.querySerialNumber(deviceId);
	}

	/**
	 * 通过设备号查询密码
	 * @param serialNumber
	 * @return
	 */
	@RequestMapping( "serialCompany")
	public Integer serialCompany(@RequestParam("serialNumber")String serialNumber ){
		return equipmentService.serialCompany(serialNumber);
	}

	/**
	 *
	 * @param projectId
	 * @param listRole
	 * @return
	 */
	@GetMapping(value = "queryRoleEquipment")
	public List<String> queryRoleEquipment(@RequestParam("projectId") Integer projectId,@RequestParam("listRole") List<Integer> listRole){
		return roleService.queryEquipment(projectId,listRole);
	}

	@GetMapping("querySubBuilding")
	public List<Map> querySubBuildingAndMetersByBuildingId(@RequestParam("buildingId") Long id,@RequestParam("projectId")Integer projectId,@RequestParam("mainUserId") Long mainUserId){
		List<Map> listMap = buildingService.querSubName(id,projectId,mainUserId);
		waterMeterService.queryMetersByBuildingIds(listMap);
		return listMap;
	}

	@GetMapping("querySubBuildingWaterId")
	public List<String> querySubBuildingWaterId(@RequestParam("buildingId") Long id,@RequestParam("projectId")Integer projectId,@RequestParam("mainUserId") Long mainUserId){
		List<String> listMap = buildingService.querySubBuildingWaterId(id,projectId,mainUserId);
		return listMap;
	}
}
