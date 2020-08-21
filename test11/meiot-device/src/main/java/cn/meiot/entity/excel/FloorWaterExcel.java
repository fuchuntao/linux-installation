package cn.meiot.entity.excel;

import cn.meiot.utils.ConstantsUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

@Data
public class FloorWaterExcel extends BaseRowModel {
    @ExcelProperty(value = {"设备号"},index = 1)
    private String meterid;
    @ExcelProperty(value = {"位置"},index = 2)
    private String address;
    private Long buildingId;
    @ExcelProperty(value = {"水表名称"},index = 3)
    private String name;
    @ExcelProperty(value = {"册号"},index = 4)
    private String bookname;
    @ExcelProperty(value = {"开始使用时间"},index = 5)
    private String startTime;
    @ExcelProperty(value = {"结束使用时间"},index = 6)
    private String endTime;
    @ExcelProperty(value = {"备注"},index = 7)
    private String cinfo;
    public void setStartTime(Long startTime) {
        if (startTime == null){
            return;
        }
        this.startTime = ConstantsUtil.getSimpleDateFormat().format(startTime);
    }

    public void setEndTime(Long endTime) {
        if(endTime ==null){
            return;
        }
        this.endTime = ConstantsUtil.getSimpleDateFormat().format(endTime);
    }
}
