package cn.meiot.mapper;

import java.util.List;

import cn.meiot.entity.ActionLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;



/**
 * <p>
 * 日志表 Mapper 接口
 * </p>
 *
 * @author 贺志辉
 * @since 2019-08-15
 */
@Mapper
public interface ActionLogMapper extends BaseMapper<ActionLog> {
	

	
	/**
	 * @Title: insertLog  
	 * @Description: 添加日志信息
	 * @param @param actionLog
	 * @param @return
	 * @author: 贺志辉
	 * @return: Integer
	 * @date: 2019年8月16日
	 */
	Integer insertLog(ActionLog actionLog);

	List<ActionLog> getLogList(@Param("currentPage") Integer currentPage,
							   @Param("pageSize")Integer pageSize,
							   @Param("startTime")String startTime,
							   @Param("endTime")String endTime,
							   @Param("account")String account,
							   @Param("userId")Long userId);

	Integer getLogListTotal(@Param("startTime")String startTime,
							@Param("endTime")String endTime,
							@Param("account")String account,
							@Param("userId")Long userId);

    List<ActionLog> getLogListAdmin(@Param("currentPage") Integer currentPage,
									@Param("pageSize")Integer pageSize,
									@Param("startTime")String startTime,
									@Param("endTime")String endTime,
									@Param("account")String account,
									@Param("type")Integer type);
	Integer getLogListAdminTotal(@Param("startTime")String startTime,
								 @Param("endTime")String endTime,
								 @Param("account")String account,
								 @Param("type")Integer type);


}
