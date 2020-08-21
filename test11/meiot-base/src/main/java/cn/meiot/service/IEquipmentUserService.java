package cn.meiot.service;

import cn.meiot.entity.EquipmentUser;
import cn.meiot.entity.vo.DeviceVersionVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.UpgradeVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 设备用户关系表 服务类
 * </p>
 *
 * @author wuyou
 * @since 2019-11-28
 */
public interface IEquipmentUserService extends IService<EquipmentUser> {


    /**
     * 通过用户ID和项目ID查询需要更新的设备以及当前版本的详细描述
     * @param userId
     * @return
     */
    List<UpgradeVo> getUpgradeAndDevice(Long userId, Integer projectId);

    /**
     * 通过设备号查询用户id与项目编号
     * @param serialNumber
     * @return
     */
    EquipmentUser getUserIdAndProjectIdBySerialNumber(String serialNumber);
}
