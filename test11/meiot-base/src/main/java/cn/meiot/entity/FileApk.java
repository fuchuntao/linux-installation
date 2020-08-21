package cn.meiot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 *
 * </p>
 *
 * @author wuyou
 * @since 2020-03-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FileApk extends Model<FileApk> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * apk名称
     */
    private String apkName;

    /**
     * 版本号
     */
    private String versionName;

    /**
     * 描述
     */
    private String description;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 0不是默认 1默认
     */
    private Boolean isDefault;

    /**
     * 上传时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

    /**
     * 类型
     * @return
     */
    private Integer type;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
