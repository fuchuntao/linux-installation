package cn.meiot.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Package cn.meiot.entity.vo
 * @Description:系统消息设备被编辑vo
 * @author: 武有
 * @date: 2020/5/6 9:58
 * @Copyright: www.spacecg.cn
 */
@Data
@Builder
public class DeviceSwitchVo {

    /**
     * 编辑人
     */
    private String name;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 内容
     */
    private String content;

    /**
     * 开关状态
     */
    private Integer status;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备位置
     */
    private String address;

    /**
     * 数据
     */
    private List<Object> switchName;

    private Map<String,String> extend;
}
