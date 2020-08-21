package cn.meiot.entity.equipment.kgcs;

import cn.meiot.entity.bo.Crcuit;
import cn.meiot.entity.equipment.Device;
import cn.meiot.entity.equipment.Timeswitch;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author lingzhiying
 * @title: Kgcs.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月15日
 */
@Data
public class Kgcs implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 开关的设备信息
	 */
	private Device device;
	/**
	 * 开关的状态参数
	 */
	private Status1 status;
	/**
	 * 数组开关的定时开关参数
	 */
	private List<Timeswitch> timer;
	/**
	 * 报警参数
	 */
	private Maxparam maxparam;
	/**
	 * 预警参数
	 */
	private Remindparam remindparam;

	public void setCrcuit(Crcuit crcuit,Integer index,Long id){
		maxparam = new Maxparam();
		remindparam = new Remindparam();
		status = new Status1();
		//过流
		maxparam.setMaxcurrent(crcuit.getCurrentWA().multiply(BigDecimal.valueOf(1000)));
		remindparam.setRmcurrent(crcuit.getCurrent().multiply(BigDecimal.valueOf(1000)));
		//漏电
		maxparam.setMaxleakage(crcuit.getLeakageWA());
		remindparam.setRmleakage(crcuit.getLeakage());
		//过压
		maxparam.setMaxvoltagehigh(crcuit.getVoltageWA().multiply(BigDecimal.valueOf(1000)));
		remindparam.setRmvoltagehigh(crcuit.getVoltage().multiply(BigDecimal.valueOf(1000)));
		//欠压
		maxparam.setMaxvoltagelow(crcuit.getUnderVoltageWA().multiply(BigDecimal.valueOf(1000)));
		remindparam.setRmvoltagelow(crcuit.getUnderVoltage().multiply(BigDecimal.valueOf(1000)));
		//过温
		remindparam.setRmtemp(crcuit.getTemp());
		status.setTempmax(crcuit.getTempWA().intValue());
		//过载
		remindparam.setRmpower(crcuit.getPower());
		status.setLoadmax(crcuit.getPowerWA().intValue());

		device = new Device();
		device.setId(id);
		device.setIndex(index);
	}
}
