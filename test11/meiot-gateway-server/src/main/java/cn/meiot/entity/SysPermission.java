package cn.meiot.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

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
public class SysPermission {

    private static final long serialVersionUID = 1L;

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
     * 权限的唯一标识
     */
    private String permission;

    /**
     * 是否选中   1：选中   0：未选中
     */
    private Integer checked;

    /**
     * 子集菜单
     */
    private List<SysPermission> childNodes ;


}
