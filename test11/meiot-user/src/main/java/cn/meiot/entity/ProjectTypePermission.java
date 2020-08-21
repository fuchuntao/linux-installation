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
* @since 2019-09-25
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public class ProjectTypePermission extends Model<ProjectTypePermission> {

    private static final long serialVersionUID = 1L;

            @TableId(value = "id", type = IdType.AUTO)
    private Long id;

            /**
            * 项目类型id
            */
    private Integer projectTypeId;

            /**
            * 权限id
            */
    private Integer permissionId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
