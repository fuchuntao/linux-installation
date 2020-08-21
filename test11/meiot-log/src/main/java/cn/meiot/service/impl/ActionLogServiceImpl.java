package cn.meiot.service.impl;

import cn.meiot.entity.ActionLog;
import cn.meiot.entity.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import cn.meiot.entity.RespData;
import cn.meiot.mapper.ActionLogMapper;
import cn.meiot.service.IActionLogService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 日志表 服务实现类
 * </p>
 *
 * @author 贺志辉
 * @since 2019-08-15
 */
@Service
@SuppressWarnings("all")
public class ActionLogServiceImpl extends ServiceImpl<ActionLogMapper, ActionLog> implements IActionLogService {
	
	@Autowired
	private ActionLogMapper actionLogMapper;

	@Override
	public RespData insertLog(ActionLog actionLog) {
		return actionLogMapper.insertLog(actionLog) > 0 ? RespData.success(""):RespData.error("");
	}

	@Override
	public Result getLogList(Integer currentPage, Integer pageSize, String startTime, String endTime, String account,Long userId) {
		List<ActionLog> actionLogs =  actionLogMapper.getLogList(currentPage,pageSize,startTime,endTime,account,userId);
		Integer total=actionLogMapper.getLogListTotal(startTime,endTime,account,userId);
		Map<String,Object> map=new HashMap<>();
		map.put("actionLogs",actionLogs);
		map.put("total",total);
		Result<Map> result=Result.getDefaultTrue();
		result.setData(map);
		return result;
	}

	@Override
	public Result getLogListAdmin(Integer currentPage, Integer pageSize, String startTime, String endTime, String account, Integer type) {
		List<ActionLog> actionLogs =  actionLogMapper.getLogListAdmin(currentPage,pageSize,startTime,endTime,account,type);
		Integer total = actionLogMapper.getLogListAdminTotal(startTime,endTime,account,type);
		Map<String,Object> map=new HashMap<>();
		map.put("actionLogs",actionLogs);
		map.put("total",total);
		Result result = Result.getDefaultTrue();
		result.setData(map);
		return result;
	}
}
