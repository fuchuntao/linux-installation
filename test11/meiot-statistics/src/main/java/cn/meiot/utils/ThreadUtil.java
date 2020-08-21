package cn.meiot.utils;

import cn.meiot.entity.water.Record;
import cn.meiot.enums.WaterType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ThreadUtil
 * @Description: 多线程工具类
 * @author: 符纯涛
 * @date: 2020/3/24
 */
@Component
@Slf4j
public class ThreadUtil {


    @Resource(name = "taskExecutor")
    private TaskExecutor executor;

    @Autowired
    private WaterUtils waterUtils;

    /**
     *添加的接受对象
     */
    public static List<Record> recordList = null ;

    /**
     *修改的接受对象
     */
    public static List<Record> recordUpdate = null ;

    private static final String add_List_Lock = "RecordLock";

    /**
     *
     * @Title: getRecord
     * @Description: 创建多线程获取数据
     * @param map
     * @param aLong
     * @param statue
     * @return: java.util.List<cn.meiot.entity.water.Record>
     */
    public synchronized void getRecord(final Map map, final Long aLong) {
//        if(!CollectionUtils.isEmpty(ThreadUtil.recordList)) {
////           return null;
////        }
        while (!CollectionUtils.isEmpty(ThreadUtil.recordList) && !CollectionUtils.isEmpty(ThreadUtil.recordUpdate)) {

        }
        //获取total
        Integer integer = waterUtils.queryRecordTotal();
        if(integer == null || integer.equals(0)) {
            return;
        }
        recordList = new ArrayList<>(integer);
        recordUpdate = new ArrayList<>(integer);
//        List<Record> recordList = new ArrayList<>(integer);

        //线程数
        int corePoolSize = ConstantsUtil.WATER_THREAD_TOTAL;

        //单线程条数
        float floatValue = integer.floatValue();
        //总数条数/线程数
        float v = floatValue / corePoolSize;
        double size = Math.ceil(v);
        //判断开启多线程(大于10000开启)
        if(integer < ConstantsUtil.START_THREAD_TOTAL) {
            corePoolSize = 1;
            size = Math.ceil(floatValue);
        }
        CountDownLatch count = new CountDownLatch(corePoolSize);
        for(int i = 0; i < corePoolSize; ++i) {
             int finalI = i;
             int sizef = new Double(size).intValue();
             final int from = finalI*sizef;
            executor.execute(() ->{
                try {
                    map.put("from", from);
                    map.put("size", sizef);
                    System.out.println("线程名："+Thread.currentThread().getName()+"     form："+from +"   sizeof："+sizef);
                    //判断是否是由0到新增
                    if (aLong == null) {
                        //新增 statue = 0 为1是修改
                        List<Record> recordUpdate = waterUtils.getRecordByGtIdBy(map, aLong, null);
                        System.out.println("线程名："+Thread.currentThread().getName() + "         recordUpdate:"+recordUpdate);
                        addList(recordUpdate);
                    } else {
                        //添加数据
                        map.put("sort", "desc");
                        //新增 statue = 0 为1是修改
                        List<Record> recordList = waterUtils.getRecordByGtIdBy(map, aLong, 0);
                        ThreadUtil.recordList.addAll(recordList);

                        map.put("sort", "asc");
                        map.put("checked", "true");
                        //新增 statue = 0 为1是修改
                        List<Record> recordListUpdate = waterUtils.getRecordByGtIdBy(map, aLong, 1);

                        List<Record> collect = recordListUpdate.stream().filter(record -> "true".equals(record.getChecked())).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(collect)) {
                            ThreadUtil.recordUpdate.addAll(collect);
                        }
                    }
                }catch (Exception e){
                    log.error("拉取水表时创建多线程获取数据失败",e);
                    e.printStackTrace();
                }finally {
                    count.countDown();
                }
            });


//            Future<List<Record>> booleanTask = service.submit(new Callable<List<Record>>() {
//                @Override
//                public List<Record> call()  {
////                    Map map1 = new HashMap();
//                    map.put("from", from);
//                    map.put("size", sizef);
//                    //判断是否是由0到新增
//                    if(aLong == null) {
//                        //新增 statue = 0 为1是修改
//                        List<Record> recordUpdate = waterUtils.getRecordByGtIdBy(map, aLong, null);
//                        ThreadUtil.recordList.addAll(recordUpdate);
//                    }else {
//                        //添加数据
//                        map.put("sort","desc");
//                        //新增 statue = 0 为1是修改
//                        List<Record> recordList = waterUtils.getRecordByGtIdBy(map, aLong, 0);
//                        ThreadUtil.recordList.addAll(recordList);
//
//                        map.put("sort","asc");
//                        map.put("checked","true");
//                        //新增 statue = 0 为1是修改
//                        List<Record> recordListUpdate = waterUtils.getRecordByGtIdBy(map, aLong, 1);
//
//                        List<Record> collect = recordListUpdate.stream().filter(record -> "true".equals(record.getChecked())).collect(Collectors.toList());
//                        ThreadUtil.recordUpdate.addAll(collect);
//                    }
////
////                    //新增 statue = 0 为1是修改
////                    List<Record> recordUpdate = waterUtils.getRecordByGtIdBy(map, aLong, statue);
////                    ThreadUtil.recordList.addAll(recordUpdate);
////                    if(statue != null && statue == 1) {
////                        //只查询修改状态为true的数据
////                        List<Record> collect = recordUpdate.stream().filter(record -> "true".equals(record.getChecked())).collect(Collectors.toList());
////                        ThreadUtil.recordUpdate.addAll(collect);
////                    }
////                    System.out.println(recordUpdate.size());
//                    System.out.println("开始减----------");
//                    count.countDown();
//                    return null;
//                }
//            });
        }
        try {
            System.out.println("开始等待");
            count.await();
            System.out.println("结束等待。。。。。。。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private  static void  addList(List<Record> recordUpdate){
        synchronized (add_List_Lock){
            ThreadUtil.recordList.addAll(recordUpdate);
        }

    }

}


