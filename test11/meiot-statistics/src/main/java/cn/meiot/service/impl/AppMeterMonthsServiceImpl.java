package cn.meiot.service.impl;

import cn.meiot.config.TableConfig;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.AppMeterHours;
import cn.meiot.entity.AppMeterMonths;
import cn.meiot.entity.AppMeterYears;
import cn.meiot.entity.vo.AppMeterMonthsVo;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.mapper.AppMeterHoursMapper;
import cn.meiot.mapper.AppMeterMonthsMapper;
import cn.meiot.service.IAppMeterMonthsService;
import cn.meiot.utils.CommonUtil;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.MeterUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-19
 */
@Service
@Slf4j
public class AppMeterMonthsServiceImpl extends ServiceImpl<AppMeterMonthsMapper, AppMeterMonths> implements IAppMeterMonthsService {

    @Autowired
    private AppMeterMonthsMapper appMeterMonthsMapper;

    @Autowired
    private AppMeterHoursMapper appMeterHoursMapper;

    @Autowired
    private CommonUtil commonUtil;

    private Calendar cal = Calendar.getInstance();


    @Override
    public Result getList(AppMeterVo appMeterVo) {
        List<Map<String,Object>> list= appMeterMonthsMapper.getStatslastmonthly(appMeterVo);
        //判断查询时是否时当前月份
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);//获取年份
        log.info("年：{}",year);
        int month=cal.get(Calendar.MONTH)+1;//获取月份
        log.info("当前月：{}",month);
//        //判断查询的时候是当月
//        if(year == appMeterVo.getYear() && month == appMeterVo.getMonth()){
//            int day = cal.get(Calendar.DAY_OF_MONTH);//获取当前日
//            //将当天的用电量统计好
//            appMeterVo.setDay(day);
//            BigDecimal meter = appMeterHoursMapper.getNowDayByCondition(appMeterVo);
//            log.info("当月{}日使用电量：{}",day,meter);
//            if(null == meter){
//                meter = BigDecimal.ZERO;
//            }
//            Map<String,Object> map = new HashMap<String,Object>();
//            map.put("sDay",day);
//            map.put("meter",meter);
//
//            list.add(map);
//
//        }
        BigDecimal totalMeter =  MeterUtil.addMeter(list);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("totalMeter",totalMeter);
        map.put("list",list);
        Result result = Result.getDefaultTrue();
        result.setData(map);
        return result;
    }



    @SuppressWarnings("Duplicates")
    @Override
    @Transactional
    public Result dayStatistics(Integer year,Integer month,Integer day) {
        //判断查询时是否时当前月份
        log.info("年：{}",year);
        log.info("当前月：{}",month);
        log.info("昨天：{}",day);
        AppMeterVo appMeterVo = AppMeterVo.builder().year(year).month(month).day(day).build();
        //获取昨天所有的设备号
//        List<String> serialNumbers = appMeterHoursMapper.getLastDaySerialNumber(year,month,day);
//        if(null == serialNumbers || serialNumbers.size() == 0){
//            log.info("昨天没有有设备用电");
//            return Result.getDefaultTrue();
//        }


        //通过设备序列号查询列表
        List<AppMeterMonthsVo> appMeterMonthsVos = appMeterHoursMapper.selectListBySerialNumber(appMeterVo);
        if(null == appMeterMonthsVos){
            log.info("app昨天没有有设备用电");
            return Result.getDefaultTrue();
        }


        ParametersDto parametersDto = ParametersDto.builder()
                .tableName(TableConfig.METER)
                .platform(TableConfig.APP)
                .type(1)
                .sYear(year)
                .sMonth(month)
                .sDay(day)
                .build();
        List<AppMeterMonths> list = commonUtil.appMeterMonthsVoToAppMeterMonths(parametersDto, appMeterMonthsVos);

//            this.saveBatch(list);
        list.forEach(appMeterMonths ->{
            //获取主开关编号
            try {
                appMeterMonthsMapper.insert(appMeterMonths);
            } catch (Exception e) {
                log.error("统计app前一天表app_meter_hours数据到月表中:{}",appMeterMonths);
                log.error("错误信息：{},======[]{},=========[]{}",e.getMessage(),e.getCause(),e.getSuppressed());
            }
        });
        log.info("app统计前一天成功！");
        return Result.getDefaultTrue();
    }

    @Override
    public Result pullAppDayStatistics(int year, int month, int day) {

        //判断查询时是否时当前月份
//        cal.setTime(new Date());
//        int year = cal.get(Calendar.YEAR);//获取年份
        log.info("年：{}",year);
//        int month=cal.get(Calendar.MONTH)+1;//获取月份
        log.info("月：{}",month);
//        int day = cal.get(Calendar.DAY_OF_MONTH)-1;
        log.info("天：{}",day);
        AppMeterVo appMeterVo = AppMeterVo.builder().year(year).month(month).day(day).build();

        //通过设备序列号查询列表
        List<AppMeterMonthsVo> appMeterMonthsVos = appMeterHoursMapper.selectListBySerialNumber(appMeterVo);
        if(null == appMeterMonthsVos){
            log.info("手动拉取，app昨天没有有设备用电");
            return Result.getDefaultTrue();
        }
        ParametersDto parametersDto = ParametersDto.builder()
                .tableName(TableConfig.METER)
                .platform(TableConfig.APP)
                .type(1)
                .sYear(year)
                .sMonth(month)
                .sDay(day)
                .build();
        List<AppMeterMonths> list = commonUtil.appMeterMonthsVoToAppMeterMonths(parametersDto, appMeterMonthsVos);

//            this.saveBatch(list);
        list.forEach(appMeterMonths ->{
            //获取主开关编号
            try {
                appMeterMonthsMapper.insert(appMeterMonths);
            } catch (Exception e) {
                log.error("手动拉取，统计app前一天表app_meter_hours数据到月表中:{}",appMeterMonths);
                log.error("错误信息：{},======[]{},=========[]{}",e.getMessage(),e.getCause(),e.getSuppressed());
            }
        });
        log.info("手动拉取，app统计前一天成功！");
        return Result.getDefaultTrue();
    }
}
