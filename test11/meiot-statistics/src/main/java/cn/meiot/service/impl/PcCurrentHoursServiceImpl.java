package cn.meiot.service.impl;

import cn.meiot.entity.PcCurrentHours;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.mapper.PcCurrentHoursMapper;
import cn.meiot.service.IPcCurrentHoursService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 企业平台天数据统计表 服务实现类
 * </p>
 *
 * @author fuchuntao
 * @since 2020-07-17
 */
@Service
public class PcCurrentHoursServiceImpl extends ServiceImpl<PcCurrentHoursMapper, PcCurrentHours> implements IPcCurrentHoursService {


    @Autowired
    private PcCurrentHoursMapper pcCurrentHoursMapper;
    /**
     *
     * @Title: currentData
     * @Description: 企业端首页获取近12个小时的电流
     * @param pcCurrentHours
     * @param startTime
     * @param endTime
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    public Result currentData(PcCurrentHours pcCurrentHours, Long startTime, Long endTime) {
        List<Map<String, Object>> mapList = pcCurrentHoursMapper.currentData(pcCurrentHours, startTime, endTime);
        return Result.OK(mapList);
    }


    /**
     *
     * @Title: queryStatisticsByDay
     * @Description:  统计天的数据 type = 2
     * @param parametersDto
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    public List<Map<String,Object>> queryStatisticsByDay(ParametersDto parametersDto) {
        Map map = new HashMap();
        //查询数据
        Calendar cal = Calendar.getInstance();
        Integer type = parametersDto.getType();
        cal.setTimeInMillis(parametersDto.getTime());
        int year = cal.get(Calendar.YEAR);//获取年份
        parametersDto.setYears(year);
        int month = cal.get(Calendar.MONTH) + 1;//获取月份
        parametersDto.setMonths(month);
        int day = cal.get(Calendar.DATE);//获取日
        parametersDto.setHours(day);
//        cal.setTimeInMillis(System.currentTimeMillis());
//        int nowYear = cal.get(Calendar.YEAR);//获取当前年份
//        int nowMonth = cal.get(Calendar.MONTH) + 1;//获取当前月份
//        int nowDay = cal.get(Calendar.DATE);//获取当日
        List<Map<String, Object>> mapList = pcCurrentHoursMapper.queryStatisticsByDay(parametersDto);
        return mapList;
    }


    /**
     *
     * @Title: queryMeterByType
     * @Description: 如果年相等则统计电量要统计到当月的  不包含当天的电量
     * @param time
     * @param type 0：年， 1：月， 2：日
     * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     */
    @Override
    public Map<String, Object> queryMeterByType(ParametersDto parametersDto) {
        //如果是当年的话
        Map<String, Object> map = null;
        //查询数据
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(parametersDto.getTime());
        int year = cal.get(Calendar.YEAR);//获取年份
        int month = cal.get(Calendar.MONTH) + 1;//获取月份
        int day = cal.get(Calendar.DATE);//获取日
        //判断是否是当年并且是否为年类型
        cal.setTimeInMillis(System.currentTimeMillis());
        //获取当前年份
        int nowYear = cal.get(Calendar.YEAR);
        if(0 == parametersDto.getType() && nowYear == year) {
            ParametersDto parametersDtoNow = new ParametersDto();
            BeanUtils.copyProperties(parametersDto, parametersDtoNow);
            parametersDtoNow.setSwitchSn(null);
            parametersDtoNow.setSwitchSnList(null);
            map = pcCurrentHoursMapper.queryMeterByType(parametersDtoNow);
            map.put("name", month);
        }
        return map;
    }
}
