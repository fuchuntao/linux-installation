package cn.meiot.feign.hystrix;

import cn.meiot.dto.PasswordDto;
import cn.meiot.entity.vo.*;
import cn.meiot.feign.DeviceFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Service
@Slf4j
public class DeviceFeignHystrix implements DeviceFeign {
    @Override
    public int queryBindNum(Long userId) {
        log.info("获取当前用户{}设备号：连接设备服务超时",userId);
        return 0;
    }

    @Override
    public Result delete(Integer roleId) {
        return new Result().Faild("超时");
    }

    @Override
    public Result getRtuserIdBySerialNumber(String serialNumber) {
        Result result = Result.getDefaultFalse();
        result.setMsg("超时");
        return result;
    }

    @Override
    public Result getRtuserIdByUserId( String serialNumber) {
        Result result = Result.getDefaultFalse();
        result.setMsg("超时");
        return result;
    }

    @Override
    public Result getMasterIndex(String serialNumber) {
        Result result = Result.getDefaultFalse();
        result.setMsg("超时");
        return result;
    }

    @Override
    public Map<String,Object> getQueryCreateTime(String serialNumber) {
        log.info("获取设备服务设备第一次上传时间错误！");
        return null;
    }


    /**
     *
     * @Title: getRtuserTypeBySerialNumber
     * @Description: 根据设备号获取用户类型
     * @param serialNumber
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    public Integer getRtuserTypeBySerialNumber(String serialNumber) {
        log.info("根据设备号获取用户类型错误！");
        return null;
    }

    @Override
    public List<SerialNumberMasterVo> queryMasterIndexBySerialNUmber(List<String> serialNumbers) {
        log.info("查询设备号主开关时超时，获取失败！！！！！");
        return null;
    }

    @Override
    public List<String>  getSerialNUmbersByProjectId(Integer projectId) {
        log.info("获取设备超时，失败！！！！！！");

        return null;
    }

    @Override
    public List<SwitchTypeVo> querySwitch(Integer projectId) {
        log.info("根据项目id获取开关类型失败！");
        return null;
    }

    @Override
    public Integer queryDeviceTotal(Integer projectId) {
        log.info("获取设备总数量超时，失败！！！！！！");
        return 0;
    }

    @Override
    public Long getMasterSn(String serialNumber) {
        log.info("根据设备号获取主开关sn超时！！！");
        return null;
    }

    @Override
    public List<PersonalSerialVo> querySerialAndMaster(Long userId) {

        log.info("根据用户id获取主账号以及设备超时，用户id：{}",userId);
        return null;
    }


    @Override
    public Map<String, Object> queryUseTime(Long userId, Integer projectId) {
        log.info("app版本根据用户id获取主账号以及设备号超时，用户id：{}，项目id:{}",userId,projectId);
        return null;
    }


    @Override
    public Map<String, WaterAddressVo> queryWaterUser(Set<String> setMeterId) {
        log.info("通过水表编号查询楼层水表名称超时,setMeterId:{}", setMeterId);
//        Result result = Result.getDefaultFalse();
//        result.setCode(Result.TIMEOUT);
        return null;
    }

    @Override
    public PasswordDto getPasswordDto(String serialNumber) {
        log.info("查询该华为设备账号密码超时:{}", serialNumber);
        return null;
    }

    @Override
    public List<Map> querySubBuildingAndMetersByBuildingId(Long buildingId, Integer projectId, Long mainUserId) {
        log.info("查询根据组织架构获取水表编号超时,组织架构id:{}, 项目id:{},主账号id:{}", buildingId, projectId, mainUserId);
        return null;
    }

    @Override
    public List<String> querySubBuildingWaterId(Long id, Integer projectId, Long mainUserId) {
        log.info("查询根据组织架构获取水表编号超时,组织架构id:{}, 项目id:{},主账号id:{}", id, projectId, mainUserId);
        return  null;
    }
    public String querySerialNumber(String deviceId) {
        return null;
    }
}
