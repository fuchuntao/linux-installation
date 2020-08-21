package cn.meiot.service.impl;

import cn.meiot.entity.EquipmentUser;
import cn.meiot.entity.Firmware;
import cn.meiot.entity.WhiteList;
import cn.meiot.entity.vo.DeviceVersionVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.UpgradeVo;
import cn.meiot.entity.vo.WhiteVo;
import cn.meiot.mapper.EquipmentUserMapper;
import cn.meiot.mapper.FirmwareMapper;
import cn.meiot.mapper.WhiteListMapper;
import cn.meiot.service.IEquipmentUserService;
import cn.meiot.service.IWhiteListService;
import cn.meiot.utils.RedisUtil;
import cn.meiot.utils.VersionUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 设备用户关系表 服务实现类
 * </p>
 *
 * @author wuyou
 * @since 2019-11-28
 */
@Service
@SuppressWarnings("all")
public class EquipmentUserServiceImpl extends ServiceImpl<EquipmentUserMapper, EquipmentUser> implements IEquipmentUserService {

    @Autowired
    private EquipmentUserMapper equipmentUserMapper;
    @Autowired
    private FirmwareMapper firmwareMapper;
    @Autowired
    private WhiteListMapper whiteListMapper;

    @Override
    public List<UpgradeVo> getUpgradeAndDevice(Long userId, Integer projectId) {

        //要返回的数据
        Map<String, Object> resutMap = new HashMap();

        //1、找到最新的推送所有设备的版本
        Firmware allFirmware = this.firmwareMapper.selectOne(new QueryWrapper<Firmware>().lambda().eq(Firmware::getIsList, 0).eq(Firmware::getIsUpgrade, 0).eq(Firmware::getType, 1).orderByDesc(Firmware::getPushTime).last("limit 1"));

        //2、查推送部分设备最新版本号
        Firmware sectionFirmware = this.firmwareMapper.selectOne(new QueryWrapper<Firmware>().lambda().eq(Firmware::getIsList, 1).eq(Firmware::getIsUpgrade, 0).eq(Firmware::getType, 1).orderByDesc(Firmware::getPushTime).last("limit 1"));
        // 判断 第一步和第二步那个版本号大
        if (null == allFirmware && null == sectionFirmware) {
            return null;
        } else {
            if (null != allFirmware && null != sectionFirmware) {
                //如果第一步大推送所有

                if (VersionUtil.eq(allFirmware.getVersion(),sectionFirmware.getVersion()) > 0) {
                    return getResult(userId, projectId, allFirmware, null, allFirmware.getVersion());
                }
                List<UpgradeVo> deviceVersionVos = get(userId, projectId);
                List<UpgradeVo> result = getResult(userId, projectId, allFirmware, null, allFirmware.getVersion());
                if (null == deviceVersionVos) {
                    return result;
                }
                return get(result, deviceVersionVos);

            }
            if (allFirmware != null) {
                return getResult(userId, projectId, allFirmware, null, allFirmware.getVersion());
            }
            if (sectionFirmware != null) {
                List<UpgradeVo> deviceVersionVos = get(userId, projectId);
                List<UpgradeVo> result = getResult(userId, projectId, allFirmware, null, allFirmware.getVersion());
                if (null == deviceVersionVos) {
                    return result;
                }
//                return deviceVersionVos;
                return get(result, deviceVersionVos);
            }

        }

        return null;
    }

    public static void main(String[] args) {
        String a = "AD-13_V1.47";
        String b = "AD-138V1.47";
        System.out.println(a.compareTo(b));
    }

    private List<UpgradeVo> getResult(Long userId, Integer projectId, Firmware allFirmware, List<WhiteList> o, String version) {
        List<DeviceVersionVo> versionList = this.equipmentUserMapper.selectDeviceVersionVo(userId, projectId, o);
        if (null == versionList || versionList.size() <= 0) {
            return null;
        }
        List<DeviceVersionVo> deviceVersionVos = new ArrayList<>();
        if (null != versionList) {
            /**
             * 需要更新的设备列表  正式代码
             */
            for (DeviceVersionVo d : versionList) {
                if (StringUtils.isEmpty(d.getVersion())) {
                    continue;
                }
                if (VersionUtil.eq(d.getVersion(),allFirmware.getVersion()) < 0) {
                    d.setStatus(0);
                    d.setLength(0L);
                    d.setCurrentLength(0L);
                    deviceVersionVos.add(d);
                }
            }
        }


        List<UpgradeVo> deviceVersionVoList = new ArrayList<>(Arrays.asList(new UpgradeVo(version, allFirmware.getDescription(), deviceVersionVos)));
        if (deviceVersionVos.size() ==0 ) {
            return null;
        }
        return deviceVersionVoList;
    }

//    private List<UpgradeVo> get(UpgradeVo... upgradeVo) {
//        List<UpgradeVo> list = new ArrayList<>();
//        for (int i = 0; i < upgradeVo.length; i++) {
//            if (null != upgradeVo[i])
//                list.add(upgradeVo[i]);
//        }
//        return list;
//    }


    /**
     * 按照设备号聚合 取最大版本 这就是当前设备需要更新的版本
     * 去设备表里查 版本号不等于的版本就是需要升级的设备
     */
    public List<UpgradeVo> get(Long userId, Integer projectId) {
        // 按照设备号聚合白名单 联查版本表 max最大版本号
        List<WhiteVo> lists = this.whiteListMapper.selectUpgradeDevice();
        List<UpgradeVo> upgradeVos = new ArrayList<>();
        for (WhiteVo w : lists) {
            /**
             * 查询出所有设备
             */
            DeviceVersionVo allDeviceVersionVo = equipmentUserMapper.selectUpgrade(userId, projectId, w.getSerialNumber(), w.getVersion());
            DeviceVersionVo deviceVersionVo=null;
            if (VersionUtil.eq(allDeviceVersionVo.getVersion(), w.getVersion()) < 0) {
                deviceVersionVo=allDeviceVersionVo;
            }

            if (null == deviceVersionVo) {
                continue;
            }
            if (upgradeVos.size() == 0) {
                UpgradeVo upgradeVo = new UpgradeVo();
                upgradeVo.setVersion(w.getVersion());
                upgradeVo.setDescription(w.getDescription());
                upgradeVo.setList(new ArrayList<>(Arrays.asList(deviceVersionVo)));
                upgradeVos.add(upgradeVo);
            } else {
                for (int i = 0; i < upgradeVos.size(); i++) {
                    if (upgradeVos.get(i).getVersion().equals(w.getVersion())) {
                        List<DeviceVersionVo> list = upgradeVos.get(i).getList();
                        upgradeVos.get(i).getList().remove(deviceVersionVo);
                        list.add(deviceVersionVo);
                    } else {
                        UpgradeVo upgradeVo = new UpgradeVo();
                        upgradeVo.setVersion(w.getVersion());
                        upgradeVo.setDescription(w.getDescription());
                        upgradeVo.setList(new ArrayList<>(Arrays.asList(deviceVersionVo)));
                        upgradeVos.add(upgradeVo);
                    }
                }
            }
        }
        if (upgradeVos.size() == 0) {
            return null;
        }
        return upgradeVos;
    }

    /**
     * 去重复
     */
    public List<UpgradeVo> get(List<UpgradeVo> deviceVersionVoList, List<UpgradeVo> list) {
        int flag = 0;
        for (UpgradeVo u : list) {
            for (int i = 0; i < u.getList().size() - flag; i++) {
                if (u.getList().size() == 0) break;
                List<DeviceVersionVo> voList = deviceVersionVoList.get(0).getList();
                for (int a = 0; a < voList.size(); a++) {
                    if (u.getList().size() == 0) break;
                    if (voList.get(a).getSerialNumber().equals(u.getList().get(i).getSerialNumber())) {

                        if (VersionUtil.eq(deviceVersionVoList.get(0).getVersion(),u.getVersion()) <= 0) {
                            voList.remove(voList.get(a));
                            a--;
                        } else {
                            List<DeviceVersionVo> uList = u.getList();
                            u.getList().remove(i);
                            flag++;
                            i--;
                        }
                    }
                }
            }
        }
        if (deviceVersionVoList.get(0).getList().size() != 0) {
            list.add(deviceVersionVoList.get(0));
        }

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getList().size() == 0) {
                list.remove(i);
                i--;
            }
        }
        return list;
    }

    @Override
    public EquipmentUser getUserIdAndProjectIdBySerialNumber(String serialNumber) {
        return this.equipmentUserMapper.selectOne(new QueryWrapper<EquipmentUser>().lambda().eq(EquipmentUser::getSerialNumber, serialNumber).eq(EquipmentUser::getIsPrimary, 1));
    }
}
