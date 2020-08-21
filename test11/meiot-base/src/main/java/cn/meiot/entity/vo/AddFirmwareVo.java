package cn.meiot.entity.vo;

import cn.meiot.entity.Files;
import cn.meiot.entity.Firmware;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/11/21 9:21
 * @Copyright: www.spacecg.cn
 */
@Data
public class AddFirmwareVo {
    private Firmware firmware;
    private Files a;
    private Files b;

    public AddFirmwareVo(Firmware firmware, Files a, Files b) {
        this.firmware = firmware;
        this.a = a;
        this.b = b;
    }
}
