package cn.meiot.entity.vo;

import cn.meiot.entity.equipment2.upwarn.WarnInfo;
import cn.meiot.exception.AlarmException;
import cn.meiot.utils.enums.AlarmEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Package cn.meiot.entity.vo
 * @Description: 新协议vo  110协议 故障增加预警  （预警|报警）
 * @author: 武有
 * @date: 2019/12/28 15:00
 * @Copyright: www.spacecg.cn
 */
@Data
@Slf4j
public class AlarmVo implements Serializable {

    private static final SimpleDateFormat SD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 设备ID 设备序列号
     */
    private String deviceid;

    /**
     * 故障时间
     */
    private String time;

    private SwitchInfo switchInfo;


    /**
     * 开关信息
     */
    @Data
    public class SwitchInfo {
        private Integer index;
        private String mode;
        private Long id;
        private Integer switchStatus;
        private Integer type;//1报警 2预警
        private List<Event> events;

        public SwitchInfo() {
            events = new ArrayList<>();
        }
    }

    @Data
    public class Event {
        private Integer event;
        private BigDecimal value;

        public Event(Integer event, BigDecimal value) {
            this.event = event;
            this.value = value;
        }
    }


    public AlarmVo(String msg) {
        initAlarmVo(msg);
    }

    public AlarmVo(WarnInfo warnInfo) {
        initAlarmVo(warnInfo);
    }

    private void initAlarmVo(WarnInfo warnInfo) {
        switchInfo = new SwitchInfo();
        switchInfo.setId(warnInfo.getSid());
        switchInfo.setIndex(0);
        Map<String, Integer> value = AlarmEnum.getValue(warnInfo.getEventtype());
        if (null == value) {
            return;
        }
        log.info("type=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`>:{}",value.get("type"));
        switchInfo.setType(value.get("type"));
        switchInfo.setEvents(getEventList(new Event(value.get("value"),BigDecimal.valueOf(warnInfo.getEventvalue()))));
    }

    private List<Event> getEventList(Event event) {
        ArrayList<Event> arrayList = new ArrayList<>();
        arrayList.add(event);
        return arrayList;
    }


    private void initAlarmVo(String msg) {
        JSONObject msgObject = JSONObject.parseObject(msg);
        JSONObject payload = msgObject.getJSONObject("payload");
        String deviceid = payload.getString("deviceid");
        this.deviceid = deviceid;
        Long time = payload.getLong("timestamp");
        this.time = this.SD.format(new Date(time * 1000));
        this.switchInfo = new SwitchInfo();
        JSONObject desired = payload.getJSONObject("desired");
        JSONArray arrays = desired.getJSONArray("arrays");
        for (int i = 0; i < arrays.size(); i++) {
            JSONObject jsonObject = arrays.getJSONObject(i);
            JSONObject device = jsonObject.getJSONObject("device");
            Integer index = device.getInteger("index");
            String mode = device.getString("mode");
            Long id = Long.valueOf(device.getString("id"));
            this.switchInfo.setId(id);
            this.switchInfo.setMode(mode);
            this.switchInfo.setIndex(index);
            this.switchInfo.setSwitchStatus(jsonObject.getInteger("switch"));
            this.switchInfo.setType(jsonObject.getInteger("type"));
            JSONArray event = jsonObject.getJSONArray("event");
            JSONArray value = jsonObject.getJSONArray("value");
            log.info("获取到的值为===>：{}", value);
            for (int j = 0; j < event.size(); j++) {
                Integer eventInteger = event.getInteger(j);
                BigDecimal bigDecimal = new BigDecimal(value.getString(j));
                Event e = new Event(eventInteger, bigDecimal);
                this.switchInfo.getEvents().add(e);
            }

        }
    }
}
