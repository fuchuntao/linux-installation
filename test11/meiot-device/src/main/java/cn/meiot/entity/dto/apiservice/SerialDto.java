package cn.meiot.entity.dto.apiservice;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class SerialDto {
    /**
     * 设备号
     */
    @Length(max = 30,min=5,message = "")
    private String serialNumber;

    private Long appId;

    /**
     * 开关
     */
    private List<SwitchApi> switchApiList;
}
