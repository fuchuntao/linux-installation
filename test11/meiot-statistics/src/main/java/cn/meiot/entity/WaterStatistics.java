package cn.meiot.entity;

import cn.meiot.entity.water.Record;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author 符纯涛
 * @since 2020-02-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WaterStatistics extends Model<WaterStatistics> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "record_id", type = IdType.AUTO)
    private Long recordId;


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
    private String deviceid;
    /**
     *核对状态
     */
    private String checked;
    /**
     *核对者
     */
    private String checker;
    /**
     *抄表时间
     */
    private Long readtime;
    /**
     *抄表读数
     */
    private BigDecimal readcount;

    /**
     * 单位
     */
    private Double unit;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 项目id
     */
    private Integer projectId;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;


//    public WaterStatistics(Map map) {
//        //单位
//        this.unit = (Double)map.get("unit");
//        //用户id
//        this.userId = Long.valueOf(map.get("userId").toString()) ;
//        //项目id
//        this.projectId = (Integer) map.get("projectId");
//    }

//    public void setChecked(String checked) {
//        if("false".equals(checked)){
//            this.checked = 0;
//        }else{
//            this.checked = 1;
//        }
//    }
//
//    public void setChecked(Integer checked) {
//        this.checked = checked;
//    }

    @Override
    protected Serializable pkVal() {
        return this.recordId;
    }

}
