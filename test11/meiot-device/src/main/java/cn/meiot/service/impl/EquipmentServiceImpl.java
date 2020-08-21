package cn.meiot.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cn.meiot.client.MessageCilent;
import cn.meiot.config.CmdConstart;
import cn.meiot.entity.db.*;
import cn.meiot.entity.equipment2.BaseEntity2;
import cn.meiot.entity.equipment2.upswitch.Switchd;
import cn.meiot.entity.vo.*;
import cn.meiot.enums.EquipmentStatus;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.service.*;
import cn.meiot.utils.*;
import cn.meiot.utlis.EquipmentUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.meiot.client.CommunicationClient;
import cn.meiot.client.StatisticsClient;
import cn.meiot.client.UserClient;
import cn.meiot.constart.ProjectConstart;
import cn.meiot.constart.RedisConstart;
import cn.meiot.dao.ChangeSwitchMapper;
import cn.meiot.dao.EquipmentMapper;
import cn.meiot.dao.EquipmentUserMapper;
import cn.meiot.dao.RoleEquipmentMapper;
import cn.meiot.dao.SwitchMapper;
import cn.meiot.entity.dto.EquipmentRespDto;
import cn.meiot.entity.dto.SwitchRespDto;
import cn.meiot.entity.dto.TimingExamination;
import cn.meiot.entity.dto.pc.equipment.EquipmentPcDto;
import cn.meiot.entity.dto.pc.equipmentUser.EquipmentUserInsert;
import cn.meiot.entity.equipment.BaseEntity;
import cn.meiot.entity.equipment.Device;
import cn.meiot.entity.equipment.sckgds.Sckgds;
import cn.meiot.enums.DeviceBindStatus;
import cn.meiot.exception.MyServiceException;
import cn.meiot.utlis.PhoneUtils;
import cn.meiot.utlis.TimeUtlis;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lingzhiying
 * @title: EquipmentServiceImpl.java
 * @projectName spacepm
 * @description:
 * @date 2019年8月16日
 */
@Service
@Slf4j
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private EquipmentUserMapper equipmentUserMapper;

    @Autowired
    private SwitchMapper switchMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CommunicationClient communicationClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private StatisticsClient statisticsClient;

    @Autowired
    private SwitchNameService switchNameService;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private RoleEquipmentMapper roleEquipmentMapper;

    @Autowired
    private NetworkingUtlis networkingUtlis;

    @Autowired
    private ChangeSwitchMapper changeSwitchMapper;

    @Autowired
    private MessageCilent messageCilent;

    @Autowired
    private UseTimeService useTimeService;

    @Autowired
    private EquipmentUserService equipmentUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private SwitchService switchService;

    @Autowired
    private ExaminationService examinationService;

    @Override
    public Result queryEquipment(String serialNumber, Long userId) {
        EquipmentRespDto equipment = equipmentMapper.querySerialNumber(serialNumber, userId);
        if (equipment == null) {
            Result result = Result.getDefaultFalse();
            result.setMsg(ResultCodeEnum.EQUIMENT_NO_EXISTENT.getMsg());
            result.setCode(ResultCodeEnum.EQUIMENT_NO_EXISTENT.getCode());
            return result;
        }
        Map<String, Map<String, String>> maps = userService.getRule();
        String imageKey = EquipmentUtil.getImageKey(maps, serialNumber, ConstantsUtil.DEVICE_DEFAULT_RULES_BIG_KEY);
        String url = userClient.getConfigValueByKey(imageKey);
        String mPath = FileConfigVo.getMPath(url);
        equipment.setImage(mPath);
        Result result = Result.getDefaultTrue();
        result.setData(equipment);
        if (!equipment.getBuildingId().equals(0L)) {
            equipment.setUserStatus(3);
        }
        EquipmentUser select = equipmentUserMapper.queryEquipmentByUser(serialNumber, userId);
        if (select != null && select.getUserStatus() != null) {
            equipment.setUserStatus(select.getUserStatus());
            //如果已经绑定过了,返回,否则往下走
            if (select.getUserStatus().equals(1)) {
                return result;
            }
        }
        //查找该设备的主账户
        Long rtuserIdByUserId = equipmentUserMapper.getRtuserIdByUserId(serialNumber);
        if (rtuserIdByUserId != null) {
            Map<String, Object> subinfoById = userClient.getInfoById(rtuserIdByUserId);
            if (subinfoById != null) {
                if (subinfoById.get("phone") != null) {
                    equipment.setUserPhone(PhoneUtils.getPhone(subinfoById.get("phone")));
                }
                if (subinfoById.get("userName") != null) {
                    equipment.setUserName(subinfoById.get("userName").toString());
                }
            }
        }
        return result;
    }

    @Override
    @Transactional
    public Result bindEquipment(String serialNumber, Long userId) {
        Equipment equipment = equipmentMapper.selectByPrimaryKey(serialNumber);
        if (equipment == null) {
            Result result = Result.getDefaultFalse();
            result.setMsg(ResultCodeEnum.EQUIMENT_NO_EXISTENT.getMsg());
            result.setCode(ResultCodeEnum.EQUIMENT_NO_EXISTENT.getCode());
            return result;
        }

        List<EquipmentUser> listEquipment = equipmentUserMapper.queryEqBySerialNumberAnduserId(serialNumber);
        Integer queryUserIdAndSerialumber = equipmentUserMapper.queryUserIdAndSerialumber(userId, null);
        //默认设备
        Integer isDefault = 0;
        //如果用户没有任何设备 则设为默认设备
        if (queryUserIdAndSerialumber == null) {
            isDefault = 1;
        }
        String name = userClient.getConfigValueByKey(ConstantsUtil.ConfigItem.SERIAL_NAME);
        //过滤出正在绑定的数据
        List<EquipmentUser> equipmentUserStream = listEquipment.stream()
                .filter(equipmentUser -> equipmentUser.getUserStatus().equals(1)).collect(Collectors.toList());
        //该设备没有绑定过任何用户
        if (CollectionUtils.isEmpty(equipmentUserStream)) {
            EquipmentUser equipmentUser = new EquipmentUser();
            equipmentUser.setSerialNumber(serialNumber);
            equipmentUser.setUserId(userId);
            equipmentUser.setUserStatus(1);
            equipmentUser.setIsDefault(isDefault);
            equipmentUser.setIsPrimary(1);
            equipmentUser.setName(name);
            redisTemplate.opsForValue().set(RedisConstantUtil.PROJECT_SERIALNUMER + serialNumber, 0);
            redisTemplate.opsForHash().put(RedisConstantUtil.NIKNAME_SERIALNUMBER, equipmentUser.getUserId() + "_" + equipmentUser.getSerialNumber(), equipmentUser.getName());
            equipment.setEquipmentStatus(1);
            equipmentUserMapper.insertSelective(equipmentUser);
            switchNameService.insertSwitchName(serialNumber, userId, null);
            equipmentMapper.updateByPrimaryKeySelective(equipment);
            Result result = equipmentUserService.queryEquipment(serialNumber, userId);
            useTimeService.insert(userId, null);
            return result;
        }
        //如果是子账户删除缓存数据
        redisTemplate.opsForHash().delete(RedisConstantUtil.SERIAL_NUMBER_USER_ID, serialNumber);
        Long mainUserId = null;
        String serialNumberName = null;
        for (EquipmentUser equipmentUser : listEquipment) {
            if (userId.equals(equipmentUser.getUserId())) {
                //定义该设备用户之前是否绑定过该设备
                Result result = Result.getDefaultFalse();
                if (equipmentUser.getUserStatus().equals(1)) {
                    result.setMsg(ResultCodeEnum.IS_YOUR_EQUIMENT.getMsg());
                } else {
                    result.setMsg(ResultCodeEnum.ALREADY_APPLY.getMsg());
                }
                return result;
            }
            if (equipmentUser.getIsPrimary().equals(1)) {
                mainUserId = equipmentUser.getUserId();
                if (StringUtils.isNotBlank(equipmentUser.getName())) {
                    serialNumberName = equipmentUser.getName();
                } else {
                    //没有名字则使用默认名称
                    serialNumberName = name;
                }
            }
        }
        if (mainUserId == null) {
            throw new MyServiceException(ResultCodeEnum.NO_AUTHENTICATION.getCode(), ResultCodeEnum.NO_AUTHENTICATION.getMsg());
        }
        EquipmentUser equipmentUser = new EquipmentUser();
        equipmentUser.setSerialNumber(serialNumber);
        equipmentUser.setUserId(userId);
        equipmentUser.setIsDefault(isDefault);
        equipmentUserMapper.insertSelective(equipmentUser);
        //设备用户为空
        //调用发消息接口
        SysMsgVo sysMsgVo = new SysMsgVo();
        sysMsgVo.setSerialNumber(serialNumber);
        sysMsgVo.setExtendId(equipmentUser.getId());
        sysMsgVo.setType(1);
        sysMsgVo.setSerialName(serialNumberName);
        Map<String, String> map = new HashMap<String, String>();
        map.put("subUser", userId + "");
        map.put("mainUser", mainUserId + "");
        sysMsgVo.setDealStatus(DeviceBindStatus.PENDING.value());
        Map<String, Object> infoById = userClient.getInfoById(mainUserId);
        Map<String, Object> subinfoById = userClient.getInfoById(userId);
        map.put("subUserPhone", PhoneUtils.getPhone(subinfoById.get("phone")));
        map.put("subUserName", subinfoById.get("userName") + "");
        map.put("mainUserPhone", PhoneUtils.getPhone(infoById.get("phone")));
        map.put("mainUserName", infoById.get("userName") + "");
        //此处调用用户中心查询用户名
        //map.put("phone", "123456");
        //map.put("userName", "用户名");
        sysMsgVo.setExtras(map);
        rabbitTemplate.convertAndSend(QueueConstantUtil.SYS_MSG_QUEUE, sysMsgVo);
        log.info("绑定设备推送: {}", sysMsgVo);
        Result result = Result.getDefaultTrue();
        return result;
    }

    @Override
    public Result queryEquipmentStatus(String serialNumber, Long userId) {
        String str1 = RedisConstart.DEVICE + serialNumber;
        Integer yearTime = TimeUtlis.getYearTime();
        List<SwitchRespDto> listSwitchRespDto = switchMapper.querySwitchIndexByNumber(userId, serialNumber,0);
        for (SwitchRespDto switchRespDto : listSwitchRespDto) {
            //Object obj = redisTemplate.boundHashOps(RedisConstart.DEVICE+serialNumber);
            //Long long1 = new Long(switchRespDto.getSwitchSn());
            Object status = redisTemplate.opsForHash().get(str1, switchRespDto.getSwitchSn());
            if (status == null) {
                continue;
            }
            Object obj = redisTemplate.opsForValue().get(RedisConstantUtil.FAULT_SERIALNUMER + "_" + switchRespDto.getSwitchSn());
            if (null == obj) {
                switchRespDto.setCloseStatus(1);
            } else {
                switchRespDto.setCloseStatus((Integer) obj);
            }
            Map status3 = (Map) status;
            //String loadmax = status3.get("loadmax").toString();
            //String status2 = status3.get("switch").toString();
            switchRespDto.setLoadmax(status3.get("loadmax") != null ? (Integer) status3.get("loadmax") : 0);
            switchRespDto.setStatus(status3.get("switch") != null ? (Integer) status3.get("switch") : 0);
            Integer networkingStatus = networkingUtlis.getNetworkingStatus("", serialNumber);
            if (networkingStatus.equals(0)) {
                switchRespDto.setStatus(0);
            }
            Result list = statisticsClient.listDevice(serialNumber, Long.valueOf(switchRespDto.getSwitchSn()), yearTime, userId);
            try {
                if ("0".equals(list.getCode())) {
                    Map data = (Map) list.getData();
                    List<Map> listMap = (List<Map>) data.get("list");
                    switchRespDto.setListMap(listMap);
                    log.info("从统计中心格式化数据:{}", listMap);
                }
            } catch (Exception e) {
                log.info("格式化数据失败:{}", list);
            }
            log.info("取出统计中心数据:{}", list);
        }
        Integer status = equipmentUserMapper.querySwitchByUserId(userId, serialNumber);
        Map<String, Object> map = new HashMap<>();
        map.put("listSwitch", listSwitchRespDto);
        map.put("status", status);
        //map.put("networking");
        Result defaultTrue = Result.getDefaultTrue();
        defaultTrue.setData(map);
        return defaultTrue;
    }

    @Transactional
    public void up108(String serialNumber, JSONObject content) {
        Equipment equipment = new Equipment();
        List<Device> listDevice = JSON.parseArray(content.get("arrays").toString(), Device.class);
        Integer index = listDevice.size();
        List<Sckgds> arrayList = new ArrayList<Sckgds>();
        Switch oldSwitch = new Switch();
        oldSwitch.setSerialNumber(serialNumber);
        List<Switch> oldSwList = switchMapper.select(oldSwitch);
        for (Device device : listDevice) {
            //判断新增还是修改
            //boolean flag = false;
            for (Switch switch1 : oldSwList) {
                if (switch1.getSwitchIndex().equals(device.getIndex())) {
                    //sw = switch1;
                    if (!switch1.getSwitchSn().equals(device.getId() + "")) {
                        DeviceVo build = DeviceVo.builder().serialNumber(serialNumber)
                                .switchIndex(device.getIndex())
                                .oldSwitchSn(Long.valueOf(switch1.getSwitchSn()))
                                .newSwitchSn(device.getId()).build();
                        ChangeSwitch ch = new ChangeSwitch(build);
                        ch.setType(ProjectConstart.SWITCH_UPD);
                        changeSwitchMapper.insertSelective(ch);
                        log.info("更换开关数据:{}", ch);
                        rabbitTemplate.convertAndSend(QueueConstantUtil.CHANGE_SWTICH_SN, "", build);
                    }
                    break;
                }
            }
            Switch sw = new Switch();
            sw.setSwitchIndex(device.getIndex());
            sw.setSerialNumber(serialNumber);
            sw.setSwitchModel(device.getMode());
            sw.setSwitchSn(device.getId().toString());
            if (index.equals(device.getIndex())) {
                sw.setParentIndex(0);
                redisTemplate.opsForHash().put(RedisConstantUtil.DEVICE_MASTER_INDEX, serialNumber, device.getIndex());
                redisTemplate.opsForHash().put(RedisConstantUtil.DEVICE_MASTER_SN, serialNumber, device.getId());
            } else {
                sw.setParentIndex(index);
            }
            Switch selectByPrimaryKey = switchMapper.selectByPrimaryKey(sw.getSwitchSn());
            if (selectByPrimaryKey == null) {
                switchMapper.insertSelective(sw);
            } else {
				/*if(!selectByPrimaryKey.getSerialNumber().equals(serialNumber)){

				}*/
                switchMapper.updateByPrimaryKeySelective(sw);
            }
            Sckgds s = new Sckgds();
            s.setDevice(device);
            arrayList.add(s);
        }
        //删除多余的开关
        a:
        for (Switch switch1 : oldSwList) {
            for (Device device : listDevice) {
                if (device.getId().equals(Long.valueOf(switch1.getSwitchSn()))) {
                    continue a;
                }
            }
            ChangeSwitch build = ChangeSwitch.builder()
                    .switchIndex(switch1.getSwitchIndex())
                    .serialNumber(serialNumber)
                    .oldSwitchSn(Long.valueOf(switch1.getSwitchSn()))
                    .type(ProjectConstart.SWITCH_DEL).build();
            changeSwitchMapper.insertSelective(build);
            switchMapper.deleteByPrimaryKey(switch1.getSwitchSn());
        }
        //同步时间
        BaseEntity<Sckgds> be = new BaseEntity<Sckgds>(serialNumber, MqttUtil.CMD_03, arrayList);
        rabbitTemplate.convertAndSend(QueueConstantUtil.SEND_TIMER, be);
        equipment.setSerialNumber(serialNumber);
        EquipmentUtils.setModelAndVoltage(equipment);
        equipment.setSwitchCount(index);
        Equipment selectByPrimaryKey = equipmentMapper.selectByPrimaryKey(serialNumber);
        if (selectByPrimaryKey == null) {
            equipmentMapper.insertSelective(equipment);
        } else {
            equipmentMapper.updateByPrimaryKeySelective(equipment);
        }
        redisTemplate.delete(RedisConstantUtil.SERIAL_REFRESH + serialNumber);
    }


    @Override
    public Result examination(Equipment equipment) {
        int row = equipmentMapper.updateByPrimaryKeySelective(equipment);
        Equipment equipment1 = equipmentMapper.selectByPrimaryKey(equipment.getSerialNumber());
        return Result.OK(equipment1.getExaminationTime());
    }

    @Override
    public Result timingExamination() {
        String time = TimeUtlis.getTime();
        List<TimingExamination> select = equipmentMapper.queryExamination(time);
        for (TimingExamination equipment2 : select) {
            examinationService.sendExaminationVerOne(equipment2);
            //return sendSwitch;
        }
        List<TimingExamination> select2 = equipmentMapper.queryExamination2(time);
        Map<String, List<TimingExamination>> collect = select2.stream().collect(Collectors.groupingBy(TimingExamination::getSerialNumber));
        Set<String> strings = collect.keySet();
        for (String string : strings) {
            List<TimingExamination> timingExaminations = collect.get(string);
            examinationService.sendExaminationVerTwo(string, timingExaminations);
        }
        return Result.getDefaultTrue();
    }


    @Override
    public Result realtime(String serialNumber) {
        String masterIndex = switchMapper.getMasterSn(serialNumber);
        Object object = null;
        try {
            object = redisTemplate.opsForHash().get(RedisConstart.DEVICE + serialNumber, masterIndex);
        } catch (Exception e) {
            Map map = new HashMap();
            map.put("leakage", 0);
            map.put("temp", 0);
            map.put("current", 0);
            map.put("voltage", 0);
            map.put("power", 0);
            map.put("masterSn", masterIndex);
            Result defaultTrue = Result.getDefaultTrue();
            defaultTrue.setData(map);
            return defaultTrue;
        }
        if (object == null) {
            Map map = new HashMap();
            map.put("leakage", 0);
            map.put("temp", 0);
            map.put("current", 0);
            map.put("voltage", 0);
            map.put("power", 0);
            map.put("masterSn", masterIndex);
            Result defaultTrue = Result.getDefaultTrue();
            defaultTrue.setData(map);
            return defaultTrue;
        }
        log.info("取出缓存实时数据: {}", object);
        Map obj = (Map) object;
        Map map = new HashMap();
        map.put("masterSn", masterIndex);
        BigDecimal current = BigDecimal.ZERO;
        BigDecimal voltage = BigDecimal.ZERO;
        BigDecimal power = BigDecimal.ZERO;
        Integer leakage = (Integer) obj.get("leakage");
        map.put("leakage", leakage);
        map.put("temp", obj.get("temp") == null ? 0 : obj.get("temp"));
        map.put("current", current);
        map.put("voltage", voltage);
        List list = obj.get("current") == null ? null : (List) obj.get("current");
        List list2 = obj.get("voltage") == null ? null : (List) obj.get("voltage");
        if (!CollectionUtils.isEmpty(list)) {
            String string = list.get(0).toString();
            current = new BigDecimal(string).divide(new BigDecimal(1000), 1, BigDecimal.ROUND_HALF_UP);
            map.put("current", current);
        }
        if (!CollectionUtils.isEmpty(list2)) {
            String string = list2.get(0).toString();
            voltage = new BigDecimal(string).divide(new BigDecimal(1000), 0, BigDecimal.ROUND_HALF_UP);
            map.put("voltage", voltage);
        }
        //power = current.multiply(voltage).setScale(1);
        map.put("power", obj.get("power") == null ? 0 : obj.get("power"));
        Result defaultTrue = Result.getDefaultTrue();
        defaultTrue.setData(map);
        return defaultTrue;
    }


    @Override
    public Result queryBuilding(Long id, Long mainUserId, Long userId) {
        Result defaultTrue = Result.getDefaultTrue();
        List<Integer> listRole = null;
        if (!mainUserId.equals(userId)) {
            listRole = (List<Integer>) redisTemplate.opsForValue().get(RedisConstantUtil.USER_ROLES + userId);
            if (CollectionUtils.isEmpty(listRole)) {
                return defaultTrue;
            }
        }
        Map map = new HashMap();
        List<EquipmentPcDto> listEq = equipmentMapper.queryBuilding(id, mainUserId, listRole);
        listEq.forEach(eq -> {
            eq.setIsOnline(networkingUtlis.getNetworkingStatus("", eq.getSerialNumber()));
            eq.setFaultNum(messageCilent.faultNumber(eq.getSerialNumber(), mainUserId));
        });
        map.put("list", listEq);
        map.put("address", buildingService.queryAddress(id));
        defaultTrue.setData(map);
        return defaultTrue;
    }

    @Override
    @Transactional
    public Result pcInsert(EquipmentUserInsert equipmentUserInsert) {
        Long buildingId = equipmentUserInsert.getBuildingId();
        int row = equipmentMapper.updateBuildingIdBySn(equipmentUserInsert);
        if (row != 1) {
            //throw new MyServiceException(ResultConstart.CODE_22_19, "新增设备失败");
            throw new MyServiceException(ResultCodeEnum.EQUIMENT_NO_EXISTENT.getCode(), ResultCodeEnum.EQUIMENT_NO_EXISTENT.getMsg());
        }
        List<EquipmentUser> queryEqBySerialNumberAnduserId = equipmentUserMapper.queryEqBySerialNumberAnduserId(equipmentUserInsert.getSerialNumber());
        if (!CollectionUtils.isEmpty(queryEqBySerialNumberAnduserId)) {
            //throw new MyServiceException(ResultConstart.CODE_22_19, "新增设备失败");
            throw new MyServiceException(ResultCodeEnum.SERIAL_BIND.getCode(), ResultCodeEnum.SERIAL_BIND.getMsg());
        }
        Long userId = equipmentUserInsert.getUserId();
        Integer projectId = equipmentUserInsert.getProjectId();
        useTimeService.insert(userId, projectId);
        roleEquipmentMapper.insertBuildingEquipment(equipmentUserInsert.getBuildingId(), equipmentUserInsert.getSerialNumber(), projectId);
        //添加默认名
        switchNameService.insertSwitchName(equipmentUserInsert.getSerialNumber(), userId, projectId);
        //向缓存添加项目的设备数据
        redisTemplate.opsForValue().set(RedisConstantUtil.PROJECT_SERIALNUMER + equipmentUserInsert.getSerialNumber(), projectId);
        equipmentUserMapper.pcInsert(equipmentUserInsert);
        redisTemplate.opsForHash().put(RedisConstantUtil.NIKNAME_SERIALNUMBER, userId + "_" + equipmentUserInsert.getSerialNumber(), equipmentUserInsert.getName());
        buildingService.saveSerial(buildingId, userId, projectId);
        return Result.getDefaultTrue();
    }

    @Override
    public void activationSerial(LinkedHashMap linkedHashMap) {
        String serialNumber = (String) linkedHashMap.get("nodeId");
        Equipment equipment = equipmentMapper.selectByPrimaryKey(serialNumber);
        if (equipment != null) {
            return;
        }
        equipment = new Equipment();
        equipment.setSerialNumber(serialNumber);
        equipment.setEquipmentStatus(1);
        equipment.setCompany(1);
        equipmentMapper.insertSelective(equipment);
    }

    @Override
    public Integer serialCompany(String serialNumber) {
        return equipmentMapper.queryCompanyBySerialNumber(serialNumber);
    }

    @Override
    public Result batchExamination(List<String> serialList, Integer examinationStatus, String examinationTime) {
        equipmentMapper.batchExamination(serialList, examinationStatus, examinationTime);
        return Result.getDefaultTrue();
    }

    @Override
    @Transactional
    public void insertEquipment2(String serialNumber, List<Switchd> list) {
//		Switch oldSwitch = new Switch();
//		oldSwitch.setSerialNumber(serialNumber);
        List<Switch> oldSwList = switchMapper.selectCountSerialNumber(serialNumber);
//        if (CollectionUtils.isEmpty(list)) {
//            for (Switch switc : oldSwList) {
//                String switchSn = switc.getSwitchSn();
//                switchMapper.deleteByPrimaryKey(switchSn);
//            }
//            redisTemplate.delete(RedisConstantUtil.SERIAL_REFRESH + serialNumber);
//            deleteWaringSwitch(serialNumber,null);
//            return;
//        }
        int size = list.size();
        Equipment equipment = equipmentMapper.selectByPrimaryKey(serialNumber);
        boolean isInsert = false;
        if (equipment == null) {
            equipment = new Equipment();
            isInsert = true;
            EquipmentUtils.setModelAndVoltage(equipment);
        }
        //设备数据
        equipment.setSerialNumber(serialNumber);
        equipment.setSwitchCount(size);
        equipment.setAgreementVersion(2);
        if (isInsert) {
            equipmentMapper.insertSelective(equipment);
        } else {
            equipmentMapper.updateByPrimaryKeySelective(equipment);
        }
        isInsert = false;
        //添加开关数据
        List<String> oldSw = new ArrayList<>();
        Switch switche = null;
        int index = size;
        for (int i = 0; i < size; i++) {
            isInsert = false;
            Switchd switchd = list.get(i);
            //Integer index = switchd.getIndex() -1 ;
            String switchSn = switchd.getSid().toString();
            switche = new Switch();
            switche.setSwitchSn(switchSn);
            switche.setSerialNumber(serialNumber);
            switche.setSwitchIndex(index);
            switche.setSwitchModel(switchd.getType());
            index--;
//            if (i == 0) {
//                switche.setParentIndex(0);
//            } else {
//                switche.setParentIndex(size);
//            }
            //switche.setParentIndex(size);
            for (int j = oldSwList.size() - 1; j >= 0; j--) {
                Switch aSwitch = oldSwList.get(j);
                String switchSn1 = aSwitch.getSwitchSn();
                if (switchSn.equals(switchSn1)) {
                    isInsert = true;
                    oldSwList.remove(j);
                    switchMapper.updateByPrimaryKeySelective(switche);
                    break;
                }
            }
            if (!isInsert) {
                Switch aSwitch = switchMapper.selectByPrimaryKey(switchSn);
                if (aSwitch == null) {
                    switchMapper.insertSelective(switche);
                } else {
                    if (!aSwitch.getSerialNumber().equals(serialNumber)) {
                        switchMapper.updateByPrimaryKeySelective(switche);
                    }
                }
            }
        }
        for (Switch switc : oldSwList) {
            String switchSn = switc.getSwitchSn();
            if (StringUtils.isBlank(switchSn)) {
                continue;
            }
            switc.setDeleted(1);
            switchMapper.updateByPrimaryKeySelective(switc);
            //switchMapper.deleteByPrimaryKey(switchSn);
            //deleteWaringSwitch(serialNumber,switchSn);
        }
        redisTemplate.delete(RedisConstantUtil.SERIAL_REFRESH + serialNumber);
    }

    private void deleteWaringSwitch(String serialNumber,String switchSn){
        if(StringUtils.isBlank(serialNumber)){
            return;
        }
        if(StringUtils.isBlank(switchSn)){
            redisTemplate.delete(ConstantsUtil.ALARM_SERAIL + serialNumber);
            return;
        }
        redisTemplate.opsForHash().delete(ConstantsUtil.ALARM_SERAIL + serialNumber,switchSn);
    }

    @Override
    public void updateVersion(String serialNumber, String ver) {
        Equipment oldEquipment = equipmentMapper.selectByPrimaryKey(serialNumber);
        boolean isInsert = false;
        Equipment equipment = new Equipment();
        if (oldEquipment == null) {
            isInsert = true;
        }
        equipment.setSerialNumber(serialNumber);
        equipment.setVersion(ver);
        if (isInsert) {
            equipmentMapper.insertSelective(equipment);
        } else {
            equipmentMapper.updateByPrimaryKeySelective(equipment);
        }
    }

    @Override
    public Integer queryAgreementVersion(String serialNumber) {
        return equipmentMapper.queryAgreementVersion(serialNumber);
    }

    @Override
    public Integer queryAgreementVersionBySwitchSn(String switchSn) {
        return equipmentMapper.queryAgreementVersionBySwitchSn(switchSn);
    }

    @Override
    public Result selectSwitch(String serialNumber) {
        String key = RedisConstantUtil.SERIAL_REFRESH + serialNumber;

        Integer integer = queryAgreementVersion(serialNumber);
        Result result = null;
        if (integer.equals(2)) {
            BaseEntity2 baseEntity2 = new BaseEntity2();
            baseEntity2.setCmd(CmdConstart.CMD_210);
            result = switchService.baseEntityJson2(baseEntity2, serialNumber);
        } else {
            Map map = new HashMap();
            map.put("switchinfo", 1);
            BaseEntity baseEntity = new BaseEntity(serialNumber, MqttUtil.CMD_01, map);
            String jsonString = JSON.toJSONString(baseEntity, true);
            result = switchService.sendMessage(serialNumber, jsonString);
        }
        if (!Result.SUCCESSFUL_CODE.equals(result.getCode())) {
            return result;
        }
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(key, "1", 2, TimeUnit.SECONDS);
        if (aBoolean) {
            return Result.faild(null);
        }
        //死循环跑数据
        while (redisTemplate.opsForValue().get(key) != null) {
        }
        return result;
    }

    @Override
    public List<EquialStatusVo> listEquialStatus(List<String> listEquial, Integer equipmentStatus, Long userId,Integer projectId) {
        List<EquialStatusVo> statusVoList = new ArrayList<>();
        boolean alarm = EquipmentStatus.ALARM.status().equals(equipmentStatus);
        //boolean notAll = EquipmentStatus.ALL.equals(equipmentStatus);
        boolean waring = EquipmentStatus.WARNING.status().equals(equipmentStatus);
        boolean disconnection = EquipmentStatus.DISCONNECTION.status().equals(equipmentStatus);
        int switchStatus = 0;
        int isOnline = 0;
        List<String> onlineList = null;
        List<String> warningList = null;
        List<String> alarmList = null;
        if (alarm || waring) {
            switchStatus = equipmentStatus;
            onlineList = networkingUtlis.deviceOnLineNumList(listEquial);
        } else if (disconnection) {
            //给断网
            isOnline = 0;
        } else {
            onlineList = networkingUtlis.deviceOnLineNumList(listEquial);
            alarmList = messageCilent.queryFaultSerial(listEquial, projectId, EquipmentStatus.ALARM.status());
            warningList = messageCilent.queryFaultSerial(listEquial, projectId, EquipmentStatus.WARNING.status());
        }

        boolean onlineSerial = !CollectionUtils.isEmpty(onlineList);
        for (String serialNumber : listEquial) {
            Integer switchStatus2 = switchStatus;
            Integer isOnline2 = isOnline;
            if (!alarm && !waring) {
                if(!CollectionUtils.isEmpty(alarmList) && alarmList.remove(serialNumber)){
                    switchStatus2 = EquipmentStatus.ALARM.status();
                }else if(!CollectionUtils.isEmpty(warningList) && warningList.remove(serialNumber)){
                    switchStatus2 = EquipmentStatus.WARNING.status();
                }
            }
            if (!disconnection){
                if (onlineSerial) {
                    if (onlineList.contains(serialNumber)) {
                        isOnline2 = 1;
                    }else{
                        isOnline2 = 0;
                    }
                }else{
                    isOnline2 = 0;
                }
            }
            String serialName = equipmentUserService.findSerialName(serialNumber, userId);
            List<SwitchName> switchNames = switchMapper.querySwitch(serialNumber);
            int openCount = 0;
            for (SwitchName switchName: switchNames) {
                String switchSn = switchName.getSwitchSn();
                String s = RedisConstart.DEVICE + serialNumber;
                Map status = (Map) redisTemplate.opsForHash().get(s,switchSn );
                if(status == null) {
                    continue;
                }
                Integer switchStatus1 = SwitchUtil.getSwitchStatus(status);
                if(1 == switchStatus1){
                    openCount++;
                }
            }
            EquialStatusVo equialStatusVoBuilder = EquialStatusVo.builder().serialNumber(serialNumber).serialName(serialName)
                    .openNum(openCount).closeNum(switchNames.size() - openCount)
                    .isOnline(isOnline2).switchStatus(switchStatus2).build();
            statusVoList.add(equialStatusVoBuilder);
        }
        return statusVoList;
    }

}

