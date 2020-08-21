package cn.meiot.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.time.LocalDateTime;
import java.io.Serializable;

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
 * @since 2019-09-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ProjectType extends Model<ProjectType> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 项目类型名称
     */
    @NotEmpty(message = "类型名称不能为空")
    private String name;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private String updateTime;

    /**
     * 是否删除  0：未删除   1：已删除
     */
    @TableLogic
    @TableField(select = false)
    private Integer deleted;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
