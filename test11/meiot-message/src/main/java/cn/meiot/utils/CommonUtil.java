package cn.meiot.utils;

import cn.meiot.entity.FaultType;
import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.feign.UserFeign;
import cn.meiot.mapper.FaultTypeMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CommonUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private FaultTypeMapper faultTypeMapper;

    @Autowired
    private UserFeign userFeign;


    /**
     * 通过设备号获取主账户id
     *
     * @param serialNumber
     * @return
     */
    public List<Long> getRtUserIdBySerialNumber(String serialNumber) {
        //从缓存中拿取数据
        String hash = RedisConstantUtil.SERIAL_NUMBER_USER_ID;
        String key = serialNumber;
        Object rtUserIds = redisTemplate.opsForHash().get(hash, key);//redisUtil.getHashValueByKey(hash, key);
        if (null == rtUserIds) {
            Result result = deviceFeign.getRtuserIdBySerialNumber(serialNumber);
            if (!result.isResult()) {
                log.info("获取失败，原因：{}", result.getMsg());
                return null;
            }

            log.info("数据是通过远程调用获取");
            //redisUtil.saveHashValue(hash,key,rtUserIds);
            return getLongList((List<String>) result.getData());
        }
        log.info("从缓存中拿到了数据：{}", rtUserIds);
        return getLongList((List<String>) rtUserIds);
    }

    private List<Long> getLongList(List<String> list){
        List<Long> longs=null;
        if (null != list && list.size()>0 ) {
            longs=new ArrayList<>();
            for ( String s:list) {
                longs.add(Long.valueOf(s));
            }
        }
        return longs;
    }
    /**
     * 获取事件
     *
     * @return
     */
    public List<FaultType> getEvents() {
        //从缓存中获取事件信息
        Object object = redisTemplate.opsForValue().get(RedisConstantUtil.FAULT_EVENTS);
        if (null == object) {
            List<FaultType> faultTypes = faultTypeMapper.selectList(new QueryWrapper<FaultType>());
            if (faultTypes == null) {
                log.info("从数据库中获取事件类型出错，未获取到");
                return null;
            }
            //将数据存入缓存
            redisTemplate.opsForValue().set(RedisConstantUtil.FAULT_EVENTS, new Gson().toJson(faultTypes),500, TimeUnit.MINUTES);
            object= redisTemplate.opsForValue().get(RedisConstantUtil.FAULT_EVENTS);
        }
        List<FaultType> list = new Gson().fromJson(object.toString(), new TypeToken<List<FaultType>>() {
        }.getType());
        return list;

    }

    /**
     * 获取图片配置信息
     *
     * @return
     */
    public ImgConfigVo getImgConfig() {
        Object object = redisTemplate.opsForValue().get(RedisConstantUtil.IMG_CONFIG);
        if (null == object) {
            ImgConfigVo imgConfigVo = userFeign.getImgConfig();
            if (null == imgConfigVo) {
                log.info("未获取到图片配置信息");
                return null;
            }
            log.info("接收结果：{}", imgConfigVo);
            return imgConfigVo;
        }
        ImgConfigVo imgConfigVo = new Gson().fromJson(object.toString(), ImgConfigVo.class);
        return imgConfigVo;

    }


    /**
     * 时间戳转换date
     */

    public static String getDate(Integer integer) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(integer);
        return d;
    }
    public static String getDate(String integer) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(integer);
        return d;
    }
    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(new Date());
        return d;
    }
}
