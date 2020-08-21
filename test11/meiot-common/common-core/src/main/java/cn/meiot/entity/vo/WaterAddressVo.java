package cn.meiot.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author 符纯涛
 * @since 2020-02-24
 */
@Data
public class WaterAddressVo implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     *水表编号
     */
    private String meterid;


    /**
     * 单位
     */
    private Double unit;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 项目id
     */
    private Integer projectId;


    /**
     * 地址
     */
    private String address;



    /**
     * 楼层id
     */
    private Long buildingId;



}
