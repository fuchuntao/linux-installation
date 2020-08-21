package cn.meiot.controller;

import java.util.List;
import java.util.Map;

import cn.meiot.aop.UpgradeDetection;
import cn.meiot.constart.TableConstart;
import cn.meiot.service.EquipmentUserService;
import cn.meiot.service.PowerService;
import cn.meiot.service.SwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.meiot.aop.Log;
import cn.meiot.entity.db.TimerMode;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.TimerModeService;

/**
 * @author lingzhiying
 * @title: TimerModeController.java
 * @projectName spacepm
 * @description:  
 * @date 2019年9月2日
 */
@RestController
@RequestMapping("timerMode")
public class TimerModeController extends BaseController{

	@Autowired
	private TimerModeService timerModeService;

	@Autowired
    private PowerService powerService;

    @Autowired
    private EquipmentUserService equipmentUserService;

    @Autowired
    private SwitchService switchService;

	/**
	 * 查询开关定时信息
	 * @param switchSn
	 * @return
	 */
	@GetMapping("querySn")
	@Log(operateContent = "App查询开关定时信息")
	public Result querySn(String switchSn) {
		Long userId = getUserId();
        switchService.authentication(userId,switchSn);
		return timerModeService.querySn(switchSn);
	}

	/**
    * [新增]
    * @author 
    * @date 2019/09/04
    **/
    @PostMapping("insert")
    @Log(operateContent = "App添加定时信息")
    @UpgradeDetection
    public Result insert(@RequestBody TimerMode timerMode){
        Long userId = getUserId();
        String serialNumber = timerMode.getSerialNumber();
        equipmentUserService.authentication(serialNumber,userId);
        return timerModeService.insert(timerMode);
    }

    /**
     * @author
     * @date 2019/09/04
     **/
    @PostMapping("isSwitch")
    @Log(operateContent = "App添加定时信息")
    @UpgradeDetection
    public Result isSwitch(@RequestBody TimerMode timerMode){
        return timerModeService.isSwitch(timerMode);
    }

    /**
    * [刪除]
    * @author 
    * @date 2019/09/04
    **/
    @PostMapping("delete")
    @Log(operateContent = "App删除定时信息")
    @UpgradeDetection
    public Result delete(@RequestBody Map map){
    	Long id = new Long(map.get("id").toString());
        Long userId = getUserId();
        return timerModeService.delete(id,userId);
    }

    /**
    * [更新]
    * @author 
    * @date 2019/09/04
    **/
    @PostMapping("update")
    @Log(operateContent = "App修改定时信息")
    @UpgradeDetection
    public Result update(@RequestBody TimerMode timerMode){
        Long userId = getUserId();
        String serialNumber = timerMode.getSerialNumber();
        equipmentUserService.authentication(serialNumber,userId);
        return timerModeService.update(timerMode);
    }

    /**
    * [查詢] 根據主鍵 id 查詢
    * @author 
    * @date 2019/09/04
    **/
    @GetMapping("query")
    @Log(operateContent = "App查询定时信息")
    public Result load(Long id){
        return timerModeService.load(id);
    }

    /**
     * 查询单个功率内的开关信息
     * @return
     */
    @GetMapping("queryById")
    public Result queryById(Integer id,String serialNumber) {
        Long userId = getUserId();
        equipmentUserService.authentication(serialNumber,userId);
        List<Map> result = powerService.queryById(id,userId,serialNumber, TableConstart.TIME_MODE);
        Result defaultTrue = Result.getDefaultTrue();
        defaultTrue.setData(result);
        return defaultTrue;
    }

    /**
     * @author
     * @date 2019/09/04
     **/
    @GetMapping("querySwitchSn")
    @Log(operateContent = "App查询定时信息")
    public Result querySwitchSn(String switchSn){
        Long userId = getUserId();
        switchService.authentication(userId,switchSn);
        return timerModeService.querySwitchSn(switchSn,userId);
    }

    /**
     * 根据设备查询定时信息
     * @author
     * @date 2019/09/04
     **/
    @GetMapping("querySerial")
    @Log(operateContent = "App查询定时信息")
    public Result querySerial(String serialNumber){
        Long userId = getUserId();
        equipmentUserService.authentication(serialNumber,userId);
        return timerModeService.querySerial(serialNumber,userId);
    }
}
