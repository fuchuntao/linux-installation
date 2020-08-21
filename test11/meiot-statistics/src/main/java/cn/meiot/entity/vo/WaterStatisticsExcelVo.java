package cn.meiot.entity.vo;

import cn.meiot.utils.CommonUtil;
import cn.meiot.utils.DataUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName: WaterStatisticsVo
 * @Description: 抄表信息类
 * @author: 符纯涛
 * @date: 2020/2/26
 */
@Data
public class WaterStatisticsExcelVo extends BaseRowModel {


    private Long recordId;

    /**
     * 地址
     */
    @ExcelProperty(value ={"位置"},index = 0)
    private String address;

    /**
     *抄表记录id
     */
    private Long id;
    /**
     *客户编号
     */
    private String ccid;
    /**
     *水表编号
     */
    private String meterid;
    /**
     *设备编号
     */
    @ExcelProperty(value ={"设备号"},index = 1)
    private String deviceid;

    /**
     *抄表时间
     */
    @ExcelProperty(value ={"抄表时间"},index = 2)
    private String readtime;
    /**
     *抄表读数
     */
    @ExcelProperty(value ={"抄表读数"},index = 3)
    private BigDecimal readcount;

    /**
     * 单位
     */
    @ExcelProperty(value ={"单位(立方米)"},index = 4)
    private Double unit;

    /**
     *核对状态
     */
    @ExcelProperty(value ={"核对情况"},index = 5)
    private String checked;
    /**
     *核对者
     */
    @ExcelProperty(value ={"核对人"},index = 6)
    private String checker;


    public void setChecked(String checked) {
        if("false".equals(checked)) {
            this.checked = "未核对";
        }else if("true".equals(checked)) {
            this.checked = "已核对";
        }
    }

    public void setReadtime(Long readtime) {
        if(readtime != null) {
            String time = String.valueOf(readtime);
            this.readtime = DataUtil.getDateLong(time);
        }else {
            this.readtime = null;
        }

    }
}
