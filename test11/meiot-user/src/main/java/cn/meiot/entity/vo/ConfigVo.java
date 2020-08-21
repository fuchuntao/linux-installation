package cn.meiot.entity.vo;

import cn.meiot.entity.enums.ConfigKeyTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class ConfigVo {

    private Long id;

    /**
     * value值
     */
    @NotEmpty(message = "value不能为空")
    private String value;

    /**
     * 标题
     */
    private String title;



    /**
     * key值的类型：0：文本   1：文件
     */
    private Integer keyType;

    /**
     * 描述
     */
    @NotEmpty(message = "描述不能为空")
    private String description;

    /**
     * 配置的类型 0：所有使用 1：系统使用 2：企业使用 3：个人使用
     */
    @NotNull(message = "请选择用作范围")
    private Integer type;

}
