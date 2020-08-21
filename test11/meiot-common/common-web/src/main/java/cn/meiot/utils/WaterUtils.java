package cn.meiot.utils;

import cn.meiot.config.WaterConstart;
import cn.meiot.entity.water.Customer;
import cn.meiot.entity.water.Imeter;
import cn.meiot.entity.water.Record;
import cn.meiot.entity.water.WaterAuth;
import cn.meiot.enums.WaterType;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WaterUtils {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private WaterConstart waterConstart;

    /**
     *
     * @param isRefresh 是否刷新
     * @return
     */
    public String getToken(boolean isRefresh){
        if(isRefresh){
            redisTemplate.delete(RedisConstantUtil.WATER_TOKEN);
        }else{
            String token = (String)redisTemplate.opsForValue().get(RedisConstantUtil.WATER_TOKEN);
            if(StringUtils.isNotEmpty(token)){
                return token;
            }
        }
        Map<String, Object> params= new HashMap<>();
        params.put("username", waterConstart.getUsername());
        params.put("ccode",waterConstart.getCcode());
        params.put("password", waterConstart.getPassword());
        String s = HttpUtils.sendPost(WaterConstart.LOGIN, params);
        WaterAuth parse = JSONObject.parseObject(s,WaterAuth.class);
        redisTemplate.opsForValue().set(RedisConstantUtil.WATER_TOKEN,parse.getToken(),72, TimeUnit.HOURS);
        return parse.getToken();
    }

    /**
     *
     * @param tClass  Customer 客户列表  Imeter 获取智能是水表信息 record 抄表列表
     * @param type
     * @param map 只有 WaterType.RECORD才生效
     *          starttime 开始时间毫秒时间戳 ，endtime 结束时间 ， meterid水表编号 ， deviceid 设备编号 ，ccid客户编号
     *            checked 是否已核对 [all,true,false]  ， order 默认为readtime按读表时间排序
     *            sort  升序或降序 [asc,desc]
     * @return
     */
    public <T> List<T> getCustomer(Class<T> tClass,WaterType type,Map map){
        Integer from = 0 ;
        Integer size = 1000;
        int total ;

        String token = getToken(false);
        List<T> customerList = new ArrayList<>();
        if(map == null){
            map = new HashMap();
        }else {
            from = map.containsKey("from") ? (Integer) map.get("from") : from;
            size = map.containsKey("size") ? (Integer) map.get("size") : size;
        }


        //地址
        String url = null;
        //参数名
        String dataName = null;
        if(tClass.equals(Customer.class)){
            url = WaterConstart.CUSTOMER;
            dataName = "customer";
        }else if(tClass.equals(Imeter.class)){
            url = WaterConstart.IMETER;
            dataName = "meter";
        }else if(tClass.equals(Record.class) ){
            url = WaterConstart.RECORD;
            dataName = "record";
        }
        while (true) {
            map.put("from", from*size);
            map.put("size", size);
//            log.info("抄表列表=========");
            String s = HttpUtils.sendPost( url+ token, map);
            Map result = JSONObject.parseObject(s, Map.class);
            String status = (String) result.get("status");
            if (StringUtils.isEmpty(status) || "error".equals(status)) {
                break;
            }
            JSONArray data = (JSONArray) result.get(dataName);
            List<T> ts = JSONArray.parseArray(data.toJSONString(), tClass);
            if(!CollectionUtils.isEmpty(ts)){
                customerList.addAll((Collection<? extends T>) ts);
            }
            total = (Integer) result.get("total");
            int toalSize = total/size ;
            if(toalSize <= from ){
                break;
            }
            from ++;
       }
        return customerList;
    }

    public Record queryRecordOne(String meterId){
        Map map = new HashMap();
        map.put("from", 0);
        map.put("size", 1);
        map.put("meterid",meterId);
        map.put("order","id");
        String token = getToken(false);
        //地址
        String url = WaterConstart.RECORD;
        //参数名
        String dataName = "record";
        String s = HttpUtils.sendPost( url+ token, map);
        Map result = JSONObject.parseObject(s, Map.class);
        String status = (String) result.get("status");
        if (StringUtils.isEmpty(status) || "error".equals(status)) {
            return null;
        }
        JSONArray data = (JSONArray) result.get(dataName);
        List<Record> ts = JSONArray.parseArray(data.toJSONString(), Record.class);
        if(!CollectionUtils.isEmpty(ts)){
            return ts.get(0);
        }
        return null;
    }


    /**
     *
     * @param map
     *          starttime 开始时间毫秒时间戳 ，endtime 结束时间 ， meterid水表编号 ， deviceid 设备编号 ，ccid客户编号
     *            checked 是否已核对 [all,true,false]  ， order 默认为readtime按读表时间排序
     *            sort  升序或降序 [asc,desc], type（0 ：为添加, 1：为修改）
     * @return
     */
    public  List<Record> getRecordByGtId(Map map,final Long id,Integer type){
        Integer from = 0 ;
        Integer size = 1000;
        int total ;
        String token = getToken(false);
        List<Record> customerList = new ArrayList<>();
        if(map == null){
            map = new HashMap();
        }else {
            from = map.containsKey("from") ? (Integer) map.get("from") : from;
            size = map.containsKey("size") ? (Integer) map.get("size") : size;
        }
        //地址
        String url = null;
        //参数名
        String dataName = null;
        url = WaterConstart.RECORD;
        dataName = "record";
        while (true) {
            map.put("from", from*size);
            map.put("size", size);
            String s = HttpUtils.sendPost( url+ token, map);
            Map result = JSONObject.parseObject(s, Map.class);
            String status = (String) result.get("status");
            if (StringUtils.isEmpty(status) || "error".equals(status)) {
                break;
            }
            JSONArray data = (JSONArray) result.get(dataName);
            List<Record> ts = JSONArray.parseArray(data.toJSONString(), Record.class);
            List<Record> collect = new ArrayList<>();
            if(!CollectionUtils.isEmpty(ts)){
                if(type == 0){
                    collect = ts.stream().filter(record -> record.getId() > id).collect(Collectors.toList());
                    if(CollectionUtils.isEmpty(collect)){
                        return customerList;
                    }

                }else if(type == 1 ) {
                    collect = ts.stream().filter((record) -> record.getId() <= id ).collect(Collectors.toList());
                    if(CollectionUtils.isEmpty(collect)){
                        return customerList;
                    }

                }
                customerList.addAll(collect);
            }
            total = (Integer) result.get("total");
            int toalSize = total/size ;
            if(toalSize <= from ){
                break;
            }
            from ++;
        }
        return customerList;
    }


    /**
     *
     * @Title: queryRecordTotal
     * @Description: 获取水表数据的总条数
     * @param
     * @return: java.lang.Integer
     */
    public Integer queryRecordTotal(){
        Map map = new HashMap();
        map.put("from", 0);
        map.put("size", 1);
//        map.put("meterid",meterId);
        map.put("order","id");
        String token = getToken(false);
        //地址
        String url = WaterConstart.RECORD;
        //参数名
        String dataName = "record";
        String s = HttpUtils.sendPost( url+ token, map);
        Map result = JSONObject.parseObject(s, Map.class);
        Integer total = (Integer) result.get("total");
        return total;
    }




    public  List<Record> getRecordByGtIdBy(Map map,Long id,Integer type){
        System.out.println("开始" + System.currentTimeMillis());
        Integer from = 0 ;
        Integer size = 1000;
        int total ;
        String token = getToken(false);
        List<Record> customerList = new ArrayList<>();
        if(map == null){
            map = new HashMap();
        }else {
            from = map.containsKey("from") ? (Integer) map.get("from") : from;
            size = map.containsKey("size") ? (Integer) map.get("size") : size;
        }
        //地址
        String url = null;
        //参数名
        String dataName = null;
        url = WaterConstart.RECORD;
        dataName = "record";
//        while (true) {
            map.put("from", from);
            map.put("size", size);
            String s = HttpUtils.sendPost( url+ token, map);
            Map result = JSONObject.parseObject(s, Map.class);
            String status = (String) result.get("status");
            if (StringUtils.isEmpty(status) || "error".equals(status)) {
//                break;
                return null;
            }
            JSONArray data = (JSONArray) result.get(dataName);
            List<Record> ts = JSONArray.parseArray(data.toJSONString(), Record.class);
            List<Record> collect = new ArrayList<>();
            if(!CollectionUtils.isEmpty(ts)){
                if(type == null) {
                    collect = ts;
                } else if(type == 0){
                    collect = ts.stream().filter(record -> record.getId() > id).collect(Collectors.toList());
                    if(CollectionUtils.isEmpty(collect)){
                        return customerList;
                    }

                }else if(type == 1 ) {
                    collect = ts.stream().filter((record) -> record.getId() <= id ).collect(Collectors.toList());
                    if(CollectionUtils.isEmpty(collect)){
                        return customerList;
                    }
                }
                customerList.addAll(collect);
            }
        System.out.println("结束" + System.currentTimeMillis());
        return customerList;
    }
}
