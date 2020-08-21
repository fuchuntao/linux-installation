package cn.meiot.service;

import cn.meiot.entity.AppMeterHours;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-16
 */
public interface IAppMeterHoursService extends IService<AppMeterHours> {

    /**
     * 通过条件查询信息
     * @param map
     * @return
     */
    Long  getCountByCondition(Map<String, Object> map);

    /**
     * 获取上一个小时之前的数据
     * @param map
     * @return
     */
    AppMeterHours getLastInfoByCondition(Map<String, Object> map);

    /**
     * 获取列表
     * @param appMeterVo
     * @return
     */
    List<Map<String,Object>> getList(AppMeterVo appMeterVo);

    /**
     * 通过设备型号获取
     * @param userId
     * @param serialNumber
     * @return
     */
    Result gettotalMeterBySerialNumber(Long userId, String serialNumber,Integer project);



    /**
     *
     * @Title: selectByOne
     * @Description: 查询电量
     * @param parametersDto
     * @return: java.math.BigDecimal
     */
    Map<String, Object> selectByOne(ParametersDto parametersDto);



    /**
     *
     * @Title: insertByOne
     * @Description: 插入电量
     * @param parametersDto
     * @return: int
     */
    int insertByOne(ParametersDto parametersDto);


    /**
     *
     * @Title: updateByone
     * @Description: 更新电量
     * @param parametersDto
     * @return: int
     */
    int updateByone(ParametersDto parametersDto);
}
