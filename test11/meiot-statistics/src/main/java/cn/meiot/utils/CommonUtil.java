package cn.meiot.utils;

import cn.meiot.entity.AppMeterMonths;
import cn.meiot.entity.AppMeterYears;
import cn.meiot.entity.PcMeterMonths;
import cn.meiot.entity.PcMeterYears;
import cn.meiot.entity.vo.AppMeterMonthsVo;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.mapper.AppMeterHoursMapper;
import cn.meiot.mapper.PcLeakageMonthsMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CommonUtil {

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PcLeakageMonthsMapper pcLeakageMonthsMapper;

    @Autowired
    private AppMeterHoursMapper appMeterHoursMapper;


    /**
     * 通过设备号获取所有的用户id
     * @param serialNumber
     * @return
     */
    private List<String> getAllUserBySerialNumber(String serialNumber){

        //从缓存中拿取数据
        String hash = RedisConstantUtil.SERIAL_NUMBER_USER_ID;
        String key = serialNumber;
        log.info("可以：{}，哈市Key：{}",hash,key);
        Object rtUserIds =  redisTemplate.opsForHash().get(hash,key);//redisUtil.getHashValueByKey(hash, key);
        log.info("缓存中获取到的数据：{}",rtUserIds);
        if(null == rtUserIds){
            Result result = deviceFeign.getRtuserIdBySerialNumber(serialNumber);
            if(!result.isResult()){
                log.info("获取失败，原因：{}",result.getMsg());
                return null;
            }
            List<String> list = (List<String>) result.getData();
            log.info("数据是通过远程调用获取");
            return list;
        }
        log.info("从缓存中拿到了数据：{}",rtUserIds);
        List<String> ids = (List<String>) rtUserIds;
        return ids;
    }
    public static void main(String[] args) {
		System.out.println(1);
	}

    /**
     * 通过设备号查询用户id
     * @param serialNumber
     * @return
     */
    public Long getRtUserIdBySerialNumber(String serialNumber){
        List<String> list = getAllUserBySerialNumber(serialNumber);
        if(null == list || list.size() == 0){
            log.info("未找到{}设备下的用户列表！！！！",serialNumber);
            return null;
        }
        return Long.valueOf(list.get(0));


    }

    /**
     * 通过设备号与子账户id查询主账号id
     * @param userId 子账户id
     * @param serialNumber  设备序列号
     * @return
     */
    public Long getRtUserIdByUserId(Long userId,String serialNumber){
        log.info("查询用户列表开始");
        List<String> list = getAllUserBySerialNumber(serialNumber);
        log.info("查询到的用户列表为：{}",list);
        if(null == list || list.size() == 0){
            log.info("未找到{}设备下的用户列表！！！！",serialNumber);
            return null;
        }
        for(String id :list){
            if(id.equals(userId.toString())){
                return Long.valueOf(list.get(0));
            }
        }
        log.info("越权操作");
        return null ;

    }

    /**
     * 通过设备号获取主开关编号
     * @param serialNumber 设备序列号
     * @return
     */
    public Integer getMasterIndex(String serialNumber) {
        String hashKey = RedisConstantUtil.DEVICE_MASTER_INDEX;
        //key值
        //Object  value= redisUtil.getHashValueByKey(hashKey, serialNumber);
        Object value = redisTemplate.opsForHash().get(hashKey, serialNumber);
        log.info("返回结果：{}",value);
        if(null == value){
            log.info("缓存中未拿到数据，设备中心获取");
            //远程调用获取数据
            Result result = deviceFeign.getMasterIndex(serialNumber);
            if(!result.isResult() || null == result.getData()){
//                log.info("设备号：{} 未查询到主开关信息！",serialNumber);
                return null;
            }
            Integer masterIndex = Integer.valueOf(result.getData().toString()) ;
            return masterIndex;
        }

        return Integer.valueOf(value.toString());

    }

    /**
     * 通过设备号查询是企业还是用户
     * @param serialNumber
     * @return
     */
    public Integer getRtUserTypeBySerialNumber(String serialNumber){
        //拉取失败从设备服务远程调用请求
        Integer type = deviceFeign.getRtuserTypeBySerialNumber(serialNumber);
        log.info("查询用户类型数据是通过远程调用获取：{}",type);
        return type;
    }



    /**
     * 通过设备号获取主开关编号sn
     * @param serialNumber 设备序列号
     * @return
     */
    public Long getMasterSn(String serialNumber) {
        String hashKey = RedisConstantUtil.DEVICE_MASTER_SN;
        //key值
        //Object  value= redisUtil.getHashValueByKey(hashKey, serialNumber);
        Object value = redisTemplate.opsForHash().get(hashKey, serialNumber);
        log.info("通过设备号获取主开关编号sn返回结果：{}",value);
        if(null == value){
            log.info("通过设备号获取主开关编号sn缓存中未拿到数据，设备中心获取");
            //远程调用获取数据
            Long masterSn = deviceFeign.getMasterSn(serialNumber);
            if(masterSn == null){
                log.info("设备号：{} 未查询到主开关信息！",serialNumber);
                return null;
            }
            return masterSn;
        }

        return Long.valueOf(value.toString());

    }


    /**
     *
     * @Title: updateStatistChangeSwitch
     * @Description:  更换开关修改统计电量，电流，温度
     * @param parametersDto
     * @return: void
     */
    public void updateStatistChangeSwitch(ParametersDto parametersDto){
        try{
            if(parametersDto != null ) {
                List<Long> longList = pcLeakageMonthsMapper.selectListSerialNumber(parametersDto);
                log.info("更换开关统计:{},查询表:{},表的设备号：{}",parametersDto.getTableName(),
                        parametersDto.getPlatform()+"_"+parametersDto.getTableName()+"_"+parametersDto.getTimeName(), longList);
                if(!CollectionUtils.isEmpty(longList)) {
                    int i = pcLeakageMonthsMapper.updateStatistics(parametersDto);
                    log.info("更换开关统计条数：{}", i);

                }
                log.info("更换开关统计服务parametersDto：{}", parametersDto);
            }
        }catch (Exception e){
            log.error("更换开关修改统计电量，电流，温度:{}", parametersDto);
            log.error("错误信息：{},======[]{},=========[]{}",e.getMessage(),e.getCause(),e.getSuppressed());
            log.error("更换开关统计发生错误：{}",e.getStackTrace());
        }

    }

    /**
     *
     * @Title: updateStatistChangeSwitch
     * @Description:  转换类
     * @param parametersDto
     * @return: void
     */
    public List<AppMeterMonths> appMeterMonthsVoToAppMeterMonths(ParametersDto parametersDto,
                                                    List<AppMeterMonthsVo> appMeterMonthsVos){
        List<AppMeterMonths> list = new ArrayList<>();
        //查月表里面有没有数据
        if(!CollectionUtils.isEmpty(appMeterMonthsVos)) {
            List<AppMeterMonthsVo> appMeterMonthsVoList = appMeterHoursMapper.selectListByMonths(parametersDto);
            if(!CollectionUtils.isEmpty(appMeterMonthsVoList)) {
                appMeterMonthsVos.removeAll(appMeterMonthsVoList);
            }

            for(AppMeterMonthsVo appMeterMonthsVo : appMeterMonthsVos) {
                AppMeterMonths  appMeterMonths = new AppMeterMonths();
                BeanUtils.copyProperties(appMeterMonthsVo,appMeterMonths);
                list.add(appMeterMonths);
            }
        }
        return list;
    }


    public List<AppMeterYears> appMeterYearsVoToAppMeterMonths(ParametersDto parametersDto,
                                                                List<AppMeterMonthsVo> appMeterMonthsVos){
        List<AppMeterYears> list = new ArrayList<>();
        //查月表里面有没有数据
        if(!CollectionUtils.isEmpty(appMeterMonthsVos)) {
            List<AppMeterMonthsVo> appMeterMonthsVoList = appMeterHoursMapper.selectListByMonths(parametersDto);
            if(!CollectionUtils.isEmpty(appMeterMonthsVoList)) {
                appMeterMonthsVos.removeAll(appMeterMonthsVoList);
            }

            for(AppMeterMonthsVo appMeterMonthsVo : appMeterMonthsVos) {
                AppMeterYears  appMeterYears = new AppMeterYears();
                BeanUtils.copyProperties(appMeterMonthsVo,appMeterYears);
                list.add(appMeterYears);
            }
        }
        return list;
    }





    public List<PcMeterMonths> appMeterMonthsVoToPcMeterMonths(ParametersDto parametersDto,
                                                               List<AppMeterMonthsVo> appMeterMonthsVos){
        List<PcMeterMonths> list = new ArrayList<>();
        //查月表里面有没有数据
        if(!CollectionUtils.isEmpty(appMeterMonthsVos)) {
            List<AppMeterMonthsVo> appMeterMonthsVoList = appMeterHoursMapper.selectListByMonths(parametersDto);
            if(!CollectionUtils.isEmpty(appMeterMonthsVoList)) {
                appMeterMonthsVos.removeAll(appMeterMonthsVoList);
            }

            for(AppMeterMonthsVo appMeterMonthsVo : appMeterMonthsVos) {
                PcMeterMonths  pcMeterMonths = new PcMeterMonths();
                BeanUtils.copyProperties(appMeterMonthsVo,pcMeterMonths);
                list.add(pcMeterMonths);
            }
        }
        return list;
    }


    public List<PcMeterYears> appMeterMonthsVoToPcMeterYears(ParametersDto parametersDto,
                                                               List<AppMeterMonthsVo> appMeterMonthsVos){
        List<PcMeterYears> list = new ArrayList<>();
        //查月表里面有没有数据
        if(!CollectionUtils.isEmpty(appMeterMonthsVos)) {
            List<AppMeterMonthsVo> appMeterMonthsVoList = appMeterHoursMapper.selectListByMonths(parametersDto);
            if(!CollectionUtils.isEmpty(appMeterMonthsVoList)) {
                appMeterMonthsVos.removeAll(appMeterMonthsVoList);
            }

            for(AppMeterMonthsVo appMeterMonthsVo : appMeterMonthsVos) {
                PcMeterYears  pcMeterYears = new PcMeterYears();
                BeanUtils.copyProperties(appMeterMonthsVo,pcMeterYears);
                list.add(pcMeterYears);
            }
        }
        return list;
    }


    /**
     *
     * @Title: commonMeterUpdate
     * @Description: 电量上传查询修改添加电量
     * @param parametersDto
     * @param dataMeter
     * @return: int
     */
    public int commonMeterUpdate(ParametersDto parametersDto, BigDecimal dataMeter) {
        int i = 0;
        Map<String, Object> mapNewDay = appMeterHoursMapper.selectByOne(parametersDto);
        log.info("查询表:{},数据为:{}",parametersDto.getPlatform()+"_"+
                parametersDto.getTableName()+"_"+parametersDto.getTimeName(),mapNewDay);
        if(mapNewDay != null) {
            Long id = (Long) mapNewDay.get("id");
            Object data = mapNewDay.get("data");
            if(data != null && mapNewDay.get("id") != null) {
                BigDecimal decimal = (BigDecimal) data;
                if(decimal != null && dataMeter != null && decimal.compareTo(dataMeter) != 0) {
                    //修改数据
                    parametersDto.setId(id);
                    parametersDto.setData(dataMeter);
                    log.info("执行了修改操作");
                    i = appMeterHoursMapper.updateByone(parametersDto);
                    log.info("修改表:{} 数据为:{}",parametersDto.getPlatform()+"_"+
                            parametersDto.getTableName()+"_"+parametersDto.getTimeName(),parametersDto);
                    log.info("根据id:{},修改的当天电量：{}",id,dataMeter);
                }
            }
        }else {
            parametersDto.setData(dataMeter);
            log.info("执行了插入操作");
            i = appMeterHoursMapper.insertByOne(parametersDto);
            log.info("插入表:{},数据为:{}",parametersDto.getPlatform()+"_"+
                    parametersDto.getTableName()+"_"+parametersDto.getTimeName(),parametersDto);

        }
        return i;

    }






}
