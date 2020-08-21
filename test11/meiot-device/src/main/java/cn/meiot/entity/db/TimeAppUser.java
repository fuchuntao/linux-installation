package cn.meiot.entity.db;

import lombok.Data;

@Data
public class TimeAppUser {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Integer id;

    /**
     * switch_sn
     */
    private String switchSn;

    /**
     * time_mode_id
     */
    private Integer timeModeId;

}
