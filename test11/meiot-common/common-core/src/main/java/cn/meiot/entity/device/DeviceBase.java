package cn.meiot.entity.device;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceBase implements Serializable {

    private Payload payload;

    private String clientid;

    private Long created_time;



}
