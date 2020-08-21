package cn.meiot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 *
 * </p>
 *
 * @author wuyou
 * @since 2020-02-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TroubleTicket extends Model<TroubleTicket> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    //报修人id
    private Long userId;

    //报修人电话
    private String tel;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备号
     */
    private String deviceId;

    /**
     * 故障类型ID
     */
    private Integer alarmType;

    /**
     * 故障类型名称
     */
    private String alarmTypeName;

    /**
     * 故障时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String alarmTime;

    /**
     * 开关ID
     */
    private String sn;

    /**
     * 开关名称
     */
    private String snName;

    /**
     * 备注
     */
    private String note;

    /**
     * 0 报修 1受理 2完成
     */
    private Integer type;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    /**
     * 0 显示 1不显示
     */
    private Integer isShow;

    /**
     *电箱位置
     */
    private String address;

    /**
     * 0是个人 1企业
     */
    private Integer isApp;

    /**
     * 项目ID 个人项目id为0  企业大于0
     */
    private Integer projectId;

    private Long alarmId; //故障故障的唯一标识

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
     * 修改时间
     * @return
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updateTime;

    /**
     * 报修时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String repairTime;

    /**
     * 受理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String receptionTime;

    /**
     * 维修时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String maintenanceTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
