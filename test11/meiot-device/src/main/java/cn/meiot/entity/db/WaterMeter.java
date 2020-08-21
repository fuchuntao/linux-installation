package cn.meiot.entity.db;

import cn.meiot.entity.water.*;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Objects;

@Data
public class WaterMeter {

    public WaterMeter(){}

    public WaterMeter(Imeter imeter){
        this.basecount = imeter.getBasecount();
        this.id = imeter.getId();
        this.caliber = imeter.getCaliber();
        this.unit = imeter.getUnit();
        this.caliber = imeter.getCaliber();
        this.meterid = imeter.getMeterid();
        DeviceDto device = imeter.getDevice();
        this.deviceid = device.getDeviceid();
        this.battery = device.getBattery();
        this.sendmode = device.getSendmode();
        this.status = device.getStatus();
        this.sim = device.getSIM();
    }

    public WaterMeter(CustomerImeter imeter,Customer customer){
        addCustomer(imeter, customer);
    }

    private void addCustomer(CustomerImeter imeter, Customer customer) {
        addCustomer(imeter, customer);
    }

    public void setCustomer (CustomerImeter imeter,Customer customer){
        this.cinfo = customer.getCinfo();
        this.bookname = customer.getBookname();
        this.meterid = imeter.getMeterid();
        this.startTime = imeter.getStarttime();
        this.endTime = imeter.getEndtime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaterMeter that = (WaterMeter) o;
        return meterid.equals(that.meterid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meterid);
    }

    public void setRecord(Record record) {
        this.latelyTime = record.getReadtime();
        this.latelyCount = record.getReadcount();
    }


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
    @Id
    @GeneratedValue(generator = "JDBC")
    protected String meterid;

    /**
     * start_time
     */
    protected Long startTime;

    /**
     * end_time
     */
    protected Long endTime;

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
    protected Long latelyTime;

    /**
     * 单位
     */
    protected Double unit;


}
