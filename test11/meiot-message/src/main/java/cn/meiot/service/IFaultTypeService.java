package cn.meiot.service;

import cn.meiot.entity.FaultType;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-24
 */
public interface IFaultTypeService extends IService<FaultType> {
    /**
     * 获取类型列表
     * @return
     */
    Result getFaultTypeList();
}
