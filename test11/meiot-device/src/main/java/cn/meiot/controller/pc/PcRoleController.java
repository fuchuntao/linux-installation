package cn.meiot.controller.pc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.meiot.aop.Log;
import cn.meiot.entity.db.RoleBuilding;
import cn.meiot.entity.dto.pc.BuildingRecursionDto;
import cn.meiot.entity.dto.pc.BuildingRespDto;
import cn.meiot.entity.dto.pc.role.RoleDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.BuildingService;
import cn.meiot.service.RoleService;

@RestController
@RequestMapping("pc/role")
public class PcRoleController extends PcBaseController{
	
	@Autowired
    private RoleService roleService;
	@Autowired
	private BuildingService buildingService;

    /**
    * [新增]
    * @author lzy
    * @date 2019/10/10
    **/
    @PostMapping("insert")
    @Log(operateContent = "")
    public Result insert(@RequestBody RoleDto roleDto){
        return roleService.insert(roleDto);
    }

    /**
    * [刪除]
    * @author lzy
    * @date 2019/10/10
    **/
    @PostMapping("delete")
    public Result delete(Integer roleId){
    	if(roleId == null) {
    		return Result.getDefaultFalse();
    	}
    	RoleBuilding roleBuilding = new RoleBuilding();
    	roleBuilding.setRoleId(roleId);
        return roleService.delete(roleBuilding);
    }

    /**
    * [更新]
    * @author lzy
    * @date 2019/10/10
    **/
    @PostMapping("update")
    @Log(operateContent = "修改区域管理",operateModule="设备服务")
    public Result update(@RequestBody RoleDto roleDto){
        return roleService.update(roleDto);
    }
    
    /**
     * [更新]
     * @author lzy
     * @date 2019/10/10
     **/
     @PostMapping("batchUpdate")
     @Log(operateContent = "批量修改区域管理",operateModule="设备服务")
     public Result batchUpdate(@RequestBody List<RoleDto> roleDto){
         return roleService.batchUpdate(roleDto);
     }

    /**
    * [查詢] 根據主鍵 id 查詢
    * @author lzy
    * @date 2019/10/10
    **/
    @GetMapping("query")
    @Log(operateContent = "区域管理弹窗",operateModule="设备服务")
    public Result load(BuildingRespDto buildingRespDto){
    	buildingRespDto.setCurrentUserId(getMainUserId());
    	buildingRespDto.setUserId(getMainUserId());
    	List<BuildingRecursionDto> queryRecursion = buildingService.queryRecursion(buildingRespDto);
    	buildingRespDto.setType(0);
    	List<Long> listBuilding = roleService.load(buildingRespDto);
    	Map<String,Object> map = new HashMap<String, Object>();
    	map.put("building", queryRecursion);
    	map.put("status", listBuilding);
    	Result defaultTrue = Result.getDefaultTrue();
    	defaultTrue.setData(map);
        return defaultTrue;
    }

    @GetMapping("querySerialByRoleId")
    public List<String> querySerialByRoleId(Integer roleId){
    	return roleService.querySerialByRoleId(roleId);
    }
    
    @GetMapping("queryRoleIdBySerial")
    public List<Integer> queryRoleIdBySerial(String serialNumber){
    	return roleService.queryRoleIdBySerial(serialNumber);
    }
}
