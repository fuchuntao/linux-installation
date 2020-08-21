package cn.meiot.service.impl;

import cn.meiot.entity.WaterStatistics;
import cn.meiot.entity.WaterStatisticsMonths;
import cn.meiot.entity.WaterStatisticsYears;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.WaterStatisticsDto;
import cn.meiot.mapper.WaterStatisticsMonthsMapper;
import cn.meiot.mapper.WaterStatisticsYearsMapper;
import cn.meiot.service.IWaterStatisticsYearsService;
import cn.meiot.utils.DataUtil;
import cn.meiot.utils.MeterUtil;
import cn.meiot.utils.WaterUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fct
 * @since 2020-02-28
 */
@Service
public class WaterStatisticsYearsServiceImpl extends ServiceImpl<WaterStatisticsYearsMapper, WaterStatisticsYears> implements IWaterStatisticsYearsService {


    @Autowired
    private WaterUtil waterUtil;

    @Autowired
    private WaterStatisticsMonthsMapper waterStatisticsMonthsMapper;



    @Autowired
    private WaterStatisticsYearsMapper waterStatisticsYearsMapper;



    /**
     *
     * @Title: insertWaterMeterYearsList
     * @Description: 查询抄表记录里面的每一天最后一条数据并且计算差值,最后插入年表
     * @param
     * @return: void
     */
    @Override
    public void insertWaterMeterYearsList() {
        //查询水表编号的集合

        //查询出水表编号
        List<String> stringList = waterUtil.querySubBuildingWaterId(0L, 0, 0L);

        //对项目id 水表编号 查询抄表数据库的数据按照升序排列
        for (String meterId : stringList) {

            List<WaterStatisticsYears> waterList = new ArrayList<>();

            WaterStatisticsDto waterStatisticsDto = new WaterStatisticsDto();
            waterStatisticsDto.setMeterid(meterId);

            //查询抄表列表水表编号的数据
            List<WaterStatisticsMonths> waterMeterList = waterStatisticsMonthsMapper.selectWaterMeterMonthsList(waterStatisticsDto);
            if(CollectionUtils.isEmpty(waterMeterList)) {
                continue;
            }
            List<WaterStatisticsMonths> collect = waterMeterList.stream()
                    .sorted(Comparator.comparing(WaterStatisticsMonths::getReadtime)).collect(Collectors.toList());

            for (WaterStatisticsMonths waterStatistics : collect) {
                WaterStatisticsYears waterStatisticsMonth = new WaterStatisticsYears();
                BeanUtils.copyProperties(waterStatistics,waterStatisticsMonth);
                waterList.add(waterStatisticsMonth);
            }
            List<WaterStatisticsYears> lastRecordT = WaterUtil.findLastRecordT(waterList);
            //插入数据库
            this.saveBatch(lastRecordT);
        }
    }


    /**
     *
     * @Title: queryWaterMeterList
     * @Description: 用水占比
     * @param projectId 项目id
     * @param userId 主用户id
     * @param startTime 时间戳
     * @param type  年 月 （0 1）
     * @return: cn.meiot.entity.vo.Result
     */
    @Override
    public Result queryWaterMeterList(Integer projectId, Long userId, Long startTime, Integer type, Long buildingId) {
        //时间戳转换为具体的年月日
        Calendar calEndDataUtil = Calendar.getInstance();
        calEndDataUtil.setTimeInMillis(startTime);
        //获取年份
        int year = calEndDataUtil.get(Calendar.YEAR);
        //获取月份
        int month = calEndDataUtil.get(Calendar.MONTH) + 1;
        //根据项目id查询水表编号
        List<String> waterList = waterUtil.querySubBuildingWaterId(buildingId, projectId, userId);

        //根据type查询年月日的用水量 waterList, 项目id, 主用户id
        List<Map<String, Object>> mapList = null;
        Map<String, Object> map = new HashMap<>();
        if(!CollectionUtils.isEmpty(waterList)){

            WaterStatisticsDto waterStatisticsDto = WaterStatisticsDto.builder()
                    .year(year)
                    .month(month)
                    .waterList(waterList)
                    .projectId(projectId)
                    .userId(userId)
                    .build();
            if(type == 1) {
                //查询月表的数据
                mapList = waterStatisticsMonthsMapper.WaterMonthsListByMeterIdList(waterStatisticsDto);
            } else{
                //查询年表的数据
                mapList = waterStatisticsYearsMapper.WaterYearsListByMeterIdList(waterStatisticsDto);
                //获取月份的用水最大值平均值最小值
                map = MeterUtil.meterMaxMin(mapList,0);

                //获取平均值
                BigDecimal totalMeter = (BigDecimal) map.get("totalMeter");

                //如果年为当年则月为当月
                //获取服务器的当年
                calEndDataUtil.setTimeInMillis(System.currentTimeMillis());
                //获取年份
                int newYear = calEndDataUtil.get(Calendar.YEAR);
                //获取月份
                int newMonth = calEndDataUtil.get(Calendar.MONTH) + 1;
                int num = 12;
                if(newYear == year) {
                    num = newMonth;
                }
                map.put("averageMeter", totalMeter.divide(new BigDecimal(num), 1, BigDecimal.ROUND_HALF_UP));
            }

        }
        //补数据
        mapList = DataUtil.toDataHour(startTime, type , mapList,1);

        map.put("mapList", mapList);

        return Result.OK(map);
    }


    /**
     *
     * @Title: updateWaterMeterYearsList
     * @Description: 更新年表数据
     * @param map
     * @return: void
     */
    @Override
    @Transactional
    public void updateWaterMeterYearsList(Map<String, Object> map) {

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            //水表编号
            String meterId = entry.getKey();
            //水表更新列表
            List<WaterStatisticsMonths> value = (List<WaterStatisticsMonths>)entry.getValue();

            List<WaterStatisticsYears> waterList = new ArrayList<>();

            WaterStatisticsDto waterStatisticsDto = new WaterStatisticsDto();
            waterStatisticsDto.setMeterid(meterId);

            if(CollectionUtils.isEmpty(value)) {
                continue;
            }
            //查询开始的更新时间
            WaterStatisticsMonths waterStatisticsMonthsLast = value.get(0);

            WaterStatisticsMonths waterStatisticsMonthsFirst = value.get(value.size()-1);
            waterStatisticsDto.setStartTime(waterStatisticsMonthsLast.getReadtime());
            waterStatisticsDto.setEndTime(waterStatisticsMonthsFirst.getReadtime());

            //查询抄表列表水表编号的数据
            List<WaterStatisticsMonths> waterMeterList = waterStatisticsMonthsMapper.selectWaterMeterMonthsList(waterStatisticsDto);
            if(CollectionUtils.isEmpty(waterMeterList)) {
                continue;
            }


            List<WaterStatisticsMonths> collect = waterMeterList.stream()
                    .sorted(Comparator.comparing(WaterStatisticsMonths::getReadtime)).collect(Collectors.toList());

            for (WaterStatisticsMonths waterStatistics : collect) {
                WaterStatisticsYears waterStatisticsMonth = new WaterStatisticsYears();
                BeanUtils.copyProperties(waterStatistics,waterStatisticsMonth);
                waterList.add(waterStatisticsMonth);
            }
            List<WaterStatisticsYears> lastRecordT = WaterUtil.findLastRecordT(waterList);

            for (WaterStatisticsYears waterStatisticsYears : lastRecordT) {

                WaterStatisticsDto waterStatisticsDtoS = WaterStatisticsDto.builder()
                        .meterid(waterStatisticsYears.getMeterid())
                        .year(waterStatisticsYears.getYear())
                        .month(waterStatisticsYears.getMonth())
                        .build();
                //查询是否有数据
                WaterStatisticsYears statisticsYears = waterStatisticsYearsMapper.selectWaterYearByMeterId(waterStatisticsDtoS);
                if(statisticsYears != null) {
                    //有则更新
                    waterStatisticsYears.setRecordId(statisticsYears.getRecordId());
                    waterStatisticsYearsMapper.updateById(waterStatisticsYears);
                }else {
                    //否则添加
                    this.save(waterStatisticsYears);
                }
            }
        }
    }

}
