package cn.meiot.entity.bo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketBo {

    private  String ticket;

    private long expire_seconds;

    private String url;
}
