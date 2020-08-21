package cn.meiot.entity.vo;

import cn.meiot.entity.WaterStatistics;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author 符纯涛
 * @since 2020-02-24
 */
@Data
public class WaterStatisticsDataVo extends WaterStatistics{
    /**
     *抄表的实际差值度数
     */
    private BigDecimal water;

}
