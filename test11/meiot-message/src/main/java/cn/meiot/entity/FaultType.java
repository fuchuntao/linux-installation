package cn.meiot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FaultType extends Model<FaultType> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 故障类型名称
     */
    private String fName;

    /**
     * 故障图片路径
     */
    private String fImg;

    /**
     * 别名
     */
    private String fAlias;

    /**
     * 符号
     */
    private String fAymbol;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
