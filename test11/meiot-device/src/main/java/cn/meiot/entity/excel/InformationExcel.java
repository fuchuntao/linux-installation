package cn.meiot.entity.excel;

import cn.meiot.utils.ConstantsUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

@Data
public class InformationExcel extends BaseRowModel {
    private Long buildingId;
    @ExcelProperty(value = {"位置"},index = 1)
    private String address;
    @ExcelProperty(value = {"水表名称"},index = 2)
    private String name;
    @ExcelProperty(value = {"编号"},index = 3)
    private String id;
    @ExcelProperty(value = {"水表型号"},index = 3)
    private String product;
    @ExcelProperty(value = {"水表口径"},index = 3)
    private String caliber;
    @ExcelProperty(value = {"设备号"},index = 3)
    private String meterId;
    @ExcelProperty(value = {"sim号"},index = 3)
    private String sim;
    @ExcelProperty(value = {"表底"},index = 3)
    private String basecount;
    @ExcelProperty(value = {"电量"},index = 3)
    private String battery;
    @ExcelProperty(value = {"状态"},index = 3)
    private String status;
    @ExcelProperty(value = {"抄表设置"},index = 3)
    private String sendmode;
    @ExcelProperty(value = {"最新行度"},index = 3)
    private String latelyCount;
    @ExcelProperty(value = {"最新抄表时间"},index = 3)
    private String latelyTime;
    @ExcelProperty(value = {"单位(立方米)"},index = 3)
    private String unit;

    public void setLatelyTime(Long latelyTime) {
        if(latelyTime == null){
            return;
        }
        this.latelyTime = ConstantsUtil.getSimpleDateFormat().format(latelyTime);
    }
}
