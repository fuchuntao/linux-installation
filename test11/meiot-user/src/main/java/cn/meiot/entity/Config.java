package cn.meiot.entity;

import cn.meiot.entity.enums.ConfigKeyTypeEnum;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

/**
 * <p>
 *
 * </p>
 *
 * @author yaomaoyang
 * @since 2020-02-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Config extends Model<Config> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * key值
     */
    private String cKey;

    /**
     * 标题
     */
    private String title;

    /**
     * value值
     */
    //@NotEmpty(message = "value不能为空")
    private String value;

    /**
     * 文件的url
     */
    @TableField(exist = false)
    private String fileUrl;


    /**
     * key值的类型：0：文本   1：文件
     */
    //@JSONField(serialzeFeatures= SerializerFeature.WriteEnumUsingToString)
    private Integer keyType;

    /**
     * 给前端展示
     */
    @TableField(exist = false)
    private String keyTypeStr;

    /**
     * 描述
     */
    //@NotEmpty(message = "描述不能为空")
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 配置的类型 0：所有使用 1：系统使用 2：用户使用
     */

    private Integer type;

    /**
     * 前端展示使用
     */
    @TableField(exist = false)
    private String typeStr;

    /**
     * 是否被删除   0：否   1：是
     */
    private Integer deleted;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
