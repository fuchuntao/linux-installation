package cn.meiot.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/11/30 8:57
 * @Copyright: www.spacecg.cn
 */
@Data
public class UpVo implements Serializable {
    private String serialNumber;
    private UploadVo uploadVo;

    public UpVo(String serialNumber, UploadVo uploadVo) {
        this.serialNumber = serialNumber;
        this.uploadVo = uploadVo;
    }

    public UpVo() {
    }
}
