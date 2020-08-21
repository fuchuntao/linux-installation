package cn.meiot.entity.device;

import lombok.Data;

import java.io.Serializable;

@Data
public class Payload implements Serializable  {

    /**
     * 消息ID，随机数，每发送一条消息后变化
     */
    private String messageid;

    /**
     * linux时间戳，无毫秒位
     */
    private Long  timestamp = System.currentTimeMillis()/1000;

    /**
     * 设备的序列号，0为广播
     */
    private String deviceid;

    /**
     * 指令编号
     */
    private String cmd;

    private Desired desired;

}
