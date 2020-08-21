package cn.meiot.utils;

import cn.meiot.entity.WaterStatisticsMonths;
import cn.meiot.entity.WaterStatisticsYears;
import cn.meiot.entity.water.Record;
import cn.meiot.feign.DeviceFeign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: WaterUtil
 * @Description: 水务工具类
 * @author: 符纯涛
 * @date: 2020/5/6
 */
@Component
@Slf4j
public class WaterUtil<T> {

    @Autowired
    private DeviceFeign deviceFeign;


    /**
     *
     * @Title: querySubBuildingWaterId
     * @Description: 查询水表设备编号
     * @param id 组织架构id
     * @param projectId 项目id
     * @param userId 主用户id
     * @return: java.util.List<java.lang.String>
     */
    public List<String> querySubBuildingWaterId(Long id, Integer projectId, Long userId) {
        //查询出水表编号
        List<String> stringList = deviceFeign.querySubBuildingWaterId(id, projectId, userId);
        if (CollectionUtils.isEmpty(stringList)) {
            log.info("根据项目id获取水表编号为空!!");
            return null;

        }
        return stringList;

    }



    /**
     *
     * @Title: findLastRecord
     * @Description: 获取该水表编号一天中最后一条数据
     * @param waterList
     * @return: java.util.List<org.apache.poi.ss.formula.functions.T>
     */
    public static <T>List<T> findLastRecord(List<T> waterList) {
        for(int i = 0;i<waterList.size();i++){
            //循环list
            for(int j = i+1;j<waterList.size();j++){
                if(waterList.get(i).equals(waterList.get(j))){
                    waterList.remove(i);
                    //删除一样的元素
                    i--;
                    break;
                }
            }
        }
        return waterList;
    }


    /**
     *
     * @Title: findLastRecordT
     * @Description: 水表天差值相加的值为月表
     * @param waterList
     * @return: java.util.List<cn.meiot.entity.WaterStatisticsYears>
     */
    public static List<WaterStatisticsYears> findLastRecordT(List<WaterStatisticsYears> waterList) {
        for(int i = 0;i<waterList.size();i++){
            //循环list
            for(int j = i+1;j<waterList.size();j++){
                if(waterList.get(i).equals(waterList.get(j))){
                    WaterStatisticsYears waterStatisticsYears = waterList.get(i);
                    WaterStatisticsYears waterStatisticsYearsLast = waterList.get(j);
                    //获取前一条的用水量
                    BigDecimal water = waterStatisticsYears.getWater();

                    //获取后一条的用水量
                    BigDecimal waterLast = waterStatisticsYearsLast.getWater();
                    if(water == null) {
                        water = BigDecimal.ZERO;
                    }
                    if(waterLast == null) {
                        waterLast = BigDecimal.ZERO;
                    }
                    waterStatisticsYearsLast.setWater(waterLast.add(water));
                    waterList.remove(i);
//                    删除一样的元素
                    i--;
                    break;
                }
            }
        }
        return waterList;
    }


    public static void main(String[] args) {


//        List<WaterStatisticsYears> strings = new ArrayList<>();
        Map<String, WaterStatisticsYears> strings = new HashMap<>();
//        Set<WaterStatisticsMonths> strings = new HashSet<>();

        WaterStatisticsYears waterStatisticsMonths = new WaterStatisticsYears();
        waterStatisticsMonths.setYear(2019);
//        waterStatisticsMonths.setDay(28);
        waterStatisticsMonths.setWater(new BigDecimal("5.00"));
        waterStatisticsMonths.setMonth(4);
        waterStatisticsMonths.setUserId(1L);
        waterStatisticsMonths.setProjectId(1);
        waterStatisticsMonths.setId(5L);
        strings.put("1",waterStatisticsMonths);
//        strings.add(waterStatisticsMonths);


        WaterStatisticsYears waterStatisticsMonths2 = new WaterStatisticsYears();
        waterStatisticsMonths2.setYear(2019);
//        waterStatisticsMonths2.setDay(28);
        waterStatisticsMonths2.setWater(new BigDecimal("2.00"));
        waterStatisticsMonths2.setMonth(4);
        waterStatisticsMonths2.setUserId(1L);
        waterStatisticsMonths2.setProjectId(1);
        waterStatisticsMonths2.setId(2L);
//        strings.add(waterStatisticsMonths2);
        strings.put("1",waterStatisticsMonths2);


        WaterStatisticsYears waterStatisticsMonths3 = new WaterStatisticsYears();
        waterStatisticsMonths3.setYear(2019);
//        waterStatisticsMonths3.setDay(29);
        waterStatisticsMonths3.setWater(new BigDecimal("8.00"));
        waterStatisticsMonths3.setMonth(4);
        waterStatisticsMonths3.setUserId(1L);
        waterStatisticsMonths3.setProjectId(1);
        waterStatisticsMonths3.setId(8L);
        strings.put("1",waterStatisticsMonths3);
//        strings.add(waterStatisticsMonths3);

//        List<WaterStatisticsYears> lastRecordT = findLastRecordT(strings);

//        System.out.println(lastRecordT);

//        lastRecordT.forEach(f -> System.out.println(f));
//
//
//        List<Long> strings = new ArrayList<>();
//
//
//        List<Record> list = new ArrayList<>();
//        Record record1 = new Record();
//        record1.setId(1L);
//        list.add(record1);
//
//        Record record2 = new Record();
//        record2.setId(2L);
//        list.add(record2);
////        record1.setId(12L);
////        list.add(record1);
//
//
//
//
//        strings.add(123L);
//        strings.add(1L);
//
//
//
//        for(int i = list.size() -1 ;i>= 0 ;i--){
//            Record record = list.get(i);
//            Long id = record.getId();
//            if(strings.contains(id)){
//                list.remove(i);
////               strings.remove(meterid);
//            }
//        }

        System.out.println("list==========="+strings);

    }
}
