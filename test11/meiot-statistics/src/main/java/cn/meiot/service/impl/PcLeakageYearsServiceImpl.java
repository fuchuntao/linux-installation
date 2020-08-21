package cn.meiot.service.impl;

import cn.meiot.config.TableConfig;
import cn.meiot.entity.AppMeterYears;
import cn.meiot.entity.PcLeakageYears;
import cn.meiot.entity.vo.AppMeterVo;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.StatisticsDto;
import cn.meiot.mapper.PcLeakageHoursMapper;
import cn.meiot.mapper.PcLeakageMonthsMapper;
import cn.meiot.mapper.PcLeakageYearsMapper;
import cn.meiot.service.IPcLeakageYearsService;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 企业平台年数据统计表 服务实现类
 * </p>
 *
 * @author 凌志颖
 * @since 2019-10-21
 */
@Service
@Slf4j
public class PcLeakageYearsServiceImpl extends ServiceImpl<PcLeakageYearsMapper, PcLeakageYears> implements IPcLeakageYearsService {

	@Autowired
	private PcLeakageMonthsMapper pcLeakageMonthsMapper;
	
	@Autowired
	private PcLeakageYearsMapper pcLeakageYearsMapper;

	@Autowired
	private PcLeakageHoursMapper pcLeakageHoursMapper;
	
	@Override
	public Result monthStatistics(String tableName,Integer year ,Integer month) {
        List<StatisticsDto> list = null;
	    try {
            //判断查询时是否时当前月份
            //int year = cal.get(Calendar.YEAR);//获取年份
            log.info("年：{}", year);
            //int month = cal.get(Calendar.MONTH);//获取月份
            log.info("当前月：{}", month);
            StatisticsDto appMeterVo = StatisticsDto.builder().tableName(tableName).year(year).month(month).build();


            //通过设备序列号查询列表
            list =  pcLeakageMonthsMapper.selectListBySerialNumber(appMeterVo);
            if (CollectionUtils.isEmpty(list)) {
                log.info("app上个月没有设备用电:{}", tableName);
                return Result.getDefaultTrue();
            }


            ParametersDto parametersDto = ParametersDto.builder()
                    .tableName(tableName)
                    .platform(TableConfig.PC)
                    .type(0)
                    .sYear(year)
                    .sMonth(month)
                    .build();
            List<StatisticsDto> statisticsDtos = pcLeakageHoursMapper.selectStatisticsDtoList(parametersDto);

            if(!CollectionUtils.isEmpty(statisticsDtos)) {
                list.removeAll(statisticsDtos);
            }

            // this.saveBatch(list);
            pcLeakageYearsMapper.insertList(list, tableName);
            log.info("app统计月度成功！:{}", tableName, list);
            return Result.getDefaultTrue();

        }catch (Exception e){
            log.error("统计数据异常！:表名{},年份{},月份{},数据{}", tableName,year,month, list);
            log.error("错误信息：{},======[]{},=========[]{}",e.getMessage(),e.getCause(),e.getSuppressed());
            return Result.getDefaultFalse();
        }
    }
}
