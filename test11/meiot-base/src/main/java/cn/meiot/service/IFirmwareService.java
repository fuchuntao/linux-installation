package cn.meiot.service;

import cn.meiot.entity.Firmware;
import cn.meiot.entity.WhiteList;
import cn.meiot.entity.vo.AddFirmwareVo;
import cn.meiot.entity.vo.FirmwareFileVo;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wuyou
 * @since 2019-11-13
 */
public interface IFirmwareService extends IService<Firmware> {

    /**
     * 获取最新的固件版本详细内容
     * @return
     */
    Firmware getNewFirmware();


    Result add(AddFirmwareVo firmwareVo);

    Result add(AddFirmwareVo firmwareVo, List<WhiteList> whiteLists);

    /**
     * 分页查询固件列表
     * @param current
     * @param pageSize
     * @return
     */
    Result getFirmwareList(Integer current, Integer pageSize);

    /**
     * 通过版本号 区域获取文件路径
     * @param version
     * @param region
     * @return
     */
    FirmwareFileVo getFirmwareUrl(String version, Integer region);

    /**
     * 修改固件
     * @param firmwareVo
     * @param whiteLists
     * @return
     */
    Result edit(AddFirmwareVo firmwareVo, List<WhiteList> whiteLists);
    /**
     * 修改固件
     * @param firmwareVo
     * @return
     */
    Result edit(AddFirmwareVo firmwareVo);


    Result getByIdAndFiles(Long id);

    Firmware queryVersion();

    Firmware getForceVersion(Integer isList);
}
