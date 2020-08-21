package cn.meiot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Map;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;

/**
 * <p>
 * 系统消息
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemMessage extends Model<SystemMessage> {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 绑定用户id
     */
    private Long userId;

    /**
     * 关联id（扩展用）
     */
    private Long extendId;

    /**
     * 设备序列号
     */
    private String serialNumber;

    /**
     * 设备名称
     */
    private String serialName;

    /**
     * 消息类型(0-系统公告,1-绑定请求,2-绑定信息,其他消息)
     */
    private Integer type;

    /**
     * 消息子标题
     */
    private String subtitle;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 是否已读
     */
    private Integer isRead;

    /**
     * 修改时间
     */
    private String updateTime;

    /**
     * 发布时间
     */
    private String createTime;

    /**
     * 扩展参数
     */
    private String extras;

    /**
     * 处理结果
     */
    private Integer dealStatus;

    /**
     * 扩展参数（集合）
     */
    @TableField(exist = false)
    private Map<String,Object> map;

    /**
     * 公告ID
     */
    private Long bulletinId;

    /**
     * 过期时间
     */
    private String expireDate;

    /**
     * 项目ID
     * @return
     */
    private Integer projectId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
