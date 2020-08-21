package cn.meiot.controller.pc;


import cn.meiot.aop.Log;
import cn.meiot.entity.db.WaterUser;
import cn.meiot.entity.dto.pc.water.WaterConditionDto;
import cn.meiot.entity.dto.pc.water.WaterMeterDto;
import cn.meiot.entity.excel.FloorWaterExcel;
import cn.meiot.entity.excel.InformationExcel;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.WaterAddressVo;
import cn.meiot.service.WaterMeterService;
import cn.meiot.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("pc/water")
@Slf4j
public class PcWaterMeterControlller extends PcBaseController{

    @Autowired
    private WaterMeterService waterMeterService;

    @PostMapping("refresh")
    @Log(operateContent = "水表信息刷新",operateModule="设备服务")
    public Result refresh(){
        Integer projectId = getProjectId();
        synchronized (projectId) {
            return waterMeterService.refresh(projectId);
        }
    }

    @GetMapping("queryBuilding")
    @Log(operateContent = "根据组织架构查询设备",operateModule="设备服务")
    public Result queryBuilding(Long id) {
        Long mainUserId = getMainUserId();
        Result result = waterMeterService.queryBuilding(id,mainUserId,getUserId());
        return result;
    }

    @PostMapping("insert")
    @Log(operateContent = "添加水表",operateModule="设备服务")
    public Result insertWaterMeter(@RequestBody WaterUser waterUser){
        if(StringUtils.isEmpty(waterUser.getName())){
            return Result.getDefaultFalse();
        }
        Integer projectId = getProjectId();
        Long mainUserId = getMainUserId();
        waterMeterService.waterAuthentication(projectId,mainUserId,waterUser.getMeterId());
        waterUser.setProjectId(projectId);
        waterUser.setUserId(mainUserId);
        return waterMeterService.saveWaterUser(waterUser);
    }

    @PostMapping("update")
    @Log(operateContent = "修改水表",operateModule="设备服务")
    public Result updateWaterMeter(@RequestBody WaterUser waterUser){
        if(StringUtils.isEmpty(waterUser.getName())){
            return Result.getDefaultFalse();
        }
        Integer projectId = getProjectId();
        Long mainUserId = getMainUserId();
        waterUser.setProjectId(projectId);
        waterUser.setUserId(mainUserId);
        return waterMeterService.updateWaterUser(waterUser);
    }

    @PostMapping("delete")
    @Log(operateContent = "删除水表",operateModule="设备服务")
    public Result updateWaterMeter(@RequestBody List<Long> ids){
        Integer projectId = getProjectId();
        Long mainUserId = getMainUserId();
        return waterMeterService.deleteWaterUser(ids,projectId,mainUserId);
    }

    @GetMapping("information")
    @Log(operateContent = "水表信息",operateModule="设备服务")
    public Result information(WaterMeterDto waterMeterDto){
        Integer projectId = getProjectId();
        Long mainUserId = getMainUserId();
        waterMeterDto.setProjectId(projectId);
        waterMeterDto.setUserId(mainUserId);
        return waterMeterService.information(waterMeterDto);
    }

    @GetMapping("informationExcel")
    @Log(operateContent = "水表信息导出",operateModule="设备服务")
    public Result informationExcel(WaterConditionDto waterConditionDto, HttpServletResponse response){
        Integer projectId = getProjectId();
        Long mainUserId = getMainUserId();
        waterConditionDto.setProjectId(projectId);
        waterConditionDto.setUserId(mainUserId);
        List<InformationExcel> informationExcelList = waterMeterService.informationExcel(waterConditionDto);
        ExcelUtils.export(informationExcelList,"水表信息",response, InformationExcel.class);
        return null;
    }

    @GetMapping("floorWater")
    @Log(operateContent = "楼层水表",operateModule="设备服务")
    public Result floorWater(WaterConditionDto waterConditionDto){
        Integer projectId = getProjectId();
        Long mainUserId = getMainUserId();
        waterConditionDto.setProjectId(projectId);
        waterConditionDto.setUserId(mainUserId);
        return waterMeterService.floorWater(waterConditionDto);
    }

    @GetMapping("floorWaterExcel")
    @Log(operateContent = "楼层水表导出",operateModule="设备服务")
    public Result floorWaterExcel(WaterConditionDto waterConditionDto, HttpServletResponse response){
        Integer projectId = getProjectId();
        Long mainUserId = getMainUserId();
        waterConditionDto.setProjectId(projectId);
        waterConditionDto.setUserId(mainUserId);
        List<FloorWaterExcel> floorWaterExcelList = waterMeterService.floorWaterExcel(waterConditionDto);
        ExcelUtils.export(floorWaterExcelList,"楼层水表",response, FloorWaterExcel.class);
        return null;
    }

    @PostMapping("systemAddWater")
    @Log(operateContent = "水表信息",operateModule="设备服务")
    public boolean systemAddWater(){
        new Thread(()-> waterMeterService.systemAddWater()).start();
        return true;
    }

    @GetMapping("queryWaterUser")
    //@Log(operateContent = "水表信息",operateModule="设备服务")
    public Map<String, WaterAddressVo> queryWaterUser(@RequestParam("setMeterId")Set<String> setMeterId){
        log.info("setMeterId:{}",setMeterId);
        if(CollectionUtils.isEmpty(setMeterId)){
            return null;
        }
        List<Map> listmap = waterMeterService.queryWaterUser(setMeterId);
        Map<String, WaterAddressVo> mapData = new HashMap<>();
        WaterAddressVo waterAddressVo =  null;
        for (Map map : listmap) {
            waterAddressVo = new WaterAddressVo ();
            try {
                BeanUtils.populate(waterAddressVo,map);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            String meterid = waterAddressVo.getMeterid();
            mapData.put(meterid,waterAddressVo);
        }
        return mapData;
    }

    /**
     *
     * @param id
     * @return name :横坐标
     *         meters:水表集合
     */
    @GetMapping("queryMeters")
    @Log(operateContent = "水表信息",operateModule="设备服务")
    public List<Map> queryWaterUser(@RequestParam("id") Long id,@RequestParam("projectId") Integer projectId,@RequestParam("userId")Long userId){
        List<Map> listmap = waterMeterService.queryMeters(id,projectId,userId);
        return listmap;
    }
}
