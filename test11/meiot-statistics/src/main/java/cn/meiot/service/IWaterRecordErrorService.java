package cn.meiot.service;

import cn.meiot.entity.WaterRecordError;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 水表抄表记录异常表 服务类
 * </p>
 *
 * @author fct
 * @since 2020-02-28
 */
public interface IWaterRecordErrorService extends IService<WaterRecordError> {



    /**
     *
     * @Title: selectByStatus
     * @Description: 查询异常数据的状态 status==0
     * @param
     * @return: java.util.List<cn.meiot.entity.WaterRecordError>
     */
    List<WaterRecordError> selectByStatus(Integer status);



}
