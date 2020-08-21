package cn.meiot.entity.excel;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

//import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.meiot.constart.ProjectConstart;
import lombok.Data;

@Data
public class UserExcel extends BaseRowModel{
	//@Excel(name="设备号" ,orderNum = "2",needMerge=true,width=20)
	@ExcelProperty(value = {"设备号"},index = 2)
	private String serialNumber;
	//@Excel(name="开关数量" ,orderNum = "3",needMerge=true)
	@ExcelProperty(value = {"排数"},index = 3)
	private Integer switchCount;
	private Integer userId;
	//@Excel(name="排数" ,orderNum = "4",needMerge=true)
	@ExcelProperty(value = {"账户绑定数量"},index = 4)
	private Integer userCount;
	//@Excel(name="设备名" ,orderNum = "1",needMerge=true,width=20)
	@ExcelProperty(value = {"设备名"},index = 1)
	private String serialName;
	//@Excel(name="用户名" ,orderNum = "0",needMerge=true,width=20)
	@ExcelProperty(value = {"用户名"},index = 0)
	private String userName;
	//@Excel(name="有效期" ,orderNum = "5",needMerge=true)
	@ExcelProperty(value = {"有效期"},index = 5)
	private String time;
}
