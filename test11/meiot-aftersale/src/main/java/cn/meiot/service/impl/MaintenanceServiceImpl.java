package cn.meiot.service.impl;

import cn.meiot.common.enums.MaintenanceStatusEnum;
import cn.meiot.entity.Maintenance;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.dto.MaintenanceDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.StatisticsVo;
import cn.meiot.entity.vo.StatusVo;
import cn.meiot.enums.AccountType;
import cn.meiot.exception.UpdateException;
import cn.meiot.mapper.MaintenanceMapper;
import cn.meiot.service.IMaintenanceService;
import cn.meiot.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-28
 */
@Service
@Slf4j
@SuppressWarnings(value = "all")
public class MaintenanceServiceImpl extends ServiceImpl<MaintenanceMapper, Maintenance> implements IMaintenanceService {


    @Autowired
    private ImgUrl imgUrl;

    @Autowired
    private MaintenanceMapper maintenanceMapper;
    @Autowired
    private UserInfoUtil userInfoUtil;

    @Override
    public Result saveMaintenance(Maintenance maintenance) {
        System.out.println(maintenance.getUserId());
        //通过用户id获取用户信息
//
        AuthUserBo userInfo = userInfoUtil.getUserInfo();
        if (null == userInfo) {
            return new Result().Faild(ErrorCodeConstant.NO_USER_INFORMATION_WAS_FOUND);
        }
        maintenance.setAccount(userInfo.getUser().getUserName());
        log.info("此用户的账号：{}", maintenance.getAccount());
        //添加状态   已报修
        maintenance.setMStatus(MaintenanceStatusEnum.REPAIRS.value());
        maintenance.setReportTime(ConstantsUtil.DF.format(new Date()));//报修时间
        Integer count = maintenanceMapper.insert(maintenance);
        if (null == count && count == 0) {
            return Result.getDefaultFalse();
        }
        return Result.getDefaultTrue();
    }

    @Override
    public Result updateStatus(Integer type, Long id) {
        //获取当前时间
        String date = ConstantsUtil.DF.format(new Date());
        Maintenance maintenance = Maintenance.builder().mStatus(type).build();
        UpdateWrapper<Maintenance> updateWrapper = new UpdateWrapper<Maintenance>();
        updateWrapper.set("m_status", type);//订单状态
        updateWrapper.eq("id", id);
        if (type == MaintenanceStatusEnum.ACCEPT.value()) {
            maintenance.setAcceptTime(date);
            updateWrapper.eq("m_status", MaintenanceStatusEnum.REPAIRS.value());
        } else {
            maintenance.setMaintainTime(date);
            updateWrapper.eq("m_status", MaintenanceStatusEnum.ACCEPT.value());
        }
        log.info("更新的对象，{}", maintenance);
        Integer update = maintenanceMapper.update(maintenance, updateWrapper);
        if (null == update || update == 0) {
            return Result.getDefaultFalse();
        }
        return Result.getDefaultTrue();
    }

    @Override
    public Result getDetail(Long id, Long userId) {
        AuthUserBo userInfo = userInfoUtil.getUserInfo();
        if (null == userInfo) {
            return new Result().Faild(ErrorCodeConstant.NO_USER_INFORMATION_WAS_FOUND);
        }
        Maintenance maintenance = null;
        Integer userType = userInfo.getUser().getType();
        log.info("用户类型：{}", userType);
        if (AccountType.PLATFORM.value() == userType) {
            maintenance = maintenanceMapper.selectById(id);
        } else {
            log.info("只能查询本人的订单详情");
            maintenance = maintenanceMapper.selectOne(new QueryWrapper<Maintenance>().eq("id", id).eq("user_id", userId));
        }
        Result result = Result.getDefaultTrue();
        MaintenanceDto maintenanceDto = MaintenanceDto.getMaintenanceDto(maintenance, imgUrl);
        result.setData(maintenanceDto);
        return result;
    }


    @Override
    public Result getAfterSaleByPage(Map<String, Object> paramMap) {
        Integer total = null;
        List<Maintenance> maintenanceList = maintenanceMapper.selectAfterSaleByPage(paramMap);

        List<MaintenanceDto> maintenanceDtoList = new ArrayList<>();
        for (Maintenance m : maintenanceList) {
            maintenanceDtoList.add(new MaintenanceDto(m, imgUrl));
        }

        total = maintenanceMapper.selecTotal(paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        map.put("maintenanceList", maintenanceDtoList);

        Result result = Result.getDefaultTrue();
        result.setData(map);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result editStatus(List<StatusVo> statusVoList) throws Exception {
        if (null == statusVoList) {
            return Result.getDefaultFalse().builder().msg("数据为空").code("1").build();
        }
        for (StatusVo statusVo:statusVoList) {
            Maintenance maintenance = maintenanceMapper.selectAfterSaleById(statusVo.getId(), null);
            if (maintenance.getMStatus()>statusVo.getStatus()){
                return Result.getDefaultFalse().builder().msg("故障消息以完成，不可再受理").code("1").build();
            }else if (maintenance.getMStatus()==statusVo.getStatus()){
               if (maintenance.getMStatus()==2){
                   return Result.getDefaultFalse().builder().msg("该故障消息为已受理状态，不可再受理").code("1").build();
               }else {
                   return Result.getDefaultFalse().builder().msg("故障消息为已完成状态，不可再完成").code("1").build();
               }
            }else if (statusVo.getStatus()-maintenance.getMStatus()!=1){
                return Result.getDefaultFalse().builder().msg("请先受理").code("1").build();
            }
        }
         maintenanceMapper.updateStatusByList(statusVoList);
        return Result.getDefaultTrue().builder().msg("操作成功").code("0").build();
    }

    public Result getAfterSaleById(Long id, Long userId) {
        Maintenance maintenance = maintenanceMapper.selectAfterSaleById(id, userId);
        Result result = Result.getDefaultTrue();
        MaintenanceDto maintenanceDto = MaintenanceDto.getMaintenanceDto(maintenance, imgUrl);
        result.setData(maintenanceDto);
        return result;
    }

    @Override
    public List<StatisticsVo> getStatistics(String serialNumber) {



        return maintenanceMapper.getStatistics(serialNumber);
    }
}
