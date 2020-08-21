package cn.meiot.entity.vo;

import cn.meiot.utils.ConstantsUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2020/4/17 17:46
 * @Copyright: www.spacecg.cn
 */
@Data
public class AftersaleVo {



    /**
     * 故障ID
     */
    private Long id;

    /**
     * 故障名称
     */
    private String alarmTypeName;

    /**
     * 设备名称
     */
    private String deviceAlias;

    /**
     * 设备号
     */
    private String deviceId;

    /**
     * 线路名称
     */
    private String switchAlias;

    /**
     * 故障值
     */
    private String alarmValue;

    /**
     * 故障类型别名
     */
    private String fAlias;

    /**
     *故障类型符号
     */
    private String fAymbol;

    /**
     * 故障时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String alarmTime;

    /**
     * 报修时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd")
    private String repairTime;

    /**
     * 受理时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd")
    private String receptionTime;

    /**
     * 维修时间
     */
    @JsonFormat(pattern = "yyyy/MM/dd")
    private String maintenanceTime;

    /**
     * 状态 0 报修 1受理 2完成
     */
    private Integer type;

    /**
     * 备注
     */
    private String note;

    /**
     * 故障类型ID
     */
    private Integer alarmType;
    /**
     * icon 图标
     * @return
     */
    private String icon;

    /**
     * 地址
     * @return
     */

    private String address;

    public String getAlarmValue() {
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        return decimalFormat.format(Double.valueOf(alarmValue));
    }

    public String getRepairTime() {
        return format(repairTime);
    }

    public String getReceptionTime() {
        return format(receptionTime);
    }

    public String getMaintenanceTime() {
        return format(maintenanceTime);
    }

    private String format(String date){
        if (StringUtils.isNotEmpty(date)) {
            String[] strings = date.split("-");
            String[] strings1 = strings[2].split(" ");
            return strings[0]+"/"+strings[1]+"/"+strings1[0];
        }
        return null;
    }
}
