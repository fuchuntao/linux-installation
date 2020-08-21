package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVo implements Serializable {

    /**
     * 密码
     */
    private String password;

    /**
     * 接收方账号
     */
    private String to;

    /**
     * 账号类型
     */
    private Integer accountType;
}
