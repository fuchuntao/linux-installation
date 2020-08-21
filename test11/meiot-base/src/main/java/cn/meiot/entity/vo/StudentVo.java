package cn.meiot.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/11/21 16:31
 * @Copyright: www.spacecg.cn
 */
@Data
public class StudentVo extends BaseRowModel {
    @ExcelProperty(value = "姓名", index = 0)
    private String name;

    /**
     * 商品名称
     */
    @ExcelProperty(value = "年龄", index = 1)
    private Integer age;
}
