package cn.meiot.entity.bo;

import lombok.Data;

@Data
public class ConfigUserBo {


    /**
     * key值
     */
    private String cKey;

    /**
     * 标题
     */
    private String title;

    /**
     * value值
     */
    private String value;

    /**
     * key值的类型：0：文本   1：文件
     */
    private Integer keyType;

    /**
     * 描述
     */
    private String description;
}
