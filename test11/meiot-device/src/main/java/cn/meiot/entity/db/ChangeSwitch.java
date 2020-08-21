package cn.meiot.entity.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import cn.meiot.entity.vo.DeviceVo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeSwitch implements Serializable{
	/**
	 * 创建时间
	 */
	private String createTime;
	/**
	 * id
	 */
	@Id
	@GeneratedValue(generator = "JDBC")
	private Long id;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 设备号
	 */
	private String serialNumber;
	/**
	 * index
	 */
	private Integer switchIndex;
	/**
	 * 旧的开关号   被替换的
	 */
	private Long oldSwitchSn;
	/**
	 * 新的开关号   替换旧的
	 */
	private Long newSwitchSn;
	/**
	 * 类型  0:修改   1删除
	 */
	private Integer type;
	public ChangeSwitch() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ChangeSwitch(DeviceVo deviceVo) {
		super();
		this.serialNumber = deviceVo.getSerialNumber();
		this.switchIndex = deviceVo.getSwitchIndex();
		this.oldSwitchSn = deviceVo.getOldSwitchSn();
		this.newSwitchSn = deviceVo.getNewSwitchSn();
	}
	public ChangeSwitch(String createTime,Long id, String serialNumber, Integer switchIndex, Long oldSwitchSn, Long newSwitchSn,
			Integer type) {
		super();
		this.id = id;
		this.serialNumber = serialNumber;
		this.switchIndex = switchIndex;
		this.oldSwitchSn = oldSwitchSn;
		this.newSwitchSn = newSwitchSn;
		this.type = type;
		this.createTime = createTime;
	}
}
