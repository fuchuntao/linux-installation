package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2020/4/16 17:29
 * @Copyright: www.spacecg.cn
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReadNumber {
    /**
     * 未读报警数
     */
    private Integer alarm;

    /**
     * 未读预警数
     */
    private Integer EarlyWarning;
}
