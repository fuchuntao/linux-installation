package cn.meiot.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2020/3/16 17:13
 * @Copyright: www.spacecg.cn
 */
@Data
public class ApkInfoVo {
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
     * 文件大小
     */
    private Long fileSize;

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
