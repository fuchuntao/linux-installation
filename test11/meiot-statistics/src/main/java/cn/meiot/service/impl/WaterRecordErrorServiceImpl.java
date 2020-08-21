package cn.meiot.service.impl;

import cn.meiot.entity.WaterRecordError;
import cn.meiot.mapper.WaterRecordErrorMapper;
import cn.meiot.service.IWaterRecordErrorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 水表抄表记录异常表 服务实现类
 * </p>
 *
 * @author fct
 * @since 2020-02-28
 */
@Slf4j
@Service
public class WaterRecordErrorServiceImpl extends ServiceImpl<WaterRecordErrorMapper, WaterRecordError> implements IWaterRecordErrorService {


    @Autowired
    private WaterRecordErrorMapper waterRecordErrorMapper;

    /**
     *
     * @Title: selectByStatus
     * @Description: 查询异常数据的状态 status==0
     * @param status
     * @return: java.util.List<cn.meiot.entity.WaterRecordError>
     */
    @Override
    public List<WaterRecordError> selectByStatus(Integer status) {
        List<WaterRecordError> waterRecordErrorList = waterRecordErrorMapper.selectByStatus(status);
        if(CollectionUtils.isEmpty(waterRecordErrorList)) {
            log.info("查询异常数据的状态的数据为空");
        }
        return waterRecordErrorList;
    }
}
