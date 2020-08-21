package cn.meiot.entity.db;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

/**
 * @author lingzhiying
 * @title: Switch.java
 * @projectName spacepm
 * @description:  
 * @date 2019年8月16日
 */
@Data
public class Switch {
	 //开关编号
	@Id
	@GeneratedValue(generator = "JDBC")
    private  String switchSn;
   
	 //设备序列号
    private  String serialNumber;
   
	 //设备中序号
    private  Integer switchIndex;
   
	 //开关型号
    private  String switchModel;
   
	 //上级开关index, 0表示总开关
    private  Integer parentIndex;
   
	 //关联情景模式
    private  Long timerId;

    //删除
	private Integer deleted = 0;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Switch other = (Switch) obj;
		if (switchSn == null) {
			if (other.switchSn != null)
				return false;
		} else if (!switchSn.equals(other.switchSn))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((switchSn == null) ? 0 : switchSn.hashCode());
		return result;
	}

    
}
