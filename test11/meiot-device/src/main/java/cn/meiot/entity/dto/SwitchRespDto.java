package cn.meiot.entity.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author lingzhiying
 * @title: SwitchRespDto.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月19日
 */
@Data
public class SwitchRespDto {
	/**
	 * 开关预警状态
	 */
	//线路状态  0正常  1报警  2预警
	private Integer switchStatus = 0;
	//0正常  1删除
	private Integer deleted;
	//0表示总开关
	private Integer parentIndex;
	//数量
	private Integer faultNum = 0;
	/**
	 * 类型
	 */
	private String typeName;
	/**
	 * 类型ID
	 */
	private Integer typeId;
	/**
	 * 手动开合闸
	 */
	private Integer closeStatus;
	/**
	 * 当月电量
	 */
	private Object monthElectric = BigDecimal.ZERO ;
	/**
	 * 当日电量
	 */
	private Object dayElectric = BigDecimal.ZERO ;
	/**
	 * 电流
	 */
	private BigDecimal current = BigDecimal.ZERO ;
	/**
	 * 电压
	 */
	private BigDecimal voltage = BigDecimal.ZERO ;
	/**
	 * 别名d
	 */
	private Long id;
	/**
	 * 开关编号
	 */
	private String switchSn;
	/**
	 * 设备序号
	 */
	private Integer switchIndex;
	/**
	 * 别名
	 */
	private String name;
	/**
	 * 开关状态
	 */
	private Integer status = 0 ;
	/**
	 * 定时数量
	 */
	private Integer timeCount = 0 ; 
	/**
	 * 最大功率
	 */
	private Integer loadmax =0;
	/**
	 * 温度
	 */
	private Integer temp = 0;
	/**
	 * 漏电  0漏电 1正常
	 */
	private Integer leakedElectric = 1;
	/**
	 * 漏电
	 */
	private Object leakage = 0; 
	/**
	 * 功率
	 */
	private BigDecimal power = BigDecimal.ZERO ;
	/**
	 * 电量统计
	 */
	private List<Map> listMap;


}
