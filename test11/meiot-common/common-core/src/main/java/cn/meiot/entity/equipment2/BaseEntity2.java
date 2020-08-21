package cn.meiot.entity.equipment2;

import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;

@Data
public class BaseEntity2 implements Serializable {
    private static final long serialVersionUID = 1L;
    //消息id
    protected Long messageid = new Long(RandomStringUtils.randomNumeric(5));
    //协议
    protected String cmd;
    //时间戳
    protected Long stamp = System.currentTimeMillis()/1000;

    protected String deviceid;
}
