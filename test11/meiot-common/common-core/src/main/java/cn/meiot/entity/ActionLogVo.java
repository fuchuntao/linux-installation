package cn.meiot.entity;

import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Package cn.meiot.entity
 * @Description:
 * @author: 武有
 * @date: 2019/10/29 9:10
 * @Copyright: www.spacecg.cn
 */
@Data
@Builder
public class ActionLogVo implements Serializable {


    /**
     * ID
     */
    private Long id;

    /**
     * 管理员ID
     */
    private Long userId;

    /**
     * 用户账号
     */
    private String username;

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
     * 创建时间
     */
    private LocalDateTime createTime;


    /**
     * 日志属于？
     * 账户类型 1 运营 2 企业 3 代理商 4 维修 5 个人
     */
    private Integer type;

    /**
     * 作为企业日志的时候需要加账号主id
     */
    private Long mainUserId;

    /**
     * 用户昵称
     */
    private String name;


}
