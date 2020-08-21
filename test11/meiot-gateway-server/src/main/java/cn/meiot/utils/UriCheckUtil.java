package cn.meiot.utils;

import cn.meiot.entity.Auth;
import cn.meiot.entity.SysMenu;
import cn.meiot.entity.SysPermission;
import cn.meiot.enums.AccountType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class UriCheckUtil {

    private  static List<String> urls = new ArrayList<String>();

    private static List<String> permissions;

    @Autowired
    private RedisTemplate redisTemplate;

    static {
        urls.add("/project/cutProjectId");//切换项目
        urls.add("/pcd/list");//城市列表
        urls.add("/sysmenu/enterpriseList");//菜单列表
        urls.add("/fault-type/getTypeList");//故障消息-获取故障类型列表
        urls.add("/sysmenu/platList");//获取当前用户的菜单列表
        urls.add("/pc/Management/selectDataStatistics");//首页数据统计
        urls.add("/pc/equipment/allExaminationByMonth");//首页数据统计
        urls.add("/project/filterCondition");//项目的查询条件
        urls.add("/logout");//推出
       // urls.add("/app-user-fault-msg-alarm/unread");//获取当前用户的未读消息
       // urls.add("/app-user-fault-msg-alarm/reportTotal "); //获取报警的总记录数以及是否包含唯独消息
        //urls.add("/app-user-fault-msg-alarm/statisticsWarn");//根据设备统计报警信息
        urls.add("/pc/switch/querySwitchByBuilding");//根据建筑查询 设备ID 开关Id
        urls.add("/user/info");//根据建筑查询 设备ID 开关Id
        urls.add("/verify");
        urls.add("/Enterprise/clean");//清除缓存
        urls.add("/file/uploadFIle");//文件上传
        urls.add("/file/uploadMaterial/1");
        urls.add("/sys/ueditor/exec");
        urls.add("/wx/ticket");
        urls.add("/wx/ticket/unBindWx");
        urls.add("/communication/huawei");
       // urls.add("/enterprise-user-fault-msg-alarm/unread");
        //urls.add("/enterprise-user-fault-msg-alarm/reportTotal");





        urls.add("/personalAlarm/getNoticeList");
        urls.add("/personalAlarm/getTotalDetailed ");
        urls.add("/personalAlarm/getTotal");
        urls.add("/personalMsg/getNewsMsg");
        urls.add("/personalMsg/getUnreadTotal");
        urls.add("/personalAlarm/getNewsNotice");
        urls.add("/personalAlarm/findAlarmEarlyWarningTotal");
        urls.add("/app/meter-years/nowMonthMeter");
        urls.add("/app/meter-years/information");
        urls.add("/app/meter-years/getMonthlyMeter");
        urls.add("/app/meter-years/getPeakValleyMeter");
        urls.add("/pc-device/perMeterTop");
        urls.add("/pc-device/entMeterTop");
        urls.add("/pc-device/meterToYear");
        urls.add("/pc-meter-months/appMonthStatistics");
        urls.add("/app/enterprise/information");
        urls.add("/app/enterprise/getMonthlyMeter");
        urls.add("/app/enterprise/getPeakValleyMeter");
        urls.add("/water/waterFloor");
        urls.add("/water/waterInfo");
        urls.add("/water/waterList");
        urls.add("/water/WaterTotal");
        urls.add("/pc/water/insert");
        urls.add("/pc/water/update");
        urls.add("/pc/water/floorWater");
        urls.add("/pc/water/information");
        urls.add("/pc/WaterManagement/queryWaterMeter");
        urls.add("/file-apk/saveApkInfo");
        urls.add("/config/getCondition");
        urls.add("/config/list");
        urls.add("/config/update");




    }

    /**
     * 校验此uri是否具备权限
     * @param auth
     * @return
     */
    public Boolean checkUri(Auth auth){
        return true;
//        permissions = new ArrayList<String>();
//
//        //如果是个人用户，直接放行
//        if(AccountType.PERSONAGE.value().equals(auth.getType())){
//            return true;
//        }
//        //判断用户是否拥有此项目权限
//        //return true;
//
//        String key = "";
//        //判断是否是企业账户
//        if(AccountType.ENTERPRISE.value() == auth.getType()){
//            if(auth.getUri().equals("/project/getListById")){
//                return true;
//            }
//        }
//        key = RedisConstantUtil.USER_PERMISSIONS+auth.getUserId();
//        //通过key获取此用户的模块信息
//        List<String> list = (List<String>) redisTemplate.opsForValue().get(key);
//        if(null == list){
//            log.info("未获取到此用户的模块信息");
//            return false;
//        }
//        boolean flag = permissionverifty(urls,auth.getUri());
//        if(flag){
//            log.info("自定义的url放行");
//            return true;
//        }
//        flag = permissionverifty(list,auth.getUri());
//        log.info("缓存中的权限校验对比：{}",flag);
//        return flag;

    }


    private boolean  permissionverifty(List<String> list,String uri){
        for(String s : list){
            if(s == null ){
                continue;
            }
            if(s.split(",").length > 1){
                
                if(s.indexOf(uri) != -1){
                    log.info("权限校验通过！");
                    return true;
                }
            }else{
                if(s.equals(uri)){
                    log.info("权限校验通过！");
                    return true;
                }
            }

        }
        //log.info("权限校验未通过！");
        return false;
    }


    public static void main(String[] args) {
        String url = "/pc/Management/selectPcDataAll";
        String p = "/pc/Management/selectPcDataAll,/pc/Management/selectPcMonthMeter,/pc/Management/selectPcEnergy,/pc-device/queryDeviceInfo,/enterprise-user-fault-msg-alarm/getBigDataStatisticalAlarm,/enterprise-user-fault-msg-alarm/getBigDataStatus,/enterprise-user-fault-msg-alarm/getBigDataFaultTotal,/enterprise-user-fault-msg-alarm/getBigDataFaultDeviceNumber,/enterprise-user-fault-msg-alarm/getBigDataFaultList";
        System.out.println(p.contains(url));
    }

}
