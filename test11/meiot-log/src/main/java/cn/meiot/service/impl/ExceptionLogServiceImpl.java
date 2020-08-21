package cn.meiot.service.impl;

import cn.meiot.entity.ExceptionLog;
import cn.meiot.entity.vo.Result;
import cn.meiot.mapper.ExceptionLogMapper;
import cn.meiot.service.IExceptionLogService;
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
 * @since 2019-10-12
 */
@Service
@SuppressWarnings("ALL")
public class ExceptionLogServiceImpl extends ServiceImpl<ExceptionLogMapper, ExceptionLog> implements IExceptionLogService {
    @Autowired
    private ExceptionLogMapper exceptionLogMapper;

    @Override
    public Result<Map> getExceptionLogList(Integer currentPage, Integer pageSize, String startTime, String endTime, String account, Long userId) {
      List<ExceptionLog> exceptionLogs=  exceptionLogMapper.selectExceptionLogListByMainUserId(currentPage,pageSize,startTime,endTime,account,userId);
      Integer total =exceptionLogMapper.selectExceptionLogListByMainUserIdTotal(startTime,endTime,account,userId);
      Map<String,Object> rest=new HashMap<>();
      rest.put("exceptionLogs",exceptionLogs);
      rest.put("total",total);
        Result result = Result.getDefaultTrue();
        result.setData(rest);
        return result;
    }

    @Override
    public Result<Map> getExceptionLogListAdmin(Integer currentPage, Integer pageSize, String startTime, String endTime, String account, Integer type) {
        List<ExceptionLog> exceptionLogs=  exceptionLogMapper.selectExceptionLogListByMainUserIdAdmin(currentPage,pageSize,startTime,endTime,account,type);
        Integer total=exceptionLogMapper.selectExceptionLogListByMainUserIdTotalAdmin(startTime,endTime,account,type);
        Map<String,Object> map=new HashMap<>();
        map.put("exceptionLogs",exceptionLogs);
        map.put("total",total);
        Result result = Result.getDefaultTrue();
        result.setData(map);
        return result;
    }
}
