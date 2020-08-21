package cn.meiot.entity;

import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.utils.CommonUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *
 * </p>
 *
 * @author wuyou
 * @since 2019-11-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Files extends Model<Files> {

    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件地址
     */
    private String address;

    /**
     * 0a 2b
     */
    private Integer region;

    /**
     * 外键
     */
    private Long firmwareId;

    public Files(String name, String address, Integer region) {
        this.name = name;
        this.address = address;
        this.region = region;
    }
    public Files(){}




    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
