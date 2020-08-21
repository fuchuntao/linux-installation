package cn.meiot.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author wuyou
 * @since 2019-10-12
 */
@Data
@Builder
public class ExceptionLogVo implements Serializable {
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
    private LocalDateTime createtime;

    /**
     * 主用户ID
     */
    private Long mainUserId;

    /**
     * 用户类型 同用户表
     */
    private Integer userType;

}
