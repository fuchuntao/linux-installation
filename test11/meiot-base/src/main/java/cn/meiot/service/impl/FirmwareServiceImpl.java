package cn.meiot.service.impl;

import cn.meiot.entity.Files;
import cn.meiot.entity.Firmware;
import cn.meiot.entity.WhiteList;
import cn.meiot.entity.vo.*;
import cn.meiot.exception.MyServiceException;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.mapper.FilesMapper;
import cn.meiot.mapper.FirmwareMapper;
import cn.meiot.mapper.WhiteListMapper;
import cn.meiot.service.IFirmwareService;
import cn.meiot.utils.CommonUtil;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.RedisUtil;
import cn.meiot.utils.VersionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wuyou
 * @since 2019-11-13
 */
@Service
@Transactional(rollbackFor = Exception.class)
@SuppressWarnings("all")
@Slf4j
public class FirmwareServiceImpl extends ServiceImpl<FirmwareMapper, Firmware> implements IFirmwareService {
    @Autowired
    private FirmwareMapper firmwareMapper;
    @Autowired
    private FilesMapper filesMapper;
    @Autowired
    private WhiteListMapper whiteListMapper;
    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private DeviceFeign deviceFeign;


    @Override
    public Firmware getNewFirmware() {
//        return firmwareMapper.selectPage(new Page<Firmware>(0,1),
//                new QueryWrapper<Firmware>().orderByDesc("create_time")).getRecords().get(0);
        return null;
    }

    @Override
    public Result add(AddFirmwareVo firmwareVo) {
        Firmware firmware = firmwareVo.getFirmware();
        Files a = firmwareVo.getA();
        Files b = firmwareVo.getB();
        firmware.setCreateTime(ConstantsUtil.DF.format(new Date()));
        Firmware firmware1 = this.getOne(new QueryWrapper<Firmware>().eq("version", firmware.getVersion()));
        if (null != firmware1) {
            throw new MyServiceException("重复版本号");
        }
        Firmware one = this.getOne(new QueryWrapper<Firmware>().lambda().lt(Firmware::getPushTime, ConstantsUtil.DF.format(new Date())).orderByDesc(Firmware::getId).last("limit 1"));
        if (null != one ) {
            String maxVersion =one.getVersion();

            if (VersionUtil.eq(maxVersion,firmware.getVersion()) > 0) {
                throw new MyServiceException("上传的版本必须大于数据库当前最大的版本");
            }
        }
//        firmware.setSort(VersionUtil.getSort(firmware.getVersion()));
        log.info("上传的版本：{}",firmware);
        this.firmwareMapper.insert(firmware);
        a.setFirmwareId(firmware.getId());
        b.setFirmwareId(firmware.getId());
        this.filesMapper.insert(a);
        this.filesMapper.insert(b);
        return Result.getDefaultTrue();
    }

    @Override
    public Result add(AddFirmwareVo firmwareVo, List<WhiteList> whiteLists) {
        Firmware firmware = firmwareVo.getFirmware();
        Files a = firmwareVo.getA();
        Files b = firmwareVo.getB();
        firmware.setCreateTime(ConstantsUtil.DF.format(new Date()));
        Firmware firmware1 = this.getOne(new QueryWrapper<Firmware>().eq("version", firmware.getVersion()));
        if (null != firmware1) {
            throw new MyServiceException("重复版本号");
        }
        String maxVersion = this.getOne(new QueryWrapper<Firmware>().lambda().lt(Firmware::getPushTime, ConstantsUtil.DF.format(new Date())).orderByDesc(Firmware::getId).last("limit 1")).getVersion();
        if (VersionUtil.eq(maxVersion,firmware.getVersion())> 0) {
            throw new MyServiceException("上传的版本必须大于数据库当前最大的版本");
        }
//        firmware.setSort(VersionUtil.getSort(firmware.getVersion()));
        log.info("上传的版本：{}",firmware);
        this.firmwareMapper.insert(firmware);
        a.setFirmwareId(firmware.getId());
        b.setFirmwareId(firmware.getId());
        this.filesMapper.insert(a);
        this.filesMapper.insert(b);
        for (WhiteList whiteList : whiteLists) {
            whiteList.setFirmwareId(firmware.getId());
            whiteListMapper.insert(whiteList);
        }
        return Result.getDefaultTrue();
    }

    @Override
    public Result getFirmwareList(Integer current, Integer pageSize) {
        Page<Firmware> page = new Page<>(current, pageSize);
        IPage<Firmware> iPage = this.page(page, new QueryWrapper<Firmware>().lambda().ne(Firmware::getType, 7).orderByDesc(Firmware::getId));
        List<Firmware> records = iPage.getRecords();
        List<Firmware> firmwares = new ArrayList<>();
        for (Firmware firmware : records) {
            List<String> s = filesMapper.selectNameByVersion(firmware.getVersion());
            Long size = 0L;
            for (String address : s) {
                String sizeByAddress = RedisUtil.getSizeByAddress(address);
                if (!StringUtils.isEmpty(sizeByAddress)) {
                    size += Long.valueOf(sizeByAddress)/1024L;

                }
            }
            firmware.setSize(size);
            firmwares.add(firmware);
        }
        iPage.setRecords(firmwares);
        Result result = Result.getDefaultTrue();
        result.setData(iPage);
        return result;
    }

    @Override
    public FirmwareFileVo getFirmwareUrl(String version, Integer region) {
        return firmwareMapper.selectFirmwareUrl(version, region);
    }

    @Override
    public Result edit(AddFirmwareVo firmwareVo, List<WhiteList> whiteLists) {
        Firmware firmware = firmwareVo.getFirmware();
        Files a = firmwareVo.getA();
        Files b = firmwareVo.getB();
        firmware.setType(0);
        firmware.setCreateTime(ConstantsUtil.DF.format(new Date()));
        Firmware firmware1 = this.getById(firmware.getId());
        if (null == firmware1) {
            throw new MyServiceException("记录不存在");
        }
        if (firmware1.getType() == 1) {
            throw new MyServiceException("已推送不能修改");
        }
        String maxVersion = this.getOne(new QueryWrapper<Firmware>().lambda().lt(Firmware::getPushTime, ConstantsUtil.DF.format(new Date())).orderByDesc(Firmware::getId).last("limit 1")).getVersion();

        if (VersionUtil.eq(maxVersion,firmware.getVersion()) > 0) {
            throw new MyServiceException("修改的版本必须大于数据库当前最大的版本");
        }
        List<WhiteList> whiteListsId = this.whiteListMapper.selectList(new QueryWrapper<WhiteList>().eq("firmware_id", firmware1.getId()));
        List<Long> deltetList = new ArrayList();
        for (Long id : deltetList) {
            deltetList.add(id);
        }
        if (deltetList.size() > 0) {
            this.whiteListMapper.deleteBatchIds(deltetList);
        }
        firmware.setSort(VersionUtil.getSort(firmware.getVersion()));
        this.firmwareMapper.updateById(firmware);
        a.setFirmwareId(firmware1.getId());
        b.setFirmwareId(firmware1.getId());
        this.filesMapper.update(a, new QueryWrapper<Files>().eq("firmware_id", firmware1.getId()).eq("region", 0));
        this.filesMapper.update(b, new QueryWrapper<Files>().eq("firmware_id", firmware1.getId()).eq("region", 1));
        for (WhiteList whiteList : whiteLists) {
            whiteList.setFirmwareId(firmware.getId());
            this.whiteListMapper.insert(whiteList);
        }
        return Result.getDefaultTrue();
    }

    @Override
    public Result edit(AddFirmwareVo firmwareVo) {
        Firmware firmware = firmwareVo.getFirmware();
        Files a = firmwareVo.getA();
        Files b = firmwareVo.getB();
        firmware.setType(0);
        firmware.setCreateTime(ConstantsUtil.DF.format(new Date()));
        Firmware firmware1 = this.getById(firmware.getId());
        if (null == firmware1) {
            throw new MyServiceException("记录不存在");
        }
        if (firmware1.getType() == 1) {
            throw new MyServiceException("已推送不能修改");
        }
        String maxVersion = this.getOne(new QueryWrapper<Firmware>().lambda().lt(Firmware::getPushTime, ConstantsUtil.DF.format(new Date())).orderByDesc(Firmware::getId).last("limit 1")).getVersion();
        if (VersionUtil.eq(maxVersion,firmware.getVersion()) > 0) {
            throw new MyServiceException("修改的版本必须大于数据库当前最大的版本");
        }
        if (firmware.getIsList() == 0) {
            this.whiteListMapper.delete(new QueryWrapper<WhiteList>().eq("firmware_id", firmware1.getId()));
        }
//        firmware.setSort(VersionUtil.getSort(firmware.getVersion()));
        this.firmwareMapper.updateById(firmware);
        a.setFirmwareId(firmware1.getId());
        b.setFirmwareId(firmware1.getId());
        this.filesMapper.update(a, new QueryWrapper<Files>().eq("firmware_id", firmware1.getId()).eq("region", 0));
        this.filesMapper.update(b, new QueryWrapper<Files>().eq("firmware_id", firmware1.getId()).eq("region", 1));
        return Result.getDefaultTrue();
    }

    @Override
    public Result getByIdAndFiles(Long id) {
        Firmware firmware = this.getById(id);
        Files a = this.filesMapper.selectOne(new QueryWrapper<Files>().eq("firmware_id", id).eq("region", 0));
        Files b = this.filesMapper.selectOne(new QueryWrapper<Files>().eq("firmware_id", id).eq("region", 1));
//        ImgConfigVo imgConfig = commonUtil.getImgConfig();
//        a.setAddress(imgConfig.getServername() + imgConfig.getMap() + imgConfig.getUpgrade() + a.getAddress());
//        b.setAddress(imgConfig.getServername() + imgConfig.getMap() + imgConfig.getUpgrade() + b.getAddress());
        Map<String, Object> map = new HashMap<>();
        map.put("firmware", firmware);
        map.put("a", a);
        map.put("b", b);
        Result result = Result.getDefaultTrue();
        result.setData(map);
        return result;
    }

    @Override
    public Firmware queryVersion() {
        List<Firmware> firmwares = this.firmwareMapper.selectReservationVersion(ConstantsUtil.DF.format(new Date()));
        if (firmwares == null || firmwares.size() <= 0) {
            return null;
        }
        Firmware firmware = firmwares.get(0);
        firmware.setType(1);
        this.firmwareMapper.updateById(firmware);
        if (firmwares.size() > 1) {
            for (int i = 1; i < firmwares.size(); i++) {
                Firmware updateFirmware = firmwares.get(i);
                /**
                 * 状态7属于过期版本
                 */
                updateFirmware.setType(7);
                this.firmwareMapper.updateById(updateFirmware);
            }
        }
        return firmware;
    }


    @Override
    public Firmware getForceVersion(Integer isList) {
        return this.firmwareMapper.selectOne(new QueryWrapper<Firmware>().lambda().eq(Firmware::getIsUpgrade, "1").eq(Firmware::getIsList, isList).eq(Firmware::getType, "1").orderByDesc(Firmware::getSort).last("limit 1"));
    }
}
