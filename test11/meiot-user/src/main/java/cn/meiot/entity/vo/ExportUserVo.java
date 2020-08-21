package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportUserVo {

    /**
     * 操作人
     */
    private Long userId;

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 主账户id
     */
    private Integer belongId;

    /**
     * 导出的文件名
     */
    private String fileName;
}
