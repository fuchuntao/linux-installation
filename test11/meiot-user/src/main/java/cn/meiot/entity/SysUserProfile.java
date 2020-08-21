package cn.meiot.entity;

import cn.meiot.entity.bo.UserInfo;
import cn.meiot.utils.DTOConvert;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

/**
 * <p>
 *
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SysUserProfile extends Model<SysUserProfile> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 详细地址
     */
    private String addr;

    /**
     * 县
     */
    private String district;

    /**
     * 头像
     */
    private String headPortrait;

    private String activeFlag;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }



}
