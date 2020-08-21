package cn.meiot.utils;

import cn.meiot.entity.FaultMessage;
import cn.meiot.entity.bo.Crcuit;
import cn.meiot.entity.vo.MqDeviceVo;
import cn.meiot.entity.vo.MqStatusVo;
import cn.meiot.entity.vo.MqVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2019/10/16 12:28
 * @Copyright: www.spacecg.cn
 */
@Slf4j
@SuppressWarnings("all")
public class MqUtils {
    //获取mq的数据
    public static MqVo getMqVo(String jsonString) {
        Map parseObject = JSON.parseObject(jsonString, Map.class);
        Map payloadMap = (Map) parseObject.get("payload");
        Map desiredMap = (Map) payloadMap.get("desired");

        List<Map> arraysMap = (List<Map>) desiredMap.get("arrays");
        List<MqStatusVo> mqStatusVos = new ArrayList<>();
        List<MqDeviceVo> mqDeviceVos = new ArrayList<>();
        for (Map paramMap : arraysMap) {
            log.info("--设备参数-{}", paramMap);
            Map deviceMap = (Map) paramMap.get("device"); // {"index": 1,"mode": "C32","id": 1911190418}
            MqDeviceVo mqDeviceVo = getMqDeviceVo(deviceMap);
            log.info("mqDeviceVo:{}", mqDeviceVo);
            mqDeviceVos.add(mqDeviceVo);

            Map statusMap = (Map) paramMap.get("status");
            MqStatusVo mqStatusVo = getMqStatusVo(statusMap);
            log.info("mqStatusVo:{}", mqStatusVo);
            mqStatusVos.add(mqStatusVo);
        }
        MqVo mqVo = new MqVo(mqDeviceVos, mqStatusVos);
        mqVo.setSerialNumber((String) payloadMap.get("deviceid"));
        mqVo.setTimestamp(getDate(Long.valueOf(payloadMap.get("timestamp").toString()) * 1000l));
        return mqVo;
    }

    private static String getDate(Long l) {
        Date parse = null;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sf.format(l);
        try {
            parse = sf.parse(format);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sf.format(parse);
    }

    //获取设备的数据
    public static MqDeviceVo getMqDeviceVo(Map deviceMap) {
        MqDeviceVo mqDeviceVo = new MqDeviceVo();
        mqDeviceVo.setIndex((Integer) deviceMap.get("index"));
        mqDeviceVo.setId(Long.valueOf((Integer) deviceMap.get("id")));
        mqDeviceVo.setMode((String) deviceMap.get("mode"));
        return mqDeviceVo;
    }

    //获取设备的详细数据
    //TODO
    public static MqStatusVo getMqStatusVo(Map statusMap) {
        MqStatusVo mqStatusVo = new MqStatusVo();
        mqStatusVo.setEvent(getIntegerArray((JSONArray) statusMap.get("event")));
        if (null != mqStatusVo.getEvent() && mqStatusVo.getEvent().length > 0) {
            if (mqStatusVo.getEvent()[0] != 8) {
                mqStatusVo.setPower(new BigDecimal(statusMap.get("power") == null ? "0" : statusMap.get("power").toString()));
                mqStatusVo.setLoadmax(new BigDecimal(statusMap.get("loadmax") == null ? "0" : statusMap.get("loadmax").toString()));
                mqStatusVo.setTemp(new BigDecimal(statusMap.get("temp") == null ? "0" : statusMap.get("temp").toString()));
                mqStatusVo.setTempmax(new BigDecimal(statusMap.get("tempmax") == null ? "0" : statusMap.get("tempmax").toString()));
                mqStatusVo.setMeterd(new BigDecimal(statusMap.get("meterd") == null ? "0" : statusMap.get("meterd").toString()));
                mqStatusVo.setMeterm(new BigDecimal(statusMap.get("meterm") == null ? "0" : statusMap.get("meterm").toString()));
                mqStatusVo.setSwitchs((Integer) statusMap.get("switch"));
                mqStatusVo.setAuto(new BigDecimal(statusMap.get("auto") == null ? "0" : statusMap.get("auto").toString()));
                mqStatusVo.setLeakage(new BigDecimal(statusMap.get("leakage") == null ? "0" : statusMap.get("leakage").toString()));
                mqStatusVo.setCurrent(getBigDecimalArray((JSONArray) statusMap.get("current")));
                mqStatusVo.setVoltage(getBigDecimalArray((JSONArray) statusMap.get("voltage")));
            }
        }
        return mqStatusVo;
    }

    //JSONarray转换为数组
    public static BigDecimal[] getBigDecimalArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.size() == 0) {
            return null;
        }
        BigDecimal[] integers = new BigDecimal[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            integers[i] = new BigDecimal(jsonArray.get(i).toString());
        }
        return integers;
    }

    //JSONarray转换为数组
    public static Integer[] getIntegerArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.size() == 0) {
            return null;
        }
        Integer[] integers = new Integer[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            integers[i] = Integer.valueOf(jsonArray.get(i).toString());
        }
        return integers;
    }

    //判断要不要报警
    public static boolean ifAlarm(Integer event) {
        if (null != event) {
            if (event > 0 && event <= 7 && event != 2) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据事件获取当前事件的值值
     *
     * @param e
     * @param o
     * @param queue
     */
    public static BigDecimal getValue(Integer event, MqVo mqVo) {
        try {
            BigDecimal zero = new BigDecimal("0");
            //电压
            BigDecimal voltage = mqVo.getMqStatusVos().get(0).getVoltage()[0];
            if (null == voltage || voltage.compareTo(zero) == 0) {
                voltage = zero;
            } else {
                voltage = voltage.divide(new BigDecimal("1000"));
            }

            //电流
            BigDecimal current = mqVo.getMqStatusVos().get(0).getCurrent()[0];
            if (null == current || current.compareTo(zero) == 0) {
                current = zero;
            } else {
                current.divide(new BigDecimal("1000"));
            }

            //温度
            BigDecimal temp = mqVo.getMqStatusVos().get(0).getTemp();
            if (null == temp || temp.compareTo(zero) == 0) {
                temp = zero;
            }


            switch (event) {
                //漏电报警
                case 1: {
                    if (null == mqVo.getMqStatusVos().get(0).getCurrent()[0]) {
                        return zero;
                    }
                    return mqVo.getMqStatusVos().get(0).getCurrent()[0];
                }
                //过温预警 此推送不在这里做
                case 2: {
                    break;
                }
                //过温报警
                case 3: {
                    return mqVo.getMqStatusVos().get(0).getTemp();
                }
                //过载报警
                case 4: {
                    return current.multiply(voltage);
                }
                //短路报警
                case 5: {
                    return current;
                }
                //过压报警
                case 6: {
                    return voltage;
                }
                // 欠压报警
                case 7: {
                    return voltage;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("******获取故障值异常，返回0******");
            return new BigDecimal(0);
        }
        return null;
    }


    //    public  static
    //错误日志打印
    public static void log(Exception e, Object o, String queue) {
        log.error(" ----------------start:队列名称：" + queue + "------------------");
        log.error("队列发生错误，队列名称:{}", queue);
        log.error("错误消息：{}", e.getMessage());
        log.error("接受到的参数为：{}", JSONObject.toJSONString(o));
        log.error(" ---------------- end ------------------");
    }
}
