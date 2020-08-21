package cn.meiot.entity.vo;

import lombok.Data;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/9/30 16:10
 * @Copyright: www.spacecg.cn
 */
@Data
public class FaultMessageAndTypeVo {
    private Long id;

    /**
     * 用户
     */
    private Integer userId;

    /**
     * 故障时间
     */
    private String faultTime;

    /**
     * 故障事件
     */
    private Integer switchEvent;

    /**
     * 开关别称
     */
    private String switchAlias;

    /**
     * 设备序列号
     */
    private String serialNumber;

    /**
     * 开关序号
     */
    private Integer switchIndex;

    /**
     * 开关编号
     */
    private String switchSn;

    /**
     * 设备别称
     */
    private String equipmentAlias;

    /**
     * 状态 1-待处理,2=处理中,3=已处理
     */
    private Integer switchStatus;

    /**
     * 是否已读:0=否,1=是
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 最近一次的发送时间
     */
    private String sendTime;

    /**
     * 故障内容
     */
    private String msgContent;

    /**
     * 项目Id
     */
    private Integer  projectId;

    /**
     * 上报的故障值
     */
    private String faultValue;


    private Integer typeId;

    /**
     * 故障类型名称
     */
    private String fName;

    /**
     * 故障图片路径
     */
    private String fImg;

    /**
     * 别名
     */
    private String fAlias;

    /**
     * 符号
     */
    private String fAymbol;

    /**
     * 状态 0一键报修 1待受理 2待维修 3已完成
     */
    private Integer status;

    /**
     * 备注
     */
    private String note;

    private String address;


}
