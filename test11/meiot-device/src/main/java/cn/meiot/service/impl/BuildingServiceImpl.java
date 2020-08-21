package cn.meiot.service.impl;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cn.meiot.dao.*;
import cn.meiot.entity.dto.pc.examination.ExaminationBuildingDto;
import cn.meiot.entity.dto.pc.examination.SerialDto;
import cn.meiot.entity.dto.pc.examination.SwitchDto;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.service.RoleService;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import cn.meiot.client.MessageCilent;
import cn.meiot.constart.RedisConstart;
import cn.meiot.entity.db.Building;
import cn.meiot.entity.db.Equipment;
import cn.meiot.entity.db.Switch;
import cn.meiot.entity.dto.pc.BuildingEquipment;
import cn.meiot.entity.dto.pc.BuildingRecursionDto;
import cn.meiot.entity.dto.pc.BuildingRespDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.exception.MyServiceException;
import cn.meiot.service.BuildingService;
import cn.meiot.utils.NetworkingUtlis;
import cn.meiot.utils.RedisConstantUtil;
import cn.meiot.utlis.BuildingUtlis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class BuildingServiceImpl implements BuildingService {
	
	@Autowired
	private BuildingMapper buildingMapper;
	
	@Autowired
	private SwitchMapper switchMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private RoleMapper roleMapper;
	
	@Autowired
	private NetworkingUtlis networkingUtlis;
	
	@Autowired
	private MessageCilent messageCilent;
	
	@Autowired
	private EquipmentMapper equipmentMapper;

	@Autowired
	private RoleService roleService;

	@Autowired
	private WaterUserMapper waterUserMapper;

	@Autowired
    private EquipmentUserMapper equipmentUserMapper;

	@Autowired
	private PcTimerModeMapper pcTimerModeMapper;

	@Override
	public Result queryBuilding(BuildingRespDto buildingRespDto) {
		List<BuildingRecursionDto> listData = queryRecursion(buildingRespDto);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(listData);
		return defaultTrue;
	}

	@Override
	@Transactional
	public Result insert(Building building) {
		Long parentId = building.getParentId();
		if(parentId != null && !parentId.equals(0L)){
			Building parentBuilding = buildingMapper.selectByPrimaryKey(parentId);
			int level = parentBuilding.getLevel()+1;
			building.setLevel(level);
			if(!parentBuilding.getProjectId().equals(building.getProjectId())){
				throw new MyServiceException(ResultCodeEnum.NO_AUTHENTICATION.getCode(),ResultCodeEnum.NO_AUTHENTICATION.getMsg());
			}
		}
		int row = buildingMapper.insertSelective(building);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(building.getId());
		return defaultTrue;
	}

	@Override
	@Transactional
	public Result delete(Building building) {
		Long id = building.getId();
		//优化掉递归删除
		//deleteDg(id);
        //递归查询数据
        Integer projectId = building.getProjectId();
        Long userId = building.getUserId();
		List<Building> buildings = getBuildings(projectId, userId);
		List<Long> ids = new ArrayList<>();
		ids.add(id);
		querySonId(buildings,id,ids);
		//查询是否绑定设备
        String serialNumber = equipmentMapper.findOneSerialByBuildingIds(ids);
        if(!StringUtils.isEmpty(serialNumber)){
			return Result.faild(ResultCodeEnum.ALREADY_SERIAL_BIND.getCode(), ResultCodeEnum.ALREADY_SERIAL_BIND.getMsg());
		}
        //水表
		String waterId = waterUserMapper.findOneIdByBuildingIds(ids);
        if(!StringUtils.isEmpty(waterId)){
			return Result.faild(ResultCodeEnum.WATER_SERIAL_BIND.getCode(), ResultCodeEnum.WATER_SERIAL_BIND.getMsg());
		}
        //角色
        Integer roleId = roleMapper.findOneIdByBuildingIds(ids);
        if(roleId != null){
			return Result.faild(ResultCodeEnum.ROLE_SERIAL_BIND.getCode(), ResultCodeEnum.ROLE_SERIAL_BIND.getMsg());
		}
        buildingMapper.deleteIds(ids);
		return Result.OK();
	}
	
	//递归删除
	public void deleteDg(Long id) {
		List<BuildingRecursionDto> listData = buildingMapper.queryBuildingDg(id);
		buildingMapper.deleteByPrimaryKey(id);
		List<Switch> listSwitch = switchMapper.querySwitchByBuildingId(id);
		if(!CollectionUtils.isEmpty(listSwitch)) {
			throw new MyServiceException(ResultCodeEnum.ALREADY_SERIAL_BIND.getCode(), ResultCodeEnum.ALREADY_SERIAL_BIND.getMsg());
		}
		if(listData.size() != 0) {
			for (BuildingRecursionDto buildingRecursionDto : listData) {
				if(buildingRecursionDto.getListBuildingRecursionDto().size() == 0) {
					List<Switch> listSwitch2 = switchMapper.querySwitchByBuildingId(buildingRecursionDto.getId());
					if(!CollectionUtils.isEmpty(listSwitch2)) {
						throw new MyServiceException(ResultCodeEnum.ALREADY_SERIAL_BIND.getCode(), ResultCodeEnum.ALREADY_SERIAL_BIND.getMsg());
					}
					buildingMapper.deleteByPrimaryKey(buildingRecursionDto.getId());
				}else {
					deleteDg(buildingRecursionDto.getId());
				}
			}
		}
	}

	@Override
	@Transactional
	public Result update(Building building) {
		int row = buildingMapper.updateByPrimaryKeySelective(building);
		if(row != 1) {
			throw new MyServiceException(ResultCodeEnum.UPDATE_BUDLING_ERROR.getCode(), ResultCodeEnum.UPDATE_BUDLING_ERROR.getMsg());
		}
		return Result.getDefaultTrue();
	}

	@Override
	public Result queryEquipment(BuildingRespDto buildingRespDto) {
		int unNetworkingNum = 0;
		int faultNum = 0;
		Map map = new HashMap();
		List<String> noOnline = null;
		List<BuildingRecursionDto> queryRecursion = queryRecursion(buildingRespDto);
		for (int i = queryRecursion.size()-1; i >= 0; i--) {
			BuildingRecursionDto x = queryRecursion.get(i);
			List<BuildingRecursionDto> listData = x.getListBuildingRecursionDto();
			//如果只有一层则删除当前层
		    if(CollectionUtils.isEmpty(listData)){
		    	queryRecursion.remove(i);
		    	continue;
		    }
		    for (int y = listData.size()-1; y >= 0; y--) {
		    	BuildingRecursionDto subdata = listData.get(y);
		    	List<BuildingRecursionDto> listSubData = subdata.getListBuildingRecursionDto();
		    	//有三级删除
		    	if(!CollectionUtils.isEmpty(listSubData)){
		    		subdata.setListBuildingRecursionDto(null);
			    }
		    	Long buildingId = subdata.getId();
				BuildingEquipment buildingEquipment = new BuildingEquipment();
				Equipment eq = new Equipment();
				eq.setBuildingId(buildingId);
				List<Equipment> select = equipmentMapper.select(eq);
				List<Switch> listSwitch = switchMapper.querySwitchByBuildingId(buildingId);
				if(CollectionUtils.isEmpty(select)) {
					continue;
				}
				Integer networkingStatus = 1;
				//未联网设备
				noOnline = new ArrayList<>();

				for (Equipment equipment : select) {
					String serialNumber = equipment.getSerialNumber();
					//long http = System.currentTimeMillis();
					Integer networkingStatus2 = networkingUtlis.getNetworkingStatus("",serialNumber);
					//httpTime.add(System.currentTimeMillis() - http);
					if(networkingStatus == 1) {
						networkingStatus = networkingStatus2;
					}
					//未联网时自增
					if(networkingStatus2.equals(0)) {
						noOnline.add(serialNumber);
						unNetworkingNum = unNetworkingNum + 1;
					}
					//long message = System.currentTimeMillis();
					int faultNumber = messageCilent.faultNumber(serialNumber,buildingRespDto.getUserId());
					//messageTime.add(System.currentTimeMillis()-message);
					if(faultNumber > 0) {
						buildingEquipment.setFaultStatus(0);
						faultNum = faultNum + faultNumber;
					}
				}
				buildingEquipment.setNetworkingStatus(networkingStatus);
				//开启状态
				Integer openCount = 0;
				for (Switch sw : listSwitch) {
					if(noOnline.contains(sw.getSerialNumber())){
						continue;
					}
					String str1 = RedisConstart.DEVICE+sw.getSerialNumber();
					//long redis = System.currentTimeMillis();
					Object status = redisTemplate.opsForHash().get(str1,sw.getSwitchSn() );
					//redisTime.add(System.currentTimeMillis()-redis);
					if(status == null) {
						continue;
					}
					Map status3 = (Map)status;
					String status2 = status3.get("switch").toString();
					Integer valueOf = Integer.valueOf(status2);
					if(valueOf.equals(1)) {
						openCount++;
					}
				}
				//打开数量等于开关数量  则全部开启
				if(listSwitch.size() == 0){
					buildingEquipment.setOpenStatus(2);
				}else if(openCount == listSwitch.size()) {
					buildingEquipment.setOpenStatus(1);
				}else if(openCount == 0) {
					buildingEquipment.setOpenStatus(0);
				}else {
					buildingEquipment.setOpenStatus(2);
				}
				subdata.setBuildingEquipment(buildingEquipment);
		    }
		  //只有一级，删除
		    if(CollectionUtils.isEmpty(listData)){
		    	queryRecursion.remove(i);
		    	continue;
		    }
		}
		map.put("list", queryRecursion);
		map.put("unNetworkingNum", unNetworkingNum);
		map.put("faultNum", faultNum);
		Result defaultTrue = Result.getDefaultTrue();
		defaultTrue.setData(map);
		return defaultTrue;
	}

	@Override
	public List<BuildingRecursionDto> queryRecursion(BuildingRespDto buildingRespDto) {
		List<Building> listBuildingALL = getBuildings(buildingRespDto.getProjectId(),buildingRespDto.getUserId());
		List<BuildingRecursionDto> listData = new ArrayList<>();
		listData = queryPid2(listData,listBuildingALL,buildingRespDto.getPId());
		if(buildingRespDto.getCurrentUserId().equals(buildingRespDto.getUserId())){
			return listData;
		}
		List<Integer> listRole = (List<Integer>) redisTemplate.opsForValue().get(RedisConstantUtil.USER_ROLES+buildingRespDto.getCurrentUserId());
		List<Long> listBuilding = roleMapper.queryBuilding(buildingRespDto.getProjectId(),listRole);
		BuildingUtlis.removeId(listData, listBuilding);
		return listData;
	}

	public List<Building> getBuildings(Integer projectId,Long userId) {
		List<Building> listBuildingALL = (List<Building>) redisTemplate.opsForValue().get(RedisConstart.BUILDING + projectId);
		//构造查询条件
		if(CollectionUtils.isEmpty(listBuildingALL)){
			//构造条件
			Building building  = new Building();
			building.setProjectId(projectId);
			building.setUserId(userId);
			//查询数据库数据
			listBuildingALL = buildingMapper.select(building);
			redisTemplate.opsForValue().set(RedisConstart.BUILDING + projectId,listBuildingALL, 7L, TimeUnit.DAYS);
		}else {
			listBuildingALL =  JSONObject.parseArray(JSONObject.toJSONString(listBuildingALL), Building.class);
		}
		return listBuildingALL;
	}

	//如果需查询全部数据，userid传主账号
    @Override
    public List<Long> queryBuildingIds(Long buildingId,Integer projectId,Long mainUserId,Long userId) {
		//查询所有组织架构
		List<Building> buildings = getBuildings(projectId, userId);
		List<Long> ids = new ArrayList<>();
		ids.add(buildingId);
		//查询所有儿子
		querySonId(buildings,buildingId,ids);
		//如果当前用户为主用户
		if(mainUserId.equals(userId)){
			return ids;
		}
		//查询当前用户的角色
		List<Integer> listRole = (List<Integer>) redisTemplate.opsForValue().get(RedisConstantUtil.USER_ROLES+userId);
		//取出当前用户的组织架构
		List<Long> longs = roleService.queryBuilding(projectId, listRole);
		//取交集
		ids.retainAll(longs);
		return ids;
	}

	@Override
	public Result querySerialByBuildingIds(List<Long> ids) {
		List<Map> serialList = equipmentMapper.querySerialByBuildingIds(ids);
		return Result.OK(serialList);
	}

    @Override
    public List<Map> querSubName(Long id, Integer projectId, Long mainUserId) {
	    //全部的数据
        List<Building> buildings = getBuildings(projectId, mainUserId);
        //过滤的数据
        List<Building> collect = buildings.stream().filter(building -> id.equals(building.getParentId()))
                .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(buildings)){
            return null;
        }
        //定义数据
        List<Map> maps = new ArrayList<>(buildings.size());
        List<Long> ids = null;
        Map map = null;
        //循环分组数据
        for (Building building: collect) {
            Long buildingId = building.getId();
            ids = new ArrayList<>();
			map = new HashMap();
            ids.add(buildingId);
            map.put("name",building.getName());
            querySonId(buildings,buildingId,ids);
            map.put("ids",ids);
            maps.add(map);
        }
        return maps;
    }

    @Override
    public List<String> querySubBuildingWaterId(Long id, Integer projectId, Long mainUserId) {
        //全部的数据
        if(id == null || id.equals(0L)){
			return waterUserMapper.queryMetersByProjectId(projectId);
		}
		List<Building> buildings = getBuildings(projectId, mainUserId);
		List<Long> ids = new ArrayList<>();
		ids.add(id);
		querySonId(buildings,id,ids);
		return waterUserMapper.queryMetersByBuildingIds(ids);
    }

    @Override
    public void saveSerial(Long id, Long mainUserId, Integer projectId) {
	    List<Long> ids = new ArrayList<>();
	    ids.add(id);
        List<Building> buildings = getBuildings(projectId, mainUserId);
        queryFater(id,buildings,ids);
        Integer type = 0;
        buildingMapper.updateBuildingSerialToal(ids,type);
        redisTemplate.delete(RedisConstart.BUILDING+projectId);
    }

    @Override
    public void deleteSerial(Long id, Long mainUserId, Integer projectId) {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        List<Building> buildings = getBuildings(projectId, mainUserId);
        queryFater(id,buildings,ids);
        Integer type = 1;
        buildingMapper.updateBuildingSerialToal(ids,type);
        redisTemplate.delete(RedisConstart.BUILDING+projectId);
    }

	@Override
	public Result homeBuilding(Integer projectId, Long mainUserId, Long userId) {
		//递归数据
        List<ExaminationBuildingDto> listData = queryScenarioData(mainUserId, projectId, userId);
        List<SerialDto> serialDtoList = equipmentUserMapper.querySerialAndBuildingId(projectId);
        //分组
        Map<Long, List<SerialDto>> collect = serialDtoList.stream().collect(Collectors.groupingBy(SerialDto::getBuildingId));
        //递归添加设备数据
        addSerialDtoDg(listData,collect);
        return Result.OK(listData);
	}

    @Override
    public Result defaultBuildingId(Integer projectId) {
		BuildingRecursionDto buildingRecursionDto = buildingMapper.defaultBuildingByPorjectId(projectId);
        return Result.OK(buildingRecursionDto);
    }

    public void addSerialDtoDg(List<ExaminationBuildingDto> listData, Map<Long, List<SerialDto>> collect){
        for (ExaminationBuildingDto data: listData) {
            Long id = data.getId();
            data.setListSerial(collect.get(id));
            List<ExaminationBuildingDto> listData1 = data.getListData();
            if(!CollectionUtils.isEmpty(listData1)){
                addSerialDtoDg(listData1,collect);
            }
        }
    }


	public void queryFater(Long id,List<Building> buildingList,List<Long> list){
        for (Building building: buildingList) {
            if(building.getId().equals(id)){
                Long parentId = building.getParentId();
                list.add(parentId);
                if(parentId == null || parentId.equals(0)){
                    return;
                }
                queryFater(parentId,buildingList,list);
            }
        }
    }


	//查出所有儿子的节点id放入ids中
    public void querySonId(List<Building> buildings,Long pid,List<Long> ids){
		for (Building building: buildings) {
			if(building.getParentId().equals(pid)){
				Long id = building.getId();
				ids.add(id);
				querySonId(buildings,id,ids);
			}
		}
	}

    //递归查询
	public List<BuildingRecursionDto> queryPid2(List<BuildingRecursionDto> listData,List<Building> listBuildingALL,Long pId){
		BuildingRecursionDto buildingRecursionDto = null;
		for (Building b: listBuildingALL) {

			if(pId.equals(b.getParentId())){
				buildingRecursionDto = new BuildingRecursionDto(b);
				buildingRecursionDto.setListBuildingRecursionDto(queryPid2(buildingRecursionDto.getListBuildingRecursionDto(),listBuildingALL,b.getId()));
				listData.add(buildingRecursionDto);
			}
		}
		return listData;
	}
	
	

	@Override
	public String queryAddress(Long id) {
		//StringBuffer sb = new StringBuffer();
		List<String> listAddress = new ArrayList<>();
		Building building = buildingMapper.selectByPrimaryKey(id);
		if (building == null){
			return null;
		}
		List<Building> buildings = getBuildings(building.getProjectId(), building.getUserId());
		queryAddressDg(id,listAddress,buildings);
		Collections.reverse(listAddress);
		String address = Joiner.on("/").skipNulls().join(listAddress);
		return address;
	}

	@Override
	public void queryDg(List<ExaminationBuildingDto> listData, Long id, Long userId, Integer projectId, List<Long> listBuilding, List<Long> listEquiment, List<String> listSwitch,Integer flag) {
		for (ExaminationBuildingDto examinationBuildingDto : listData) {
			//如果有子集往里走
			if(!CollectionUtils.isEmpty(examinationBuildingDto.getListData())) {
				queryDg(examinationBuildingDto.getListData(), examinationBuildingDto.getId(), userId,projectId,listBuilding, listEquiment, listSwitch,flag);
			}
			//查询到当前层级的设备和开关
			List<SerialDto> listSerial =  buildingMapper.querySerialAndSwitchByBuilding(examinationBuildingDto.getId(),userId,projectId);
			if(id != null && !CollectionUtils.isEmpty(listSerial)) {
				for (SerialDto serial : listSerial) {
					//是否包含当前id  包含则为ture;
					if(listEquiment.contains(serial.getId())) {
						serial.setStatus(1);
					}
					for (SwitchDto switchDto : serial.getListSwitch()) {
						String switchSn = switchDto.getSwitchSn();
						List<Integer> list = pcTimerModeMapper.querySwitchNum(switchSn,flag);
						switchDto.setNumList(list);
						switchDto.setNum(list.size());
						if(listSwitch.contains(switchSn)) {
							switchDto.setStatus(1);
						}
					}
				}
			}
			examinationBuildingDto.setListSerial(listSerial);
			if(id != null && !CollectionUtils.isEmpty(listBuilding)) {
				if(listBuilding.contains(examinationBuildingDto.getId())) {
					examinationBuildingDto.setStatus(1);
				}
			}
		}
	}

	@Override
	public List<ExaminationBuildingDto> queryScenarioData(Long userId, Integer projectId,Long currentUserId) {
		List<Building> listBuildingALL = getBuildings(projectId,userId);
		List<ExaminationBuildingDto> listData = new ArrayList<>();
		listData = queryPid(listData,listBuildingALL,0L);
		//List<ExaminationBuildingDto> listData = pcTimerModeMapper.queryBuilding(userId,projectId);
		if(!userId.equals(currentUserId)) {
			List<Integer> listRole = (List<Integer>) redisTemplate.opsForValue().get(RedisConstantUtil.USER_ROLES+currentUserId);
			List<Long> listBuildingRole = roleMapper.queryBuilding(projectId,listRole);
			BuildingUtlis.removeId2(listData, listBuildingRole);
			log.info("查询角色列表:用户id{},当前用户{},组织架构列表:{}",userId,currentUserId,listData);
		}
		return listData;
	}

	//递归查询
	public List<ExaminationBuildingDto> queryPid(List<ExaminationBuildingDto> listData,List<Building> listBuildingALL,Long pId){
		ExaminationBuildingDto buildingRecursionDto = null;
		for (Building b: listBuildingALL) {
			if(pId.equals(b.getParentId())){
				buildingRecursionDto = new ExaminationBuildingDto(b);
				buildingRecursionDto.setListData(queryPid(buildingRecursionDto.getListData(),listBuildingALL,b.getId()));
				listData.add(buildingRecursionDto);
			}
		}
		return listData;
	}

	public void queryAddressDg(Long id ,List<String> listAddress,List<Building> buildingList) {
		Building building = null;
		for (Building b: buildingList) {
			if(b.getId().equals(id)){
				building = b;
				break;
			}
		}
		if(building == null) {
			return ;
		}
		listAddress.add(building.getName());
		if(building.getParentId().equals(0L)) {
			return ;
		}
		buildingList.remove(building);
		queryAddressDg(building.getParentId(),listAddress,buildingList);
		return ;
	}
}
