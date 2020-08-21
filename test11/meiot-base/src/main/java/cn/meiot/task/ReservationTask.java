package cn.meiot.task;

import cn.meiot.entity.Equipment;
import cn.meiot.entity.Firmware;
import cn.meiot.entity.WhiteList;
import cn.meiot.service.IEquipmentService;
import cn.meiot.service.IFirmwareService;
import cn.meiot.service.IWhiteListService;
import cn.meiot.thread.SectionThread;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Package cn.meiot.task
 * @Description:
 * @author: 武有
 * @date: 2019/11/26 15:54
 * @Copyright: www.spacecg.cn
 */
@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
public class ReservationTask {
    @Autowired
    private IFirmwareService firmwareService;

    @Autowired
    private IEquipmentService equipmentService;

    @Autowired
    private IWhiteListService whiteListService;

//    @Value("${upgrade.maxUpgradeNumber}")
//    private Integer maxUpgradeNumber = 20;

    //    3.添加定时任务
    @Scheduled(cron = "0/5 * * * * ?")
    private void configureTasks() {
        Firmware firmware = firmwareService.queryVersion();
        if (null == firmware) {
            return;
        }
        if (firmware.getIsList() == 1) {
            new SectionThread(firmware).start();
        }
        log.info("【定时任务】==>>:{}，预约推送定时", firmware);
    }

//    @Scheduled(cron = "0/5 * * * * ?")
//    public void forceUpgrade() {
//        Firmware firmware = firmwareService.getForceVersion(0);
//        Firmware isListFirmware = firmwareService.getForceVersion(1);
//        if (null == firmware && null==isListFirmware) {
//            log.info("没有查询出可以升级的版本");
//            return;
//        }
//        if (null != firmware && null != isListFirmware) {
//            if (firmware.getVersion().compareTo(isListFirmware.getVersion())>0){
//                //推送所有设备
//                push(firmware);
//                return;
//            }
//            //推送部分设备
//            push(isListFirmware);
//            return;
//        }
//
//        if (null!=firmware){
//            push(firmware);
//            return;
//        }
//        if (null!=isListFirmware){
//            push(isListFirmware);
//            return;
//        }
//
//    }
//
//
//    private void push(Firmware firmware) {
//        log.info("【强制推送】 最新推送版本：{}", firmware);
//        Integer isList = firmware.getIsList();
//        int count = 0;
//        List<Equipment> list = null;
//
//
//        if (isList == 0) {
//            do {
//                count++;
//                Page<Equipment> page = new Page<>(count, maxUpgradeNumber);
//                IPage<Equipment> iPage = equipmentService.page(page, new QueryWrapper<Equipment>().lambda().lt(Equipment::getVersion, firmware.getVersion()));
//                list = iPage.getRecords();
//                if (null == list) {
//                    break;
//                }
//                log.info("【强制升级列表】{}",list);
//            } while (list.size() == maxUpgradeNumber);
//        } else if (isList == 1) {
//            List<WhiteList> whiteLists = whiteListService.list(new QueryWrapper<WhiteList>().lambda().eq(WhiteList::getFirmwareId,firmware.getId()));
//            if (null == whiteLists || whiteLists.size()<=0) {
//                return;
//            }
//            List<String> ids=new ArrayList<>();
//            for (WhiteList s:whiteLists) {
//                ids.add(s.getSerialNumber());
//            }
//            do {
//                count++;
//                Page<Equipment> page = new Page<>(count, maxUpgradeNumber);
//                IPage<Equipment> iPage = equipmentService.page(page, new QueryWrapper<Equipment>().lambda().lt(Equipment::getVersion, firmware.getVersion()).in(Equipment::getSerialNumber,ids));
//                list = iPage.getRecords();
//                if (null == list) {
//                    break;
//                }
//                log.info("【强制升级列表】{}",list);
//            } while (list.size() == maxUpgradeNumber);
//        } else {
//            return;
//        }
//    }

}
