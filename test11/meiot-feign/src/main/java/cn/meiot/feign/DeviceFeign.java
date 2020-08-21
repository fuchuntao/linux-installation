package cn.meiot.feign;

import cn.meiot.dto.PasswordDto;
import cn.meiot.entity.vo.*;
import cn.meiot.feign.hystrix.DeviceFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(value = "meiot-device",fallback = DeviceFeignHystrix.class)
public interface DeviceFeign {



    /**
     * 根据用户id查询绑定设备数量
     * @return
     */
    @GetMapping("/equipmentUser/queryBindNum")
    int queryBindNum(@RequestParam("userId") Long userId);

    @PostMapping("pc/role/delete")
    Result delete(@RequestParam("roleId") Integer roleId);


    /**
     * 通过设备号查询用户id
     * @param serialNumber
     * @return Result   data字段为List<String>集合，存储绑定此账户的所有账户id，第一个为主账户id
     */
    @RequestMapping(value = "/equipmentUser/getRtuserIdBySerialNumber", method = RequestMethod.GET)
    Result getRtuserIdBySerialNumber(@RequestParam("serialNumber") String serialNumber);

    /**
     * 通过用户id和设备号查询主账号id
     * @param serialNumber  设备序列号
     * @return Result   data字段为Long类型，存储用户id
     */
    @RequestMapping(value = "/equipmentUser/getRtuserIdByUserId", method = RequestMethod.GET)
    Result getRtuserIdByUserId(@RequestParam("serialNumber") String serialNumber);

    /**
     * 根据设备号获取主开关编号
     * @param serialNumber
     * @return   Result     data直接存放Long类型的主开关编号
     */
    @RequestMapping(value = "/switch/getMasterIndex",method = RequestMethod.GET)
    Result getMasterIndex(@RequestParam("serialNumber") String serialNumber);

    /**
     *
     * @Title: getQueryCreateTime
     * @Description: 获取设备第一次上传时间
     * @param serialNumber
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    @RequestMapping(value = "/equipment/queryCreateTime",method = RequestMethod.GET)
    Map<String,Object> getQueryCreateTime(@RequestParam("serialNumber") String serialNumber);


    /**
     *
     * @Title: getQueryCreateTime
     * @Description: 根据设备号获取用户类型
     * @param serialNumber
     * @return: data 直接存放用户类型 0：个人，1：企业
     */
    @RequestMapping(value = "/equipmentUser/getProjectIdBySerialNumber",method = RequestMethod.GET)
    Integer getRtuserTypeBySerialNumber(@RequestParam("serialNumber") String serialNumber);

    /**
     * 通过设备号查询主开关编号
     * @param serialNumbers
     * @return
     */
    @RequestMapping(value = "/switch/queryMasterIndexBySerialNUmber",method = RequestMethod.POST)
    List<SerialNumberMasterVo> queryMasterIndexBySerialNUmber(@RequestBody List<String> serialNumbers);

    /**
     * 通过项目id查询所有的设备号
     * @param projectId 项目id
     * @return
     */
    @RequestMapping(value = "pc/equipmentUser/getSerialNUmbers",method = RequestMethod.GET)
    List<String> getSerialNUmbersByProjectId(@RequestParam("projectId") Integer projectId);

    /**
     *
     * @Title: querySwitch
     * @Description: 根据项目ID获取开关类型对应的开关号
     * @param projectId
     * @return: java.util.List<cn.meiot.entity.vo.SwitchTypeVo>
     */
    @RequestMapping(value = "pc/switchType/querySwitch", method = RequestMethod.GET)
    List<SwitchTypeVo> querySwitch(@RequestParam("projectId") Integer projectId);

    /**
     * 查询设备的总数量
     * @return
     */
    @RequestMapping(value = "pc/equipmentUser/queryDeviceTotal",method = RequestMethod.GET)
    Integer queryDeviceTotal(@RequestParam("projectId") Integer projectId);


    /**
     * 根据设备号获取主开关编号Sn
     * @param serialNumber
     * @return   Result     data直接存放Long类型的主开关编号
     */
    @RequestMapping(value = "/api/queryMasterSn",method = RequestMethod.GET)
    Long getMasterSn(@RequestParam("serialNumber") String serialNumber);

    /**
     * 获取当前用户所有用的宋所有设备以及主账号id
     * @param userId
     */
    @GetMapping(value = "/api/querySerialAndMaster")
     List<PersonalSerialVo> querySerialAndMaster(@RequestParam("userId") Long userId);

    /**
     *
     * @Title: queryUseTime
     * @Description: 基本信息
     * @param userId
     * @param projectId
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     */
    @GetMapping(value = "api/queryUseTime")
    Map<String,Object> queryUseTime(@RequestParam("userId") Long userId,@RequestParam("projectId") Integer projectId);




    /**
     *
     * @Title: queryWaterUser
     * @Description: 通过水表编号查询楼层水表名称
     * @param setMeterId
     * @return: java.util.List<java.util.Map>
     */
    @RequestMapping("pc/water/queryWaterUser")
    Map<String, WaterAddressVo> queryWaterUser(final @RequestParam("setMeterId") Set<String> setMeterId);

    /**
     * 通过设备号查询 华为账号密码
     */
    @RequestMapping("api/passwordDto")
    PasswordDto getPasswordDto(@RequestParam("serialNumber")String serialNumber);


    /**
     *
     * @Title: querySubBuildingAndMetersByBuildingId
     * @Description: 根据组织架构获取水表编号
     * @param buildingId 组织架构id
     * @param projectId  项目id
     * @param mainUserId 主用户id
     * @return: java.util.List<java.util.Map>
     */
    @RequestMapping("api/querySubBuilding")
    List<Map> querySubBuildingAndMetersByBuildingId(@RequestParam("buildingId") Long buildingId,
                                                    @RequestParam("projectId")Integer projectId,
                                                    @RequestParam("mainUserId") Long mainUserId);



    /**
     *
     * @Title: querySubBuildingWaterId
     * @Description: 根据组织架构获取水表编号
     * @param id 组织架构id
     * @param projectId  项目id
     * @param mainUserId 主用户id
     * @return: java.util.List<java.util.Map>
     */
    @RequestMapping("api/querySubBuildingWaterId")
    List<String> querySubBuildingWaterId(@RequestParam("buildingId") Long id,
                                         @RequestParam("projectId")Integer projectId,
                                         @RequestParam("mainUserId") Long mainUserId);




    /**
     * 通过华为id查询设备id
     */
    @RequestMapping("api/querySerialNumber")
    String querySerialNumber(@RequestParam("deviceId")String deviceId );
}
