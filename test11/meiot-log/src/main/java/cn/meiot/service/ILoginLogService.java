package cn.meiot.service;

import cn.meiot.entity.LoginLog;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wuyou
 * @since 2019-10-14
 */
public interface ILoginLogService extends IService<LoginLog> {

    /**
     * 查询登录日志
     * @param currentPage
     * @param pageSize
     * @param startTime
     * @param endTime
     * @param account
     * @param userId
     * @return
     */
    Result<Map> getLoginLogList(Integer currentPage, Integer pageSize, String startTime, String endTime, String account, Long userId);

    /**
     * 平台查询登录日志
     * @param currentPage
     * @param pageSize
     * @param startTime
     * @param endTime
     * @param account
     * @param type
     * @return
     */
    Result<Map> getLoginLogListAdmin(Integer currentPage, Integer pageSize, String startTime, String endTime, String account, Integer type);
}
