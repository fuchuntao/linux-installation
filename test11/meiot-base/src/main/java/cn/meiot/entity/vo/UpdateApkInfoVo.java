package cn.meiot.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2020/3/17 16:54
 * @Copyright: www.spacecg.cn
 */
@Data
public class UpdateApkInfoVo {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空")
    private Integer id;
    /**
     * apk名称
     */
    @NotNull(message = "apk名称不能为空")
    private String apkName;

    /**
     * 版本号
     */
    @NotNull(message = "版本号不能为空")
    private String versionName;

    /**
     * 描述
     */
    @NotNull(message = "描述不能为空")
    private String description;

    /**
     * 文件地址
     */
    @NotNull(message = "文件地址不能为空")
    private String fileUrl;

    /**
     * 0不是默认 1默认
     */
    @NotNull(message = "必须选择是否为默认")
    private Boolean isDefault;

    @NotNull(message = "类型不可为空")
    private Integer type;
}
