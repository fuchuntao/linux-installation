package cn.meiot.controller;


import cn.meiot.constart.TableConstart;
import cn.meiot.dao.PowerMapper;
import cn.meiot.entity.db.Power;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.EquipmentUserService;
import cn.meiot.service.PowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("power")
public class PowerController extends BaseController{

    @Autowired
    private PowerService powerService;

    @Autowired
    private PowerMapper powerMapper;

    @Autowired
    private EquipmentUserService equipmentUserService;

    /**
     * 添加功率
     * @return
     */
    @PostMapping("insert")
    public Result insert(@RequestBody Power power) {
        //获取设备及开关列表
        String serialNumber = power.getSerialNumber();
        Long userId = getUserId();
        equipmentUserService.authentication(serialNumber,userId);
        power.setUserId(userId);
        Result result = powerService.insert(power);
        return result;
    }

    /**
     * 添加功率
     * @return
     */
    @PostMapping("isSwitch")
    public Result isSwitch(@RequestBody Power power) {
        //获取设备及开关列表
        Result result = powerService.isSwitch(power);
        return result;
    }

    /**
     * 修改功率
     * @return
     */
    @PostMapping("update")
    public Result update(@RequestBody Power power) {
        //获取设备及开关列表
        String serialNumber = power.getSerialNumber();
        Long userId = getUserId();
        equipmentUserService.authentication(serialNumber,userId);
        //powerService.queryAdminUser(serialNumber,userId);
        power.setUserId(userId);
        Result result = powerService.update(power);
        return result;
    }

    /**
     * 删除功率
     * @return
     */
    @PostMapping("delete")
    public Result delete(@RequestBody Power power) {
        Power power1 = powerMapper.selectByPrimaryKey(power.getId());
        Long userId = getUserId();
        equipmentUserService.authentication(power1.getSerialNumber(),userId);
        //powerService.queryAdminUser(power1.getSerialNumber(),userId);
        power.setUserId(userId);
        Result result = powerService.delete(power);
        return result;
    }

  /**
     * 查询开关功率列表
     * @return
     */
    @GetMapping("queryBySn")
    public Result query(String switchSn) {
        Long userId = getUserId();
        Result result = powerService.query(switchSn,userId);
        return result;
    }

    /**
     * 查询单个功率内的开关信息
     * @return
     */
    @GetMapping("queryById")
    public Result queryById(Integer id,String serialNumber) {
        Long userId = getUserId();
        List<Map> result = powerService.queryById(id,userId,serialNumber,TableConstart.POWER);
        Result defaultTrue = Result.getDefaultTrue();
        defaultTrue.setData(result);
        return defaultTrue;
    }
}
