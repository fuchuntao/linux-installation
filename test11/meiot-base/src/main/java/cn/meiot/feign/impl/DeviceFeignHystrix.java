package cn.meiot.feign.impl;

import cn.meiot.entity.vo.DeviceVersionVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.feign.DeviceFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DeviceFeignHystrix implements DeviceFeign {
    @Override
    public Result findUserIdByDevice(String device) {
        Result result = Result.getDefaultFalse();
        result.setMsg("超时");
        return result;
    }

    @Override
    public Result getRtuserIdBySerialNumber(String serialNumber) {
        Result result = Result.getDefaultFalse();
        result.setMsg("超时");
        return result;
    }

    @Override
    public Integer getProjectIdBySerialNumber(String serialNumber) {
        return -1;
    }


    @Override
    public String getAddressBySerialNumber(String serialNumber) {
        log.info("接收的参数为：{}",serialNumber);
        log.info("请求接口失败getAddressBySerialNumber");
        return "--";
    }

    @Override
    public List<String> getSerialNumberListByName(String name) {
        log.info("接收的参数为：{}",name);
        log.info("请求接口失败getSerialNumberListByName");
        return null;
    }

    @Override
    public List<String> querySerialByRoleId(Integer roleId) {
        log.info("通过角色Id获取设备网络调用失败");
        return null;
    }

    @Override
    public List<Integer> queryRoleIdBySerial(String serialNumber) {
        log.info("通过设备号查询角色List网络调用失败");
        return null;
    }

    @Override
    public Long queryAddressIdBySerialNumber(String serialNumber) {
        log.info("通过设备号查询地址ID网络错误");
        return null;
    }

    @Override
    public Integer queryDeviceTotal(Integer projectId) {
        log.info("通过项目ID查询总设备网络出错");
        return null;
    }

    @Override
    public List<DeviceVersionVo> getDeviceVersionList(Long userId, Integer projectId) {
        log.info("通过项目ID和用户ID查询总设备网络出错");
        return null;
    }
}
