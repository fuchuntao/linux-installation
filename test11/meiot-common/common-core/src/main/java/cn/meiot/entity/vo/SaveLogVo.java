package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveLogVo implements Serializable {


    /**
     * 操作人id
     */
    private Long userId;

    /**
     * 操作人账号
     */
    private String username;

    /**
     * 操作人昵称
     */
    private String name;

    /**
     * 操作模块
     */
    private String actionModel;

    /**
     * 请求url
     */
    private String url;

    /**
     * 日志内容
     */
    private String content;

    /**
     * 参数
     */
    private String param;

    /**
     * IP
     */
    private String ip;

    /**
     * 客户端
     */
    private String useragent;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 主账户id
     */
    private Long mainUserId;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 操作结果   1：成功   0：失败
     */
    private Integer status;

    /**
     * 失败消息
     */
    private String failMsg;


}
