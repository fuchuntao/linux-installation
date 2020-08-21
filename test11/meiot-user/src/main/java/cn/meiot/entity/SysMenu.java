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
* @since 2019-08-02
*/
 @Data
 @EqualsAndHashCode(callSuper = false)
 @Accessors(chain = true)
public class SysMenu extends Model<SysMenu> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
