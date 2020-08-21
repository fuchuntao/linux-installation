package cn.meiot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;

/**
 * <p>
 *
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysPermission extends Model<SysPermission> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限url
     */
    private String url;

    /**
     * 所属模块
     */
    private Integer pid;

    /**
     * 权限类型  1：平台  2：企业
     */
    private Integer type;

    /**
     * 权限类型 1：菜单 2：按钮
     */
    private Integer pType;

    /**
     * 权限的唯一标识
     */
    private String permission;

    /**
     * 是否选中   1：选中   0：未选中
     */
    @TableField(exist = false)
    private Integer checked;

    /**
     * 子集菜单
     */
    @TableField(exist = false)
    private List<SysPermission> childNodes ;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
