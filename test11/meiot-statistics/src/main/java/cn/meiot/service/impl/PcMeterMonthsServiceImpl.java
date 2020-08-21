package cn.meiot.service.impl;

import cn.meiot.config.TableConfig;
import cn.meiot.entity.AppMeterMonths;
import cn.meiot.entity.PcMeterMonths;
import cn.meiot.entity.vo.*;
import cn.meiot.mapper.PcMeterHoursMapper;
import cn.meiot.mapper.PcMeterMonthsMapper;
import cn.meiot.mapper.PcMeterYearsMapper;
import cn.meiot.service.IPcMeterMonthsService;
import cn.meiot.utils.CommonUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 企业平台月统计表 服务实现类
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
@Slf4j
@Service
public class PcMeterMonthsServiceImpl extends ServiceImpl<PcMeterMonthsMapper, PcMeterMonths> implements IPcMeterMonthsService {

    @Autowired
    private PcMeterMonthsMapper pcMeterMonthsMapper;

    @Autowired
    private PcMeterHoursMapper pcMeterHoursMapper;

    @Autowired
    private PcMeterYearsMapper pcMeterYearsMapper;

    @Autowired
    private CommonUtil commonUtil;

    private Calendar cal = Calendar.getInstance();


    @Override
    public Result getPcList(AppMeterVo appMeterVo) {
        return null;
    }

    @Override
    @Transactional
    public Result dayStatisticsPc(int year, int month, int day) {

        //判断查询时是否时当前月份
        log.info("年：{}",year);
        log.info("当前月：{}",month);
        log.info("昨天：{}",day);
        AppMeterVo appMeterVo = AppMeterVo.builder().year(year).month(month).day(day).build();

        //通过设备序列号查询列表
        List<AppMeterMonthsVo> appMeterMonthsVoList = pcMeterHoursMapper.selectMeterListBySerialNumberPc(appMeterVo);

        if(null == appMeterMonthsVoList){
            log.info("企业昨天没有有设备用电");
            return Result.getDefaultTrue();
        }

        ParametersDto parametersDto = ParametersDto.builder()
                .tableName(TableConfig.METER)
                .platform(TableConfig.PC)
                .type(1)
                .sYear(year)
                .sMonth(month)
                .sDay(day)
                .build();
        List<PcMeterMonths> list = commonUtil.appMeterMonthsVoToPcMeterMonths(parametersDto, appMeterMonthsVoList);


//            this.saveBatch(list);
        list.forEach(pcMeterMonths ->{
            //获取主开关编号
            try {
                pcMeterMonthsMapper.insert(pcMeterMonths);
            } catch (Exception e) {
                log.error("统计企业前一天表pc_meter_hours数据到月表中:{}",pcMeterMonths);
                log.error("错误信息：{},======[]{},=========[]{}",e.getMessage(),e.getCause(),e.getSuppressed());
            }
        });
        log.info("企业pc统计前一天成功！");
        return Result.getDefaultTrue();
    }


    @Override
    public Result pullPcDayStatistics(int year, int month, int day) {
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
        List<AppMeterMonthsVo> appMeterMonthsVoList = pcMeterHoursMapper.selectMeterListBySerialNumberPc(appMeterVo);

        if(null == appMeterMonthsVoList){
            log.info("企业昨天没有有设备用电");
            return Result.getDefaultTrue();
        }
        ParametersDto parametersDto = ParametersDto.builder()
                .tableName(TableConfig.METER)
                .platform(TableConfig.PC)
                .type(1)
                .sYear(year)
                .sMonth(month)
                .sDay(day)
                .build();
        List<PcMeterMonths> list = commonUtil.appMeterMonthsVoToPcMeterMonths(parametersDto, appMeterMonthsVoList);

//            this.saveBatch(list);
        list.forEach(pcMeterMonths ->{
            //获取主开关编号
            try {
                pcMeterMonthsMapper.insert(pcMeterMonths);
            } catch (Exception e) {
                log.error("手动拉取,统计企业前一天表pc_meter_hours数据到月表中:{}",pcMeterMonths);
                log.error("错误信息：{},======[]{},=========[]{}",e.getMessage(),e.getCause(),e.getSuppressed());
            }
        });
        log.info("手动拉取，企业pc统计前一天成功！");
        return Result.getDefaultTrue();
    }


    /**
     *
     * @Title: pcMonthAndDayStatistics
     * @Description: 查询当月和当天的电量
     * @param serialNumber
     * @param masterSn
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     */
    @Override
    public Map<String, Object> pcMonthAndDayStatistics(String serialNumber, Long masterSn, Integer projectId) {

        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);//获取年份
        int month = cal.get(Calendar.MONTH) + 1;//获取月份
        int day = cal.get(Calendar.DAY_OF_MONTH);

        Map<String, Object> map = new HashMap<>();
        map.put("day", 0);
        map.put("month", 0);
        List<SerialNumberMasterVo> list = new ArrayList<>();
        SerialNumberMasterVo serialNumberMasterVo = new SerialNumberMasterVo();
        serialNumberMasterVo.setSerialNumber(serialNumber);
        serialNumberMasterVo.setMasterSn(masterSn);
        list.add(serialNumberMasterVo);
        if(!CollectionUtils.isEmpty(list)) {
            //查询当月的天
            Map<String, Object> mapDay = pcMeterYearsMapper.queryNowPcMeterByMonth(list, year, month, day, projectId);
            if(mapDay == null) {
                log.info("当天没有用电");
            }else {
                Object value = mapDay.get("value");
                if(value != null) {
                    map.put("day", value);
                }
            }
            //查询当年的月
            Map<String, Object> mapMonth = pcMeterYearsMapper.queryNowPcMeterByMasterIndex(list, year, month, projectId);
            if(mapMonth == null) {
                log.info("当月没有用电");
            }else {
                Object value = mapMonth.get("value");
                if(value != null) {
                    map.put("month", value);
                }
            }
        }else {
            log.info("传入参数为空：{}{}" ,list,serialNumberMasterVo);
        }
        return map;
    }

    /**
     *
     * @Title: appMonthStatistics
     * @Description: 查询企业app的当月电量
     * @param serialNumber
     * @param masterSn
     * @param projectId
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     */
    @Override
    public Result appMonthStatistics(String serialNumber, Integer projectId, Long startTime) {
//        cal.setTime(new Date());
//        int year = cal.get(Calendar.YEAR);//获取年份
//        int month = cal.get(Calendar.MONTH) + 1;//获取月份
//        int day = cal.get(Calendar.DAY_OF_MONTH);
        //开始时间
        Calendar calStartDataUtil = Calendar.getInstance();
        calStartDataUtil.setTimeInMillis(startTime);
        //开始时间的年
        int year = calStartDataUtil.get(Calendar.YEAR);
        //月
        int month = calStartDataUtil.get(Calendar.MONTH) + 1;

//        Map<String, Object> map = new HashMap<>();
        BigDecimal value =BigDecimal.ZERO ;
//        map.put("month", month);
//        List<SerialNumberMasterVo> list = new ArrayList<>();
        SerialNumberMasterVo serialNumberMasterVo = new SerialNumberMasterVo();
        serialNumberMasterVo.setSerialNumber(serialNumber);

        Long aLong = commonUtil.getMasterSn(serialNumber);
        if(aLong == null) {
            log.info("企业app首页查询当月电量，获取根据设备号主开关sn为空");
        }
        serialNumberMasterVo.setMasterSn(aLong);
//        list.add(serialNumberMasterVo);
        if(serialNumberMasterVo != null) {
            //查询当年的月
            BigDecimal meter = pcMeterYearsMapper.appMeterByMasterIndex(serialNumberMasterVo, year, month, projectId);
            if(meter == null) {
                log.info("当月没有用电");
            }else {
                if(meter != null) {
                    value = meter;
                }
            }
        }else {
            log.info("传入参数为空：{}{}" ,serialNumberMasterVo);
        }

        Result result = Result.getDefaultTrue();
        result.setData(value.setScale(1, BigDecimal.ROUND_HALF_UP));
        return result;
    }
}
