package cn.meiot.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2020/2/27 9:54
 * @Copyright: www.spacecg.cn
 */
@Data
public class ResultSysMsgVo implements Serializable,Comparable<ResultSysMsgVo> {

    /**
     * id type=0系统公告ID 以此类推
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;
    /**
     * 时间
     */
    private String time;
    /**
     * 设备码
     */
    private String serialNumber;

    /**
     * 处理状态
     */
    private Integer dealStatus;

    /**
     * 头像
     */
    private String iconUrl;

    /**
     * 类型
     */
    private Integer type;

    /**
     *  已读未读 0未读 1已读
     */
    private Integer isRead;

    /**
     * 是否为主账号
     */
    private Boolean isMain;

    /**
     * 公告地址
     */
    private String bulletinUrl;

    /**
     * 扩展
     * @param o
     * @return
     */
    private String expand;

    @Override
    public int compareTo(ResultSysMsgVo o) {
        return this.time.compareTo(o.getTime());
    }
}
