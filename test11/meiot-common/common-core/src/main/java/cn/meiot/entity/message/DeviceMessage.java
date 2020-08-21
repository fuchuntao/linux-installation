package cn.meiot.entity.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceMessage implements Serializable {
    private static final long serialVersionUID = 8148528336365517539L;
    /**
     * 主账户id
     */
    private Long mainUserId;
    /**
     * 操作用户
     */
    private Long userId;
    /**
     * 项目id
     */
    private Integer projectId;
    /**
     * 操作类型
     */
    private Integer type;
    /**
     * 内容
     */
    private Map map;

    public static DeviceMessage switchControl(Long mainUserId,Long userId,Integer projectId,String serialNumber,List<String> switchList,Integer status){
        Map map = new HashMap();
        map.put("serialNumber",serialNumber);
        //存在即是单个开关，不存在即是全开全关并去掉主开关.
        map.put("switchList",switchList);
        // 0关  1开
        map.put("status",status);
        DeviceMessage deviceMessage = new DeviceMessage(mainUserId,userId,projectId,null,map);
        return deviceMessage;
    }

    public static DeviceMessage examination(Long mainUserId,Long userId,Integer projectId,List<String> serialNumberList,Integer status,String time){
        Map map = new HashMap();
        map.put("serialNumber",serialNumberList);
        // 0关  1开
        map.put("status",status);
        // 自检时间 日日空格时时:00   示例:30 15:00
        map.put("time",time);
        DeviceMessage deviceMessage = new DeviceMessage(mainUserId,userId,projectId,null,map);
        return deviceMessage;
    }

    public static DeviceMessage loadMax(Long mainUserId,Long userId,Integer projectId,String serialNumber,List<String> switchList,Integer status,Integer loadMax){
        Map map = new HashMap();
        //设备
        map.put("serialNumber",serialNumber);
       //开关
        map.put("switchList",switchList);
        // 0关  1开
        map.put("status",status);
        if(status.equals(0)){
            loadMax = 0;
        }
        //功率值
        map.put("loadMax",loadMax);
        DeviceMessage deviceMessage = new DeviceMessage(mainUserId,userId,projectId,null,map);
        return deviceMessage;
    }
}
