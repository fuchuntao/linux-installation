package cn.meiot.service.impl;

import cn.meiot.entity.SysUserProfile;
import cn.meiot.entity.vo.CityVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.mapper.SysUserProfileMapper;
import cn.meiot.service.ICityService;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.ReadWriteLockUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.Lock;

@Service
@Slf4j
public class CityServiceImpl implements ICityService {

    @Autowired
    private SysUserProfileMapper sysUserProfileMapper;

    @Override
    public Result updateCityByUserId(Long userId, CityVo cityVo) {
        if(null == userId){
            Result result = Result.getDefaultFalse();
            log.info("用户id不能为空");
            result.setMsg(ErrorCodeUtil.USER_ID_NOT_BE_NULL);
            return result;
        }
        //封装数据
        SysUserProfile sysUserProfile = SysUserProfile.builder().userId(userId)
                .province(cityVo.getProvince())
                .city(cityVo.getCity())
                .district(cityVo.getDistrict())
                .build();
        //添加读锁
        ReadWriteLockUtil.getReadLock();
        Integer count = null;
        try {
            log.info("添加读锁成功=========》");
            //判断此用户是否存在用户详细信息表中
            count = checkUserExist(userId);
        }finally {
            //由于读写锁不支持锁的升级，所以这几需要先将之前添加的读锁释放
            ReadWriteLockUtil.unReadLock();
        }
        if (null == count || count == 0) {
            log.info("不存在，执行添加操作");
            //将读锁升级位写锁
           ReadWriteLockUtil.getWriteLock();
            log.info("升级写锁成功========>");
            try {
                //再次判断此用户是否存在用户详细信息表中
                count = checkUserExist(userId);
                if (null == count || count == 0) {
                    log.info("不存在，执行插入操作");
                    int insert = sysUserProfileMapper.insert(sysUserProfile);
                    log.info("插入结果：{}",insert);
                    return Result.getDefaultTrue();
                } else {
                    log.info("已经存在，执行更新操作");
                    //执行更新操作
                    count = this.updateUserProfile(sysUserProfile);
                    log.info("更新结果：{}",count);
                    return Result.getDefaultTrue();
                }
            } finally {
                ReadWriteLockUtil.untWriteLock();
                log.info("解除写锁成功");
            }
        } else {
            log.info("无需加锁，直接进行更新操作");
            this.updateUserProfile(sysUserProfile);
            return Result.getDefaultTrue();
        }
    }

    /**
     * 校验用户详细信息表中是否存在此用户
     *
     * @param userId
     * @return
     */
    private Integer checkUserExist(Long userId) {
        Integer count = sysUserProfileMapper.selectCountById(userId);
        log.info("通过id查询的返回结果，大于0表示记录已存在，执行更新操作即可。====>count:{}", count);
        return count;
    }

    /**
     * 更新
     *
     * @return
     */
    private Integer updateUserProfile(SysUserProfile sysUserProfile) {
        return sysUserProfileMapper.update(sysUserProfile, new UpdateWrapper<SysUserProfile>().eq("user_id", sysUserProfile.getUserId()));

    }
}
