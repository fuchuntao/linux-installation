package cn.meiot.controller.apiservice;

import cn.meiot.dao.EquipmentMapper;
import cn.meiot.entity.db.Equipment;
import cn.meiot.entity.dto.apiservice.SerialDto;
import cn.meiot.entity.dto.pc.examination.BatchExamination;
import cn.meiot.entity.vo.Result;
import cn.meiot.feign.ApplicationFeign;
import cn.meiot.service.EquipmentService;
import cn.meiot.service.EquipmentUserService;
import cn.meiot.service.ExaminationService;
import cn.meiot.service.RedisService;
import cn.meiot.service.apiservice.EquipmentApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api-service/equipment")
public class EquipmentApiController extends IBaseOpenApiController {

    @Autowired
    private EquipmentApiService equipmentApiService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ApplicationFeign applicationFeign;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private ExaminationService examinationService;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Autowired
    private EquipmentUserService equipmentUserService;

    @PostMapping("insertSerial")
    public Result insertSerial(@RequestBody SerialDto serialDto){
        String serialNumber = serialDto.getSerialNumber();
        serialDto.setAppId(getAppId());
        Result result = null;
        redisService.insertSerial(serialNumber);
        Equipment equipment = equipmentMapper.selectByPrimaryKey(serialNumber);
        if(equipment != null ){
            boolean existence = equipmentUserService.isExistence(serialNumber);
            if(existence){
                return Result.faild("");
            }
            result = equipmentApiService.insertSerial(serialDto);
            redisService.removeInsertSerial(serialNumber);
            return result;
        }else{
            return Result.faild("");
        }
    }

    @PostMapping("deleteSerial")
    public Result deleteSerial(@RequestBody SerialDto serialDto){
        serialDto.setAppId(getAppId());
        Result result = equipmentApiService.deleteSerial(serialDto);
        return result;
    }

    /**
     * 查询拥有设备
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("listSerial")
    public Result listSerial(@RequestParam(value = "page",defaultValue = "1")Integer page,
                             @RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize){
        Long appId = getAppId();
        if(page <= 0 ){
            return Result.faild("");
        }
        page=(page-1)*pageSize;
        Result result = equipmentApiService.listSerial(page,pageSize,appId);
        return result;
    }

    /**
     * 批量设置漏电自检
     * @param page
     * @param pageSize
     * @return
     */
    @PostMapping("bathExamination")
    public Result bathExamination(@RequestBody BatchExamination batchExamination){
        Long appId = getAppId();
        List<String> serialNumbers = batchExamination.getSerialNumber();
        String examinationTime = batchExamination.getExaminationTime();
        Integer status = batchExamination.getStatus();
        for (String serialNumber :serialNumbers) {
            equipmentApiService.authentication(serialNumber,appId);
        }
        Result result = equipmentService.batchExamination(serialNumbers, status, examinationTime);
        return result;
    }

    /**
     * 立即漏电自检
     * @param page
     * @param pageSize
     * @return
     */
    @PostMapping("test")
    public Result test(@RequestBody SerialDto serialDto){
        Long appId = getAppId();
        String serialNumber = serialDto.getSerialNumber();
        equipmentApiService.authentication(serialNumber,appId);
        Result result = examinationService.test(serialNumber);
        return result;
    }

}
