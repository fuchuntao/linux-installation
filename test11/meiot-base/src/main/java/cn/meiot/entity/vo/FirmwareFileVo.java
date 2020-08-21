package cn.meiot.entity.vo;

import cn.meiot.entity.Files;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/11/22 8:53
 * @Copyright: www.spacecg.cn
 */
@Data
public class FirmwareFileVo {
    /**
     * 主键
     */
    private Long id;

    /**
     * 版本号
     */
    private String version;

    /**
     * 固件名称
     */
    private String name;

    /**
     * 推送时间
     */
    private Date pushTime;

    /**
     * 是否强制升级 0不 1是
     */
    private Integer isUpgrade;

    /**
     * 状态  0待推送 1推送中 2推送完成
     */
    private Integer type;

    /**
     * is_now 是否立即推送
     */
    private Integer isNow;

    /**
     * is_list 是否是部分推送 0全部推送 1部分推送
     */
    private Integer isList;
    /**
     * 创建时间
     */
    private Date createTime;

    private Files files;
}
