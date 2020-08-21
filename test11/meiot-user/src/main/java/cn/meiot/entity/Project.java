package cn.meiot.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project  extends BaseRowModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ExcelProperty(value = "项目id",index = 0)
    private Integer id;

    /**
     * 项目名称
     */
    @NotEmpty(message = "项目名称不能为空")
    @ExcelProperty(value = "项目名称",index = 1)
    private String projectName;

    /**
     * 企业id
     */
    @NotNull(message = "企业类型不能为空")
    private Integer enterpriseId;

    /**
     * 公司名称
     */
    @TableField(exist = false)
    @ExcelProperty(value = "公司名称",index =2 )
    private String enterpriseName;

    /**
     * 项目类型
     */
    @NotNull(message = "项目类型不能为空")
    private Integer projectType;
    /**
     * 项目类型
     */
    @TableField(exist = false)
    @ExcelProperty(value = "项目类型",index = 3)
    private String projectTypeStr;


    /**
     * 对接人
     */
    @ExcelProperty(value = "对接人",index = 4)
    private String contacts;

    /**
     * 联系电话
     */
    @ExcelProperty(value = "联系电话",index = 5)
    private String phone;

    /**
     * 邮箱
     */
    @ExcelProperty(value = "邮箱",index =6 )
    private String email;
    /**
     * 代理商id
     */
    private Integer agentId;

    /**
     * 项目类型
     */
    @TableField(exist = false)
    @ExcelProperty(value = "设备数量",index = 7)
    private Integer deviceNum;

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
    private Integer  deleted;

}
