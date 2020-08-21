package cn.meiot.service;

import cn.meiot.entity.ActionLog;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import cn.meiot.entity.RespData;

/**
 * <p>
 * 日志表 服务类
 * </p>
 *
 * @author 贺志辉
 * @since 2019-08-15
 */
public interface IActionLogService extends IService<ActionLog> {
	/**
	 * 插入普通日志
	 * @param actionLog
	 * @return
	 */
	RespData insertLog(ActionLog actionLog);

	/**
	 * 查询日志列表
	 * @param currentPage
	 * @param pageSize
	 * @param startTime
	 * @param endTime
	 * @param account
	 * @return
	 */
	Result getLogList(Integer currentPage, Integer pageSize, String startTime, String endTime, String account,Long userId);

	/**
	 * 查询平台日志列表
	 * @param currentPage
	 * @param pageSize
	 * @param startTime
	 * @param endTime
	 * @param account
	 * @return
	 */
    Result getLogListAdmin(Integer currentPage, Integer pageSize, String startTime, String endTime, String account, Integer type);
}
