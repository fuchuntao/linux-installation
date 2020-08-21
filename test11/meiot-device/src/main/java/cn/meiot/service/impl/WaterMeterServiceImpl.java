package cn.meiot.service.impl;

import cn.meiot.dao.WaterMeterMapper;
import cn.meiot.dao.WaterUserMapper;
import cn.meiot.entity.db.Building;
import cn.meiot.entity.db.WaterMeter;
import cn.meiot.entity.db.WaterUser;
import cn.meiot.entity.dto.pc.water.WaterConditionDto;
import cn.meiot.entity.dto.pc.water.WaterMeterDto;
import cn.meiot.entity.excel.FloorWaterExcel;
import cn.meiot.entity.excel.InformationExcel;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.water.Customer;
import cn.meiot.entity.water.CustomerImeter;
import cn.meiot.entity.water.Imeter;
import cn.meiot.entity.water.Record;
import cn.meiot.enums.ResultCodeEnum;
import cn.meiot.enums.WaterType;
import cn.meiot.exception.MyServiceException;
import cn.meiot.service.BuildingService;
import cn.meiot.service.WaterMeterService;
import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utils.WaterUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class WaterMeterServiceImpl implements WaterMeterService {

    @Autowired
    private WaterMeterMapper waterMeterMapper;

    @Autowired
    private WaterUserMapper waterUserMapper;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private WaterUtils waterUtils;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Result saveWaterUser(WaterUser waterUser) {
        //查询设备id是否存在
        /*String meterId = waterUser.getMeterId();
        if(waterUser.getId()!=null){
            waterUserMapper.updateByPrimaryKeySelective(waterUser);
            return Result.getDefaultTrue();
        }*/
        waterUserMapper.insertSelective(waterUser);
        return Result.getDefaultTrue();
    }

    @Override
    public void waterAuthentication(Integer projectId, Long mainUserId, String meterId) {
        WaterMeter waterMeter = waterMeterMapper.selectByPrimaryKey(meterId);
        if(waterMeter == null){
            throw new MyServiceException(ResultCodeEnum.METER_INSET_NUMBER_ERROR.getCode(), ResultCodeEnum.METER_INSET_NUMBER_ERROR.getMsg());
        }
        WaterUser oldWaterUser = waterUserMapper.queryWaterUserByMeterId(meterId);
        if(oldWaterUser != null){
            if(!mainUserId.equals(oldWaterUser.getUserId())){
                throw new MyServiceException(ResultCodeEnum.METER_INSET_ERROR.getCode(), ResultCodeEnum.METER_INSET_ERROR.getMsg());
            }
            if(!projectId.equals(oldWaterUser.getProjectId())){
                throw new MyServiceException(ResultCodeEnum.METER_INSET_PROJECT_ERROR.getCode(), ResultCodeEnum.METER_INSET_PROJECT_ERROR.getMsg());
            }
            throw new MyServiceException(ResultCodeEnum.METER_INSET_ERROR.getCode(), ResultCodeEnum.METER_INSET_ERROR.getMsg());
        }
    }

    @Override
    public Result updateWaterUser(WaterUser waterUser) {
        int i = waterUserMapper.updateWaterUser(waterUser);
        if (i==0)
            return  Result.getDefaultFalse();
        return Result.getDefaultTrue();
    }

    @Override
    public Result deleteWaterUser(List<Long> ids, Integer projectId, Long mainUserId) {
        waterUserMapper.deleteWaterUsers(ids,projectId,mainUserId);
        return Result.getDefaultTrue();
    }

    @Override
    public Result information(WaterMeterDto waterMeterDto) {
        PageHelper.startPage(waterMeterDto.getPage(), waterMeterDto.getPageSize());
        List<WaterMeterDto> waterMeterDtoList = waterUserMapper.information(waterMeterDto);
        PageInfo pageinfo = new PageInfo<>(waterMeterDtoList);
        /*waterMeterDtoList.forEach( waterMeterDto1->{
            String address = buildingService.queryAddress(waterMeterDto1.getBuildingId());
            waterMeterDto1.setAddress(address);
        });*/
        Result defaultTrue = Result.getDefaultTrue();
        defaultTrue.setData(pageinfo);
        return defaultTrue;
    }

    @Override
    public void systemAddWater() {
        Boolean systemAddWater = redisTemplate.opsForValue().setIfAbsent("systemAddWater", 1, 5L,TimeUnit.MINUTES);
        if(systemAddWater != null && !systemAddWater){
            return;
        }
        redisTemplate.opsForValue().setIfAbsent("systemAddWater", 1, 5L,TimeUnit.MINUTES);
        //刷新token
        waterUtils.getToken(true);
        //查询水表列表
        List<Imeter> imeterList = waterUtils.getCustomer(Imeter.class, WaterType.IMETER,null);
        Set<WaterMeter> waterMeterList = new HashSet<>();
        Set<String> meterIds = waterMeterMapper.queryMeterIdAll();
        for (Imeter imeter : imeterList) {
            //添加数据
            WaterMeter newWaterMeter = new WaterMeter(imeter);
            waterMeterList.add(newWaterMeter);
        }
        //查询客户列表 补充数据
        List<Customer> customerList = waterUtils.getCustomer(Customer.class, WaterType.CUSTOMER,null);
        for (Customer customer : customerList) {
            for (CustomerImeter imeter: customer.getImeter()) {
                String meterid = imeter.getMeterid();
                WaterMeter waterMeter = waterMeterList.stream().filter(i -> i.getMeterid().equals(meterid)).findAny().orElse(null);
                if(waterMeter == null){
                    WaterMeter newWaterMeter = new WaterMeter(imeter,customer);
                    waterMeterList.add(newWaterMeter);
                }else{
                    waterMeter.setCustomer(imeter,customer);
                    waterMeterList.add(waterMeter);
                }
            }
            //String meterid = customer.getImeter().getMeterid();
        }
        waterMeterList.forEach(waterMeter -> {
            String meterid = waterMeter.getMeterid();
            Record record = waterUtils.queryRecordOne(meterid);
            waterMeter.setRecord(record);
            saveWaterMeter(meterIds,waterMeter);
        });
        rabbitTemplate.convertAndSend(QueueConstantUtil.WATER_RECORD, new Date());
    }

    @Override
    public Result floorWater(WaterConditionDto waterConditionDto) {
        PageHelper.startPage(waterConditionDto.getPage(), waterConditionDto.getPageSize());
        List<WaterMeterDto> waterMeterDtoList = waterUserMapper.floorWater(waterConditionDto);
        PageInfo pageinfo = new PageInfo<>(waterMeterDtoList);
        waterMeterDtoList.forEach( waterMeterDto1->{
            String address = buildingService.queryAddress(waterMeterDto1.getBuildingId());
            waterMeterDto1.setAddress(address);
        });
        Result defaultTrue = Result.getDefaultTrue();
        defaultTrue.setData(pageinfo);
        return defaultTrue;
    }

    @Override
    public List<Map> queryWaterUser(Set<String> setMeterId) {
        List<Map> maps = waterUserMapper.queryWaterUser(setMeterId);
        maps.forEach(map ->{
            String address = buildingService.queryAddress((Long) map.get("buildingId"));
            map.put("address",address);
        });
        return maps;
    }

    @Override
    public List<Map> queryMeters(final Long id,Integer projectId,Long userId) {
        List<Building> listBuildingALL = buildingService.getBuildings(projectId, userId);
        List<Building> collect = listBuildingALL.stream().filter(building -> building.getParentId().equals(id)).collect(Collectors.toList());
        List<Map> mapList = new LinkedList<>();
        Map map = null;
        Set<Long> longSet = null;
        for (Building building : collect) {
            longSet = new HashSet<>();
            map = new HashMap();
            String name = building.getName();
            Long buildingId = building.getId();
            longSet.add(buildingId);
            dgAddBuidling(longSet,listBuildingALL,buildingId);
            map.put("name",building.getName());
            if(CollectionUtils.isEmpty(longSet)){
                map.put("meters",null);
            }else{
                List<String> stringList = waterMeterMapper.queryMeterIdByBuilding(longSet);
                map.put("meters",stringList);
            }
            mapList.add(map);
        }
        return mapList;
    }

    @Override
    public List<FloorWaterExcel> floorWaterExcel(WaterConditionDto waterConditionDto) {
        List<FloorWaterExcel> floorWaterExcels = waterMeterMapper.floorWaterExcel(waterConditionDto);
        floorWaterExcels.forEach( floorWaterExcel -> {
            Long buildingId = floorWaterExcel.getBuildingId();
            String address = buildingService.queryAddress(buildingId);
            floorWaterExcel.setAddress(address);
        });
        return floorWaterExcels;
    }

    @Override
    public List<InformationExcel> informationExcel(WaterConditionDto waterConditionDto) {
        List<InformationExcel> informationExcelList = waterMeterMapper.informationExcel(waterConditionDto);
        informationExcelList.forEach(informationExcel -> {
            Long buildingId = informationExcel.getBuildingId();
            String address = buildingService.queryAddress(buildingId);
            informationExcel.setAddress(address);
        });
        return informationExcelList;
    }

    @Override
    public Result refresh(Integer projectId) {
        List<WaterMeter> meterIds = waterMeterMapper.queryMeterIdAllByProjectId(projectId);
        meterIds.forEach(waterMeter -> {
            String meterid = waterMeter.getMeterid();
            Record record = waterUtils.queryRecordOne(meterid);
            if(record != null){
                waterMeter.setRecord(record);
                waterMeterMapper.updateByPrimaryKeySelective(waterMeter);
            }
        });
        return Result.getDefaultTrue();
    }

    @Override
    public void queryMetersByBuildingIds(List<Map> listMap) {
        for (Map map : listMap) {
           List<Long> ids= (List<Long>) map.get("ids");
           List<String> meters = waterUserMapper.queryMetersByBuildingIds(ids);
           map.put("meters",meters);
        }
    }

    @Override
    public Result queryBuilding(Long id, Long mainUserId, Long userId) {
        List<Map> list = waterUserMapper.queryBuilding(id,mainUserId);
        Map map = new HashMap();
        map.put("list", list);
        map.put("address", buildingService.queryAddress(id));
        //defaultTrue.setData(map);
        return Result.OK(map);
    }

    public void dgAddBuidling(Set<Long> longSet,List<Building> listBuildingALL,Long id){
        for (Building building: listBuildingALL) {
            if(building.getParentId().equals(id)){
                longSet.add(building.getId());
                dgAddBuidling(longSet,listBuildingALL,building.getId());
            }
        }
    }




    private void saveWaterMeter(Set<String> meterIds, WaterMeter newWaterMeter) {
        if(!meterIds.contains(newWaterMeter.getMeterid())){
            waterMeterMapper.insertSelective(newWaterMeter);
        }else{
            //否则修改数据
            waterMeterMapper.updateByPrimaryKeySelective(newWaterMeter);
        }
    }
}
