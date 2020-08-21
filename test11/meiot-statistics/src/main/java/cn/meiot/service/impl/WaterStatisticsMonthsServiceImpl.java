package cn.meiot.service.impl;

import cn.meiot.entity.WaterStatistics;
import cn.meiot.entity.WaterStatisticsMonths;
import cn.meiot.entity.WaterStatisticsYears;
import cn.meiot.entity.vo.WaterStatisticsDto;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.mapper.WaterStatisticsMapper;
import cn.meiot.mapper.WaterStatisticsMonthsMapper;
import cn.meiot.service.IWaterStatisticsMonthsService;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.WaterUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;


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
@Slf4j
public class WaterStatisticsMonthsServiceImpl extends ServiceImpl<WaterStatisticsMonthsMapper, WaterStatisticsMonths> implements IWaterStatisticsMonthsService {

    @Autowired
    private DeviceFeign deviceFeign;


    @Autowired
    private WaterStatisticsMapper waterStatisticsMapper;


    @Autowired
    private WaterUtil waterUtil;

    @Autowired
    private WaterStatisticsMonthsMapper waterStatisticsMonthsMapper;

    private  Calendar calEndDataUtil = Calendar.getInstance();



    /**
     *
     * @Title: insertWaterMeterMonthsList
     * @Description: 查询抄表记录里面的每一天最后一条数据并且计算差值,最后插入
     * @param
     * @return: java.lang.Integer
     */
    @Override
    public void insertWaterMeterMonthsList() {

        //查询出水表编号
        List<String> stringList = waterUtil.querySubBuildingWaterId(0L, 0, 0L);


        if(CollectionUtils.isEmpty(stringList)) {
           log.info("水表编号为空!!");
            return;
        }
        //对项目id 水表编号 查询抄表数据库的数据按照升序排列
        for (String meterId : stringList) {

            List<WaterStatisticsMonths> waterList = new ArrayList<>();


            WaterStatisticsDto waterStatisticsDto = new WaterStatisticsDto();
            waterStatisticsDto.setMeterid(meterId);


            //查询抄表列表水表编号的数据
            List<WaterStatistics> waterMeterList = waterStatisticsMapper.selectWaterMeterList(waterStatisticsDto);
            if(CollectionUtils.isEmpty(waterMeterList)) {
                continue;
            }
            List<WaterStatistics> collect = waterMeterList.stream()
                    .sorted(Comparator.comparing(WaterStatistics::getReadtime)).collect(Collectors.toList());

            for (WaterStatistics waterStatistics : collect) {
               WaterStatisticsMonths waterStatisticsMonth = new WaterStatisticsMonths();
                BeanUtils.copyProperties(waterStatistics,waterStatisticsMonth);
                //获取抄表时间
                Long readtime = waterStatistics.getReadtime();
                calEndDataUtil.setTimeInMillis(readtime);
                //开始时间的年
                int year = calEndDataUtil.get(Calendar.YEAR);
                //月
                int month = calEndDataUtil.get(Calendar.MONTH) + 1;
                //日
                int day = calEndDataUtil.get(Calendar.DATE);
                waterStatisticsMonth.setYear(year);
                waterStatisticsMonth.setMonth(month);
                waterStatisticsMonth.setDay(day);
                waterList.add(waterStatisticsMonth);
            }

//            //获取开始的一条数据
//            WaterStatistics waterStatistics = collect.get(0);
//
//            WaterStatisticsMonths waterStatisticsMonthsOne = new WaterStatisticsMonths();
//            BeanUtils.copyProperties(waterStatistics,waterStatisticsMonthsOne);
//            //获取抄表时间
//            Long readtime = waterStatistics.getReadtime();
//            calEndDataUtil.setTimeInMillis(readtime);
//            //开始时间的年
//            int year = calEndDataUtil.get(Calendar.YEAR);
//            //月
//            int month = calEndDataUtil.get(Calendar.MONTH) + 1;
//            //日
//            int day = calEndDataUtil.get(Calendar.DATE);
//            waterStatisticsMonthsOne.setYear(year);
//            waterStatisticsMonthsOne.setMonth(month);
//            waterStatisticsMonthsOne.setDay(day);


            //获取该水表编号一天中最后一条数据
            waterList = WaterUtil.findLastRecord(waterList);


            //计算差值
            for(int i = 0; i < waterList.size(); i++) {
                //获取当前的值
                WaterStatisticsMonths waterStatisticsMonths = waterList.get(i);
                BigDecimal readcount = waterStatisticsMonths.getReadcount();
                //上一次的值
                BigDecimal readcountLast = BigDecimal.ZERO;
                if(readcount == null) {
                    continue;
                }else {
                    //判断第一次的数据
                    if(i == 0){
//                    && waterStatisticsMonthsOne != null
//                            && waterStatisticsMonths.equals(waterStatisticsMonthsOne)) {
//
//
//                        if(!waterStatisticsMonths.getId().equals(waterStatisticsMonthsOne.getId())) {
//                            readcount = readcount.subtract(waterStatisticsMonthsOne.getReadcount());
//                        }
                        waterStatisticsMonths.setWater(readcount);
                        continue;
                    }
                    for(int j = i-1;j >= 0;j--){
                        //判断是否为null 和0
                        WaterStatisticsMonths waterStatisticsMonthsLast = waterList.get(j);
                        readcountLast = waterStatisticsMonthsLast.getReadcount();

                        //readcountLast.compareTo(BigDecimal.ZERO) != 0
                        if(readcountLast != null){
                            //存入差值
                            BigDecimal subtract = readcount.subtract(readcountLast);
                            waterStatisticsMonths.setWater(subtract);
                            break;
                        }
                    }
                }
            }
            //插入数据库
            this.saveBatch(waterList);
        }
    }

    @Override
    @Transactional
    public Map updateWaterMonths(Map map) {
        //拉取更新操作
        //修改
        List<WaterStatistics> updateWaterRecordList = (List<WaterStatistics>) map.get("updateWaterRecord");
        //修改的数据
        Map<String, Object> mapYearList = new HashMap<>();

        if(!CollectionUtils.isEmpty(updateWaterRecordList)) {
            //根据水表编号分组
            Map<String, List<WaterStatistics>> collect = updateWaterRecordList.stream()
                    .collect(Collectors.groupingBy(WaterStatistics::getMeterid));



            //遍历水表编号
            for(Map.Entry<String, List<WaterStatistics>> entry : collect.entrySet()){

                List<WaterStatisticsMonths> waterList = new ArrayList<>();
                //水表编号
                String mapKey = entry.getKey();
                List<WaterStatistics> mapValue = entry.getValue();
                //获取抄表时间排序的队列
                mapValue = mapValue.stream()
                        .sorted(Comparator.comparing(WaterStatistics::getReadtime)).collect(Collectors.toList());

                //时间最小值的
                WaterStatistics waterStatisticsFirst = mapValue.get(0);

                //时间最大值的
                WaterStatistics waterStatisticsLast= mapValue.get(mapValue.size() - 1);

                //需要更新数据的时间段
                WaterStatisticsDto build = WaterStatisticsDto.builder()
                        .meterid(mapKey)
                        .startTime(waterStatisticsFirst.getReadtime())
                        .endTime(waterStatisticsLast.getReadtime())
                        .build();

                //获取抄表列表更新的数据
                List<WaterStatistics> updateWaterStatisticsList = waterStatisticsMapper.selectWaterMeterList(build);

                for (WaterStatistics waterStatistics : updateWaterStatisticsList) {
                    WaterStatisticsMonths waterStatisticsMonth = new WaterStatisticsMonths();
                    BeanUtils.copyProperties(waterStatistics,waterStatisticsMonth);
                    //获取抄表时间
                    Long readtime = waterStatistics.getReadtime();
                    calEndDataUtil.setTimeInMillis(readtime);
                    //开始时间的年
                    int year = calEndDataUtil.get(Calendar.YEAR);
                    //月
                    int month = calEndDataUtil.get(Calendar.MONTH) + 1;
                    //日
                    int day = calEndDataUtil.get(Calendar.DATE);
                    waterStatisticsMonth.setYear(year);
                    waterStatisticsMonth.setMonth(month);
                    waterStatisticsMonth.setDay(day);
                    waterList.add(waterStatisticsMonth);
                }
                //获取该水表编号一天中最后一条数据
                waterList = WaterUtil.findLastRecord(waterList);

                //获取该水表编号上条的数据（为基准值）
                WaterStatistics water = waterStatisticsMapper.selectWater(build,1);
//            BigDecimal bigDecimal = water.getReadcount();

                BigDecimal readcountLast = BigDecimal.ZERO;
                //计算差值
                for(int i = 0; i < waterList.size(); i++) {
                    //获取当前的值
                    WaterStatisticsMonths waterStatisticsMonths = waterList.get(i);
                    BigDecimal readcount = waterStatisticsMonths.getReadcount();
                    if(water != null) {
                        //上一次的值
                        readcountLast = water.getReadcount();
                    }
                    if(readcount == null) {
                        continue;
                    }else {
                        if(i == 0) {
                            readcount = readcount.subtract(readcountLast);
                            waterStatisticsMonths.setWater(readcount);
                            continue;
                        }
                        for(int j = i-1;j >= 0;j--){
                            //判断是否为null 和0
                            WaterStatisticsMonths waterStatisticsMonthsLast = waterList.get(j);
                            readcountLast = waterStatisticsMonthsLast.getReadcount();

                            //readcountLast.compareTo(BigDecimal.ZERO) != 0
                            if(readcountLast != null){
                                //存入差值
                                BigDecimal subtract = readcount.subtract(readcountLast);
                                waterStatisticsMonths.setWater(subtract);
                                break;
                            }
                        }
                    }
                }
                //修改数据库
                waterStatisticsMonthsMapper.updateWaterMeter(waterList);
                //添加修改数据
                mapYearList.put(mapKey, waterList);
            }
        }
        Map updateAndInsertWater = insertWater(map, mapYearList);
        return updateAndInsertWater;

    }

    /**
     *
     * @Title: insertWater
     * @Description: 添加水表数据
     * @param map
     * @return: void
     */
    public Map insertWater(Map map, Map mapYearList) {
        Map<String, Object> updateAndInsertWater = new HashMap<>();
        updateAndInsertWater.put("updateWater", mapYearList);
        Map<String, Object> insertWater = new HashMap<>();

        //添加
        List<WaterStatistics> updateWaterRecordList = new ArrayList<>();
        if(map != null && map.get("addWaterRecord") != null) {
            updateWaterRecordList = (List<WaterStatistics>) map.get("addWaterRecord");
            //根据水表编号分组
            Map<String, List<WaterStatistics>> collect = updateWaterRecordList.stream()
                    .collect(Collectors.groupingBy(WaterStatistics::getMeterid));

            //遍历水表编号
            for(Map.Entry<String, List<WaterStatistics>> entry : collect.entrySet()){

                List<WaterStatisticsMonths> waterList = new ArrayList<>();
                //水表编号
                String mapKey = entry.getKey();
                List<WaterStatistics> mapValue = entry.getValue();

                if(CollectionUtils.isEmpty(mapValue)) {
                    log.info("水表编号为空!!");
                    continue;
                }
                //获取抄表时间排序的队列
                mapValue = mapValue.stream()
                        .sorted(Comparator.comparing(WaterStatistics::getReadtime)).collect(Collectors.toList());


                WaterStatistics waterStatisticsFirst = mapValue.get(0);
                WaterStatistics waterStatisticsLast= mapValue.get(mapValue.size() - 1);

                //需要更新数据的时间段
                WaterStatisticsDto build = WaterStatisticsDto.builder()
                        .meterid(mapKey)
                        .startTime(waterStatisticsFirst.getReadtime())
                        .build();

                //获取更新的数据
                //List<WaterStatistics> updateWaterStatisticsList = waterStatisticsMapper.selectWaterMeterList(build);



                WaterStatisticsMonths waterStatisticsMonthW = new WaterStatisticsMonths();
                //获取该水表编号上一条最近的数据（为基准值）
                WaterStatistics water = waterStatisticsMapper.selectWater(build,0);
                if(water != null) {
                    BeanUtils.copyProperties(water,waterStatisticsMonthW);

                    //获取抄表时间
                    Long readtime = water.getReadtime();
                    calEndDataUtil.setTimeInMillis(readtime);
                    //开始时间的年
                    int year = calEndDataUtil.get(Calendar.YEAR);
                    //月
                    int month = calEndDataUtil.get(Calendar.MONTH) + 1;
                    //日
                    int day = calEndDataUtil.get(Calendar.DATE);
                    waterStatisticsMonthW.setYear(year);
                    waterStatisticsMonthW.setMonth(month);
                    waterStatisticsMonthW.setDay(day);
                }


//            BigDecimal bigDecimal = water.getReadcount();


                //用于更新的数据
                List<WaterStatisticsMonths> updateList = new ArrayList<>();


                for (WaterStatistics waterStatistics : mapValue) {
                    WaterStatisticsMonths waterStatisticsMonth = new WaterStatisticsMonths();
                    BeanUtils.copyProperties(waterStatistics,waterStatisticsMonth);
                    //获取抄表时间
                    Long readtime = waterStatistics.getReadtime();
                    calEndDataUtil.setTimeInMillis(readtime);
                    //开始时间的年
                    int year = calEndDataUtil.get(Calendar.YEAR);
                    //月
                    int month = calEndDataUtil.get(Calendar.MONTH) + 1;
                    //日
                    int day = calEndDataUtil.get(Calendar.DATE);
                    waterStatisticsMonth.setYear(year);
                    waterStatisticsMonth.setMonth(month);
                    waterStatisticsMonth.setDay(day);

                    updateList.add(waterStatisticsMonth);
                }

                //用于更新的数据
                List<WaterStatisticsMonths> updateListBack = new ArrayList<>();
                updateListBack.addAll(updateList);

                //获取该水表编号上条有数据的记录（为基准值）
                WaterStatistics waterLast = waterStatisticsMapper.selectWater(build,1);
                BigDecimal bigDecimal = BigDecimal.ZERO;
                if(waterLast != null && waterLast.getReadcount() != null) {
                    bigDecimal = waterLast.getReadcount();
                }

                //获取该水表编号一天中最后一条数据
                waterList =WaterUtil.findLastRecord(updateList);
                //获取第一条数据
                WaterStatisticsMonths waterStatisticsMonths1 = waterList.get(0);

                //判断是否跟最新数据库查询的数据是同一天
                if(waterStatisticsMonthW != null && waterStatisticsMonthW.equals(waterStatisticsMonths1)) {
                    //则为修改数据
                    waterList.remove(0);
                    //最新抄表水量
                    BigDecimal readcount = waterStatisticsMonths1.getReadcount();
                    if( readcount != null) {
                        bigDecimal = readcount;
                        //查询除今天之前的最近的抄表读数
                        Long readtime = waterStatisticsMonths1.getReadtime();

                        //上一天
                        Calendar calStartDataUtil = Calendar.getInstance();
                        calStartDataUtil.setTimeInMillis(readtime);

                        //上一天
                        calStartDataUtil.add(Calendar.DAY_OF_MONTH, -1);
                        //将小时至0
                        calStartDataUtil.set(Calendar.HOUR_OF_DAY, 23);
                        //将分钟至0
                        calStartDataUtil.set(Calendar.MINUTE, 59);
                        //将秒至0
                        calStartDataUtil.set(Calendar.SECOND,59);

                        build.setStartTime(calStartDataUtil.getTimeInMillis());
                        WaterStatistics updateWater = waterStatisticsMapper.selectWater(build,1);
                        BigDecimal waterReadcount = BigDecimal.ZERO;
                        if(updateWater != null) {
                            waterReadcount = updateWater.getReadcount();
                        }

                        //计算差值
                        waterStatisticsMonths1.setWater(readcount.subtract(waterReadcount));

                    }else if(readcount == null && waterStatisticsMonthW.getWater() != null) {

                        //设置水量
                        waterStatisticsMonths1.setWater(waterStatisticsMonthW.getWater());
                        //设置读数
                        waterStatisticsMonths1.setReadcount(waterStatisticsMonthW.getReadcount());
                    }


                    WaterStatisticsDto waterStatisticsDto = WaterStatisticsDto.builder()
                            .year(waterStatisticsMonths1.getYear())
                            .month(waterStatisticsMonths1.getMonth())
                            .day(waterStatisticsMonths1.getDay())
                            .meterid(waterStatisticsMonths1.getMeterid())
                            .build();
//                //年月日设备编号查询月表
                    WaterStatisticsMonths waterStatisticsMonths = waterStatisticsMonthsMapper.selectWaterByMeterId(waterStatisticsDto);

                    if(waterStatisticsMonths != null && waterStatisticsMonths.getReadcount() != null
                            && waterStatisticsMonths.getReadtime() < waterStatisticsMonths1.getReadtime()) {
                        //修改
                        waterStatisticsMonths1.setRecordId(waterStatisticsMonthW.getRecordId());
                        //修改同一天的数据
                        waterStatisticsMonthsMapper.updateById(waterStatisticsMonths1);
                    }else if(waterStatisticsMonths == null){
                        this.save(waterStatisticsMonths1);
                    }
                }

                //计算差值
                for(int i = 0; i < waterList.size(); i++) {
                    //获取当前的值
                    WaterStatisticsMonths waterStatisticsMonths = waterList.get(i);
                    BigDecimal readcount = waterStatisticsMonths.getReadcount();
                    //上一次的值
                    BigDecimal readcountLast = bigDecimal;
                    if(readcount == null) {
                        continue;
                    }else {
                        if(i == 0) {
                            readcount = readcount.subtract(bigDecimal);
                            waterStatisticsMonths.setWater(readcount);
                            continue;
                        }
                        for(int j = i-1;j >= 0;j--){
                            //判断是否为null 和0
                            WaterStatisticsMonths waterStatisticsMonthsLast = waterList.get(j);
                            readcountLast = waterStatisticsMonthsLast.getReadcount();

                            //readcountLast.compareTo(BigDecimal.ZERO) != 0
                            if(readcountLast != null){
                                //存入差值
                                BigDecimal subtract = readcount.subtract(readcountLast);
                                waterStatisticsMonths.setWater(subtract);
                                break;
                            }
                        }
                    }
                }
                //修改数据库
                this.saveBatch(waterList);
                //添加修改数据
                insertWater.put(mapKey, updateListBack);
            }
        }
        updateAndInsertWater.put("insertWater", insertWater);
        return updateAndInsertWater;
    }

}
