package cn.meiot.service;

import cn.meiot.entity.ExceptionLog;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wuyou
 * @since 2019-10-12
 */
public interface IExceptionLogService extends IService<ExceptionLog> {

    /**
     * 查询异常日志
     * @param pageSize
     * @param startTime
     * @param endTime
     * @param account
     * @return
     */
    Result<Map> getExceptionLogList(Integer currentPage, Integer pageSize, String startTime, String endTime, String account, Long userId);

    /**
     * 查询平台异常日志
     * @param pageSize
     * @param startTime
     * @param endTime
     * @param account
     * @return
     */
    Result<Map> getExceptionLogListAdmin(Integer currentPage, Integer pageSize, String startTime, String endTime, String account, Integer type);
}
