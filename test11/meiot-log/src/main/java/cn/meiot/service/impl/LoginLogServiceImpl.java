package cn.meiot.service.impl;

import cn.meiot.entity.ExceptionLog;
import cn.meiot.entity.LoginLog;
import cn.meiot.entity.vo.Result;
import cn.meiot.mapper.LoginLogMapper;
import cn.meiot.service.ILoginLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wuyou
 * @since 2019-10-14
 */
@Service
@SuppressWarnings("ALL")
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements ILoginLogService {
    @Autowired
    private LoginLogMapper loginLogMapper;

    @Override
    public Result<Map> getLoginLogList(Integer currentPage, Integer pageSize, String startTime, String endTime, String account, Long userId) {
        List<LoginLog> loginLogs=  loginLogMapper.selectLoginLogLogListByMainUserId(currentPage,pageSize,startTime,endTime,account,userId);
        Integer total =loginLogMapper.selectLoginLogLogListByMainUserIdTotal(startTime,endTime,account,userId);
        Map<String,Object> rest=new HashMap<>();
        rest.put("LoginLogs",loginLogs);
        rest.put("total",total);
        Result result = Result.getDefaultTrue();
        result.setData(rest);
        return result;
    }

    @Override
    public Result<Map> getLoginLogListAdmin(Integer currentPage, Integer pageSize, String startTime, String endTime, String account, Integer type) {
        List<LoginLog> loginLogs=  loginLogMapper.selectLoginLogListAdmin(currentPage,pageSize,startTime,endTime,account,type);
        Integer total =loginLogMapper.selectLoginLogListAdminTotal(startTime,endTime,account,type);
        Map<String,Object> rest=new HashMap<>();
        rest.put("LoginLogs",loginLogs);
        rest.put("total",total);
        Result result = Result.getDefaultTrue();
        result.setData(rest);
        return result;
    }
}

