package cn.meiot.entity;

import cn.meiot.utils.DateUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author wuyou
 * @since 2019-10-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ExceptionLog extends Model<ExceptionLog> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 当前操作用户
     */
    private Long userId;

    /**
     * 用户的账户号
     */
    private String username;

    /**
     * 用户的昵称
     */
    private String name;

    /**
     * 操作的当前模块
     */
    private String actionModel;

    private String ip;

    /**
     * 操作内容
     */
    private String content;

    /**
     * 参数
     */
    private String param;

    /**
     * 客户端
     */
    private String useragent;

    /**
     * 操作的uri
     */
    private String url;

    /**
     * 异常消息
     */
    private String msg;

    /**
     * 返回状态码
     */
    private Long status;

    /**
     * 异常时间
     */
    private Date createtime;

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
       formatCreateTime= DateUtil.sd.format(createtime);
    }

    @TableField(exist = false)
    private String formatCreateTime;
    /**
     * 主用户ID
     */
    private Long mainUserId;

    /**
     * 用户类型 同用户表
     */
    private Integer userType;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
