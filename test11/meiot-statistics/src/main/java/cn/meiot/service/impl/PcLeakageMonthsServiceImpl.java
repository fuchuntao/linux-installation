package cn.meiot.service.impl;

import cn.meiot.config.TableConfig;
import cn.meiot.entity.PcLeakageMonths;
import cn.meiot.entity.vo.ParametersDto;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.StatisticsDto;
import cn.meiot.mapper.PcLeakageHoursMapper;
import cn.meiot.mapper.PcLeakageMonthsMapper;
import cn.meiot.service.IPcLeakageMonthsService;
import cn.meiot.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 企业平台月数据统计表 服务实现类
 * </p>
 *
 * @author 凌志颖
 * @since 2019-10-21
 */
@Service
@Slf4j
public class PcLeakageMonthsServiceImpl extends ServiceImpl<PcLeakageMonthsMapper, PcLeakageMonths> implements IPcLeakageMonthsService {

	private Calendar cal = Calendar.getInstance();
	
	@Autowired
	private PcLeakageHoursMapper pcLeakageHoursMapper;
	
	@Autowired
	private PcLeakageMonthsMapper pcLeakageMonthsMapper;

	@Autowired
	private CommonUtil commonUtil;


	@Override
	public Result dayStatistics(String tableName,Integer year,Integer month,Integer day) {
        List<StatisticsDto> list = null;
        try{
            //判断查询时是否时当前月份
            //int year = cal.get(Calendar.YEAR);//获取年份
            log.info("年：{}",year);
            //int month=cal.get(Calendar.MONTH)+1;//获取月份
            log.info("当前月：{}",month);
            //int day = cal.get(Calendar.DAY_OF_MONTH)-1;
            log.info("昨天：{}",day);
            StatisticsDto statisticsDto = StatisticsDto.builder().year(year).month(month).day(day).tableName(tableName).build();
            //通过设备序列号查询列表
            list = pcLeakageHoursMapper.selectListBySerialNumber(statisticsDto);
            if(CollectionUtils.isEmpty(list)){
                log.info("pc统计月表数据尚未产生数据:{}",tableName);
                return Result.getDefaultTrue();
            }

            ParametersDto parametersDto = ParametersDto.builder()
                    .tableName(tableName)
                    .platform(TableConfig.PC)
                    .type(1)
                    .sYear(year)
                    .sMonth(month)
                    .sDay(day)
                    .build();
            List<StatisticsDto> statisticsDtos = pcLeakageHoursMapper.selectStatisticsDtoList(parametersDto);

            if(!CollectionUtils.isEmpty(statisticsDtos)) {
                list.removeAll(statisticsDtos);
            }

            pcLeakageMonthsMapper.insertList(list,tableName);
            log.info("pc统计月表表名:{}，数据：{}",tableName,list);
            return Result.getDefaultTrue();
        }catch (Exception e) {
            log.error("统计数据异常！:表名{},年份{},月份{},数据{}", tableName,year,month, list);
            log.error("错误信息：{},======[]{},=========[]{}",e.getMessage(),e.getCause(),e.getSuppressed());
            return Result.getDefaultFalse();
        }
	}
}
