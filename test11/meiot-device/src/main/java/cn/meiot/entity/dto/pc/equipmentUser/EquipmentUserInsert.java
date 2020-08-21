package cn.meiot.entity.dto.pc.equipmentUser;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
public class EquipmentUserInsert {
	//buidling
	private Long buildingId;
	//开关状态
	private Integer isSwitch = 0;
	private Long id;
	 //设备序列号
    private  String serialNumber;
   
	 //用户id
    private  Long userId;
   
	 //备注名称
    private  String name;
   
	 //状态:0-待审核 1-正常 2-禁用
    private  Integer userStatus = 1;
   
	 //是否主账户: 0-否 1-是
    private  Integer isPrimary = 1;
    //项目id
    private Integer projectId;
}
