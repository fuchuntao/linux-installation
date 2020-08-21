package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.entity.Files;
import cn.meiot.entity.Firmware;
import cn.meiot.entity.WhiteList;
import cn.meiot.entity.vo.AddFirmwareVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.StudentVo;
import cn.meiot.entity.vo.WhiteListExeclVo;
import cn.meiot.exception.MyServiceException;
import cn.meiot.service.IFirmwareService;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.DateUtil;
import cn.meiot.utils.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wuyou
 * @since 2019-11-13
 */
@RestController
@RequestMapping("/firmware")
@Slf4j
@SuppressWarnings("all")
public class FirmwareController {

    @Autowired
    private IFirmwareService firmwareService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("addFirmware")
    @Log(operateModule = "公共服务", operateContent = "添加固件")
    public Result addFirmware(@RequestParam("version") String version,
                              @RequestParam("name") String name,
                              @RequestParam("pushTime") String pushTime,
                              @RequestParam("isUpgrade") Integer isUpgrade,
                              @RequestParam("isNow") Integer isNow,
                              @RequestParam("isList") Integer isList,
                              @RequestParam("aName") String aName,
                              @RequestParam("aAddress") String aAddress,
                              @RequestParam("bName") String bName,
                              @RequestParam("bAddress") String bAddress,
                              @RequestParam(value = "description") String description,
                              @RequestParam(value = "whiteName", required = false) String whiteName,
                              @RequestParam(value = "excel", required = false) MultipartFile excel) {
        Date date = DateUtil.getDate(pushTime);
        if (StringUtils.isEmpty(aAddress)||StringUtils.equals(aAddress,"undefined")) {
            throw new MyServiceException("必须上传文件","500");
        }
        if (StringUtils.isEmpty(bAddress)||StringUtils.equals(aAddress,"undefined")) {
            throw new MyServiceException("必须上传文件","500");
        }
        if (null == date)
            return Result.getDefaultFalse();
        if (DateUtil.compare(date, new Date()) <= 0 && isNow == 1) {
            Result result = Result.getDefaultFalse();
            result.setMsg("预约时间不可以小于当前时间");
            return result;
        }
        if (isList == 1 && excel == null) {
            log.info("{},{}", isList, excel);
            Result result = Result.getDefaultFalse();
            result.setMsg("请上传excel");
            return result;
        }
        Firmware firmware = new Firmware(version, name, DateUtil.SDF.format(date), isUpgrade, isNow, isList, ConstantsUtil.DF.format(new Date()));
        firmware.setDescription(description);
        if (isNow == 0) {
            firmware.setType(1);
        } else {
            firmware.setType(0);
        }
//        firmware.setType(isNow);
        firmware.setWhiteName(whiteName);

        Files a = new Files(aName, aAddress, 0);
        Files b = new Files(bName, bAddress, 1);
        AddFirmwareVo firmwareVo = new AddFirmwareVo(firmware, a, b);
        if (firmwareVo.getFirmware().getIsList() == 1) {
            log.info("推送部分");
            //把excel解析出来放在数据库
            List<WhiteListExeclVo> whiteListExeclVos = null;
            List<WhiteList> whiteLists = null;
            try {
                whiteListExeclVos = ExcelUtil.readExcel(new BufferedInputStream(excel.getInputStream()), WhiteListExeclVo.class);
                whiteLists = new ArrayList<>();
                for (int i = 0; i < whiteListExeclVos.size(); i++) {
                    WhiteList whiteList = new WhiteList();
                    WhiteListExeclVo o = whiteListExeclVos.get(i);
                    whiteList.setSerialNumber(o.getSerialNumber());
                    whiteLists.add(whiteList);
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new MyServiceException("Excel解析失败");
            }


            return firmwareService.add(firmwareVo, whiteLists);
        } else {
            return firmwareService.add(firmwareVo);
        }
    }

    @GetMapping("getFirmwareList")
    @Log(operateModule = "公共服务", operateContent = "查询固件列表")
    public Result getFirmwareList(@RequestParam("current") Integer current,
                                  @RequestParam("pageSize") Integer pageSize) {
        return firmwareService.getFirmwareList(current, pageSize);
    }

    @Log(operateModule = "公共服务", operateContent = "修改固件")
    @PostMapping("editFirmware")
    public Result editFirmware(@RequestParam("id") Long id,
                               @RequestParam("version") String version,
                               @RequestParam("name") String name,
                               @RequestParam("pushTime") String pushTime,
                               @RequestParam("isUpgrade") Integer isUpgrade,
                               @RequestParam("isNow") Integer isNow,
                               @RequestParam("isList") Integer isList,
                               @RequestParam("aName") String aName,
                               @RequestParam("aAddress") String aAddress,
                               @RequestParam("bName") String bName,
                               @RequestParam("bAddress") String bAddress,
                               @RequestParam(value = "description") String description,
                               @RequestParam(value = "whiteName", required = false) String whiteName,
                               @RequestParam(value = "excel", required = false) MultipartFile excel) {
        Date date = DateUtil.getDate(pushTime);
        if (StringUtils.isEmpty(aAddress)||StringUtils.equals(aAddress,"undefined")) {
            throw new MyServiceException("必须上传文件","500");
        }
        if (StringUtils.isEmpty(bAddress)||StringUtils.equals(aAddress,"undefined")) {
            throw new MyServiceException("必须上传文件","500");
        }
        if (null == date){
            throw new MyServiceException("请填写推送时间");
        }
        Firmware firmware = new Firmware(version, name, DateUtil.SDF.format(date), isUpgrade, isNow, isList, ConstantsUtil.DF.format(new Date()));
        firmware.setId(id);
        firmware.setWhiteName(whiteName);
        firmware.setDescription(description);
        Files a = new Files(aName, aAddress, 0);
        Files b = new Files(bName, bAddress, 1);
        AddFirmwareVo firmwareVo = new AddFirmwareVo(firmware, a, b);
        if (firmwareVo.getFirmware().getIsList() == 1 && excel != null) {
            log.info("推送部分");
            //把excel解析出来放在数据库
            List<WhiteListExeclVo> whiteListExeclVos = null;
            try {
                whiteListExeclVos = ExcelUtil.readExcel(new BufferedInputStream(excel.getInputStream()), WhiteListExeclVo.class);
                log.info("excel解析成功=======>>:{},size:{}", whiteListExeclVos, whiteListExeclVos.size());
                List<WhiteList> whiteLists = new ArrayList<>();
                for (int i = 1; i < whiteListExeclVos.size(); i++) {
                    WhiteList whiteList = new WhiteList();
                    WhiteListExeclVo o = whiteListExeclVos.get(i);
                    whiteList.setSerialNumber(o.getSerialNumber());
                    whiteLists.add(whiteList);
                }
                firmwareService.edit(firmwareVo, whiteLists);
            } catch (Exception e) {
                e.printStackTrace();
                log.info("excel解析失败日志===============>>" + e.getMessage());
                throw new MyServiceException("Excel解析失败");
            }
            Result result = Result.getDefaultTrue();
            result.setMsg("ok");
            return result;
        } else {
            return firmwareService.edit(firmwareVo);
        }

    }

    /**
     * 查询一条固件信息，包括一条固件信息对应的两个文件
     *
     * @param id
     * @return
     */
    @GetMapping("getById")
    public Result getById(@RequestParam("id") Long id) {
        return firmwareService.getByIdAndFiles(id);
    }

    @GetMapping("getByVersion")
    public Result getByVersion(@RequestParam("version") String version) {
        Firmware firmware = firmwareService.getOne(new QueryWrapper<Firmware>().eq("version", version));
        Result defaultTrue = Result.getDefaultTrue();
        defaultTrue.setData(firmware);
        return defaultTrue;
    }

    @GetMapping("Revoke")
    @Log(operateModule = "公共服务", operateContent = "撤销版本")
    public Result revoke(@RequestParam("id") Long id) {
        Firmware firmware = this.firmwareService.getById(id);
        if (null == firmware) {
            Result result = Result.getDefaultFalse();
            result.setMsg("记录不存在");
            return result;
        }
        if (firmware.getType() == 1 || firmware.getType() == 2) {
            Result result = Result.getDefaultFalse();
            result.setMsg("该状态不可撤销");
            return result;
        }
        firmware.setId(id);
        firmware.setType(2);
        this.firmwareService.updateById(firmware);
        return Result.getDefaultTrue();
    }

    public static void main(String[] args) throws Exception {
        int i = "1.2.1".compareTo("1.2.1");
        System.out.println(i);
        System.out.println("1.2.1".compareTo("1.2.1")>0);
    }

}
