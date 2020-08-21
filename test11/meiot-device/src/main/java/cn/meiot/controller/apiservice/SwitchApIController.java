package cn.meiot.controller.apiservice;


import cn.meiot.entity.dto.apiservice.SerialDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.apiservice.EquipmentApiService;
import cn.meiot.service.apiservice.SwitchApIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api-service/switch")
public class SwitchApIController extends IBaseOpenApiController{

    @Autowired
    private SwitchApIService apISwitch;

    @Autowired
    private EquipmentApiService equipmentApiService;

    @GetMapping("listSwitchSn")
    public Result listSwitchSn(String serialNumber){
        Long appId = getAppId();
        equipmentApiService.authentication(serialNumber,appId);
        List list = apISwitch.listSwitchSn(serialNumber);
        return Result.OK(list);
    }

    @PostMapping("switch")
    public Result sendSwitch(@RequestBody SerialDto serialDto){
        if(CollectionUtils.isEmpty(serialDto.getSwitchApiList())){
            return Result.faild("");
        }
        String serialNumber = serialDto.getSerialNumber();
        Long appId = getAppId();
        equipmentApiService.authentication(serialNumber,appId);
        return apISwitch.sendSwitch(serialDto);
    }
}
