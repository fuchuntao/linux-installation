package cn.meiot.service.impl;

import cn.meiot.entity.AppMeterHours;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.mapper.AppMeterHoursMapper;
import cn.meiot.mapper.PcMeterHoursMapper;
import cn.meiot.mapper.PcMeterYearsMapper;
import cn.meiot.service.IAppMeterHoursService;
import cn.meiot.utils.CommonUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-16
 */
@Service
public class AppMeterHoursServiceImpl extends ServiceImpl<AppMeterHoursMapper, AppMeterHours> implements IAppMeterHoursService {

    @Autowired
    private AppMeterHoursMapper appMeterHoursMapper;


    @Autowired
    private PcMeterYearsMapper pcMeterYearsMapper;


    @Autowired
    private PcMeterHoursMapper pcMeterHoursMapper;

    @Autowired
    private CommonUtil commonUtil;

    @Override
    public Long getCountByCondition(Map<String, Object> map) {
        return appMeterHoursMapper.getCountByCondition(map);
    }

    @Override
    public AppMeterHours getLastInfoByCondition(Map<String, Object> map) {
        return appMeterHoursMapper.getLastInfoByCondition(map);
    }

    @Override
    public List<Map<String,Object>> getList(AppMeterVo appMeterVo) {
        return appMeterHoursMapper.getList(appMeterVo);
    }

    @Override
    public Result gettotalMeterBySerialNumber(Long userId, String serialNumber,Integer projectId) {
        Result result = Result.getDefaultFalse();

        //通过设备序列号查询你主开关编号
        Long masterSn = commonUtil.getMasterSn(serialNumber);
        if(null == masterSn){
            result.setMsg("未找到主开关sn");
            return result;
        }

        BigDecimal totalMeter = BigDecimal.ZERO;
        Long totalMeterCreateTime = null;

        if(projectId != null) {
            //企业app
            totalMeter = pcMeterYearsMapper.gettotalMeterBySerialNumber(userId,serialNumber,masterSn,projectId);
            //获取最新更新时间
            totalMeterCreateTime = pcMeterHoursMapper.getTotalMeterCreateTime(userId, serialNumber,projectId);

        }else {
            //个人app
            totalMeter = appMeterHoursMapper.gettotalMeterBySerialNumber(userId,serialNumber,masterSn);
            //获取最新更新时间
            totalMeterCreateTime = appMeterHoursMapper.getTotalMeterCreateTime(userId, serialNumber);
        }

        Map<String, Object> map = new HashMap<>();
        if(totalMeter == null) {
            totalMeter = BigDecimal.ZERO;
        }

        if(totalMeterCreateTime == null) {
            totalMeterCreateTime = System.currentTimeMillis();
        }
        map.put("totalMeter", totalMeter.setScale(1, BigDecimal.ROUND_HALF_UP));
        map.put("totalMeterCreateTime", totalMeterCreateTime);
        result = Result.getDefaultTrue();
        result.setData(map);
        return result;
    }



    /**
     *
     * @Title: selectByOne
     * @Description: 查询电量
     * @param parametersDto
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    @Override
    public Map<String, Object> selectByOne(ParametersDto parametersDto) {
        return appMeterHoursMapper.selectByOne(parametersDto);
    }


    /**
     *
     * @Title: insertByOne
     * @Description: 插入电量
     * @param parametersDto
     * @return: int
     */
    @Override
    public int insertByOne(ParametersDto parametersDto) {
        return appMeterHoursMapper.insertByOne(parametersDto);
    }



    /**
     *
     * @Title: updateByone
     * @Description: 修改电量
     * @param parametersDto
     * @return: int
     */
    @Override
    public int updateByone(ParametersDto parametersDto) {
        return appMeterHoursMapper.updateByone(parametersDto);
    }
}
