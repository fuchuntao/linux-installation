package cn.meiot.feign;

import cn.meiot.entity.vo.DeviceVersionVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.feign.impl.DeviceFeignHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 设备服务
 */

@FeignClient(value = "meiot-device",fallback = DeviceFeignHystrix.class)
public interface DeviceFeign {

    @RequestMapping(value = "",method = RequestMethod.GET)
    Result findUserIdByDevice(String device);

    /**
     * 通过设备序列号查询绑定的用户id
     * @param serialNumber
     * @return
     */
    @RequestMapping(value = "getRtuserIdBySerialNumber", method = RequestMethod.GET)
    Result getRtuserIdBySerialNumber(@RequestParam("serialNumber") String serialNumber);

    /**
     * 通过设备id获取项目ID
     * @param serialNumber
     * @return
     */
    @RequestMapping(value = "equipmentUser/getProjectIdBySerialNumber")
    Integer getProjectIdBySerialNumber(@RequestParam("serialNumber") String serialNumber);

    /**
     * 通过设备号获取地址
     */
    @RequestMapping(value = "pc/building/queryAddress")
    String getAddressBySerialNumber(@RequestParam("serialNumber") String serialNumber);


    /**
     * 通过设备名称查询设备序列号集合
     */
    @RequestMapping(value = "equipmentUser/getSerialNumberByName")
    List<String> getSerialNumberListByName(@RequestParam("name") String name);

    /**
     * 通过角色查询设备
     * @param roleId
     * @return
     */
    @GetMapping("pc/role/querySerialByRoleId")
    List<String> querySerialByRoleId(@RequestParam("roleId") Integer roleId);

    /**
     * 根据设备查询角色
     * @param serialNumber
     * @return
     */
    @GetMapping("pc/role/queryRoleIdBySerial")
    List<Integer> queryRoleIdBySerial(@RequestParam("serialNumber") String serialNumber);

    /**
     * 根据设备号查询地址ID
     * @param serialNumber
     * @return
     */
    @GetMapping("pc/equipment/queryAddressIdBySerialNumber")
    Long queryAddressIdBySerialNumber(@RequestParam("serialNumber") String serialNumber);

    /**
     * 通过项目ID查询总设备数
     * @param projectId
     * @return
     */
    @RequestMapping(value = "pc/equipmentUser/queryDeviceTotal",method = RequestMethod.GET)
    Integer queryDeviceTotal(@RequestParam(value = "projectId", defaultValue = "0") Integer projectId) ;

    /**
     * 根据用户ID和项目ID获取设备列表
     * @param userId
     * @param projectId
     * @return
     */
    @GetMapping("")
    List<DeviceVersionVo> getDeviceVersionList(@RequestParam("userId")Long userId,
                                                @RequestParam("projectId") Integer projectId);
}
