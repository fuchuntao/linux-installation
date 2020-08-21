package cn.meiot.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 设备信息
 * </p>
 *
 * @author wuyou
 * @since 2019-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Equipment extends Model<Equipment> {

    private static final long serialVersionUID = 1L;

    /**
     * 设备序号
     */
    private String serialNumber;

    /**
     * 状态:0-未激活 1-已激活 2-停用
     */
    private Integer equipmentStatus;

    /**
     * 型号
     */
    private String model;

    /**
     * 信息来源: 1-设备上报 2-人工添加
     */
    private Integer source;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 激活时间
     */
    private LocalDateTime activationTime;

    /**
     * 开关数量
     */
    private Integer switchCount;

    /**
     * 固件版本号
     */
    private String firmwareVersion;

    /**
     * 组织架构
     */
    private Integer buildingId;

    /**
     * 自检格式化时间
     */
    private String examinationTime;

    /**
     * 自检状态0:关闭 1：开启
     */
    private Integer examinationStatus;

    /**
     * 额定电压
     */
    private Integer voltage;

    /**
     * 最大功率
     */
    private Integer loadmax;

    /**
     * 版本号
     */
    private String version;


    @Override
    protected Serializable pkVal() {
        return this.serialNumber;
    }

}
