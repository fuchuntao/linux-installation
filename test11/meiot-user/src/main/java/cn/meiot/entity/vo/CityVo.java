package cn.meiot.entity.vo;

import lombok.Data;

@Data
public class CityVo {

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 地区/县
     */
    private String district;
}
