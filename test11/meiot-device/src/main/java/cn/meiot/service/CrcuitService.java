package cn.meiot.service;

import cn.meiot.entity.bo.Crcuit;
import cn.meiot.entity.vo.Result;

public interface CrcuitService {

	/**
	 * 查询项目的
	 * @param projectId
	 * @return
	 */
	Result query(Integer projectId,String mode);

	/**
	 * 断路器参数设置
	 * @param crcuit
	 * @param projectId 
	 * @param userId 
	 * @return
	 */
	Result update(Crcuit crcuit, Integer projectId, Long userId);

}
