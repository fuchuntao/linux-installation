package cn.meiot.entity.dto.pc.water;

import cn.meiot.utils.ConstantsUtil;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class WaterMeterDto {
    private Long userId;
    private Integer projectId;
    private String name;
    private Long buildingId;
    private Integer page;
    private Integer pageSize;
    private String address;
    private String startTime;
    /**
     * 册号
     */
    private String bookname;
    /**
     * 水表记录id
     */
    protected Long id;

    /**
     * 册号
     */
    protected String cinfo;

    /**
     * 水表编号
     */
    protected String meterid;

    /**
     * end_time
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    protected String endTime;

    public void setStartTime(Long startTime) {
        if(startTime == null){
            return;
        }
        SimpleDateFormat simpleDateFormat = ConstantsUtil.getSimpleDateFormat();
        this.startTime = simpleDateFormat.format(new Date(startTime));
    }

    public void setEndTime(Long endTime) {
        if(endTime == null){
            return;
        }
        SimpleDateFormat simpleDateFormat = ConstantsUtil.getSimpleDateFormat();
        this.endTime = simpleDateFormat.format(new Date(endTime));
    }

    public void setLatelyTime(Long latelyTime) {
        if(latelyTime == null ){
            return;
        }
        SimpleDateFormat simpleDateFormat = ConstantsUtil.getSimpleDateFormat();
        this.latelyTime = simpleDateFormat.format(new Date(latelyTime));
    }

    /**
     * 水表型号
     */
    protected String product;

    /**
     * 水表口径
     */
    protected String caliber;

    /**
     * 设备编号
     */
    protected String deviceid;

    /**
     * 电量
     */
    protected Integer battery;

    /**
     * sim号
     */
    protected String sim;

    /**
     * 表低
     */
    protected Double basecount;

    /**
     * 水表状态
     */
    protected Integer status;

    /**
     * 抄表模式
     */
    protected String sendmode;

    /**
     * 最新进度
     */
    protected BigDecimal latelyCount;

    /**
     * 最新抄表时间
     */
    protected String latelyTime;

    /**
     * 单位
     */
    protected Double unit;
}
