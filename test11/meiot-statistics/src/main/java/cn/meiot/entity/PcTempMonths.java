package cn.meiot.entity;

    import java.math.BigDecimal;
    import com.baomidou.mybatisplus.annotation.IdType;
    import com.baomidou.mybatisplus.extension.activerecord.Model;
    import com.baomidou.mybatisplus.annotation.TableId;
    import java.time.LocalDateTime;
    import java.io.Serializable;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 企业平台月数据统计表
    * </p>
*
* @author 凌志颖
* @since 2019-10-21
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public class PcTempMonths extends Model<PcTempMonths> {

    private static final long serialVersionUID = 1L;

            @TableId(value = "id", type = IdType.AUTO)
    private Long id;

            /**
            * 设备序列号
            */
    private String serialNumber;

            /**
            * 开关序号
            */
    private Integer switchIndex;

            /**
            * 开关编号
            */
    private Long switchSn;

            /**
            * 温度
            */
    private BigDecimal temp;

            /**
            * 年
            */
    private Integer sYear;

            /**
            * 月
            */
    private Integer sMonth;

            /**
            * 日
            */
    private Integer sDay;

            /**
            * 创建时间
            */
    private LocalDateTime createTime;

            /**
            * 主账户id
            */
    private Integer userId;

            /**
            * 项目id
            */
    private Long projectId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
