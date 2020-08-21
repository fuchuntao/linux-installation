package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryProjectVo {

    /**
     * 企业id
     */
    private Integer enterpriseId;

    /**
     * 项目类型
     */
    private Integer type;

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 分页信息
     */
    private PageVo pageVo;
}
