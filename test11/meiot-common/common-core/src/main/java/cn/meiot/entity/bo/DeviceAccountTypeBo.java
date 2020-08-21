package cn.meiot.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAccountTypeBo implements Serializable {

    /**
     * 企业账户的主账号
     */
    private Long userId;

    /**
     * 是否是企业账号
     */
    private boolean isEnterprise;
}
