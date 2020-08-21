package cn.meiot.entity;

import lombok.Data;
import java.io.Serializable;

/**
* <p>
    * 
    * </p>
*
* @author yaomaoyang
* @since 2019-08-02
*/
 @Data
public class SysMenu implements  Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
    * 菜单名称
    */
    private String menuName;

     /**
     * 菜单url
     */
    private String url;


    /**
     * 菜单类型   1：平台  2：企业
     */
    private Integer menuType;

    /**
     * 父菜单名称
     */
    private Integer parentId;



}
