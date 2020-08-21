package cn.meiot.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MyTest implements Serializable {

    private String _id;

    private Integer meterd;

    private String  clientid;

    private String  topic;

    private Integer  qos;

    private Integer  raw_packet_id;

    private Boolean  is_retain;

    private Boolean  is_resend;

    private Boolean  is_will;

    private String  ip;

    private String  created;

    private Test2 test2;
}
