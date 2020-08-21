package cn.meiot.mq;

import cn.meiot.dao.LoginRedisDao;
import cn.meiot.entity.SysUser;
import cn.meiot.enums.AccountType;
import cn.meiot.mapper.SysUserMapper;
import cn.meiot.utils.QueueConstantUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SysUserReceive {

    @Autowired
    private LoginRedisDao loginRedisDao;

    @Autowired
    private SysUserMapper sysUserMapper;

    @RabbitListener(queues = QueueConstantUtil.TAKE_THE_USER_OFFLINE)
    public void kickUser(SysUser sysUser){
        //判断当前用户属于什么类型
        if(AccountType.ENTERPRISE.value().equals(sysUser.getType())){
            killEnUser(sysUser.getId());
            return ;
        }else{
            loginRedisDao.clearLoginInfo(sysUser.getId());
        }

    }

    private void killEnUser(Long userId){
        //删除当前用户id的token信息
        loginRedisDao.clearLoginInfo(userId);
        //查询当前用户下的所有子用户
        List<Long> ids = sysUserMapper.selectIdByBelongId(userId);
        if(null == ids || ids.size() == 0 ){
            return ;
        }
        ids.forEach(id ->{
            //清楚token
            loginRedisDao.clearLoginInfo(id);
        });

    }
}
