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
* @since 2019-09-17
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    public class EnterpriseRole extends Model<EnterpriseRole> {

    private static final long serialVersionUID = 1L;

            @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

            /**
            * 企业类型
            */
    private Integer enterpriseType;

            /**
            * 权限id
            */
    private Integer permissionId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
