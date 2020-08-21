package cn.meiot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
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
 * @since 2020-02-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserUnionid extends Model<UserUnionid> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long uId;

    /**
     * 微信唯一标识
     */
    private String unionid;

    /**
     * 关注微信公众号唯一标识
     */
    private String openid;

    /**
     * 微信头像
     */
    private String nickName;

    /**
     * 微信头像
     */
    private String headImgurl;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 是否删除   0：否   1：是
     */
    private Integer deleted;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
