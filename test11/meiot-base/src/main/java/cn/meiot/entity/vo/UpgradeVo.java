package cn.meiot.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/11/29 8:47
 * @Copyright: www.spacecg.cn
 */
@Data
public class UpgradeVo  {
    private String version;
    private String description;
    private List<DeviceVersionVo> list;

    public UpgradeVo(String version,String description, List<DeviceVersionVo> list) {
        this.version = version;
        this.list = list;
        this.description=description;
    }

    public UpgradeVo() {
    }
}
