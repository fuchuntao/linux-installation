package cn.meiot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

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
 * @since 2019-11-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Firmware extends Model<Firmware> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 版本号
     */
    private String version;

    /**
     * 固件名称
     */
    private String name;

    /**
     * 推送时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String pushTime;

    /**
     * 是否强制升级 0不 1是
     */
    private Integer isUpgrade;

    /**
     * 状态  0待推送 1推送中 2推送完成
     */
    private Integer type;

    /**
     * is_now 是否立即推送
     */
    private Integer isNow;

    /**
     * 白名单名称
     */
    private String whiteName;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * is_list 是否是部分推送 0全部推送 1部分推送
     */
    private Integer isList;

    /**
     * 文件大小
     */
    @TableField(exist = false)
    private Long size;
    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    public Firmware(String version, String name, String pushTime, Integer isUpgrade, Integer isNow, Integer isList, String createTime) {
        this.version = version;
        this.name = name;
        this.pushTime = pushTime;
        this.isUpgrade = isUpgrade;
        this.isNow = isNow;
        this.isList = isList;
        this.createTime = createTime;
    }

    public Firmware() {
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
