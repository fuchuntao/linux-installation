package cn.meiot.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import lombok.ToString;

/**
 * @Package cn.meiot.entity.vo
 * @Description:
 * @author: 武有
 * @date: 2019/11/21 13:54
 * @Copyright: www.spacecg.cn
 */
@Data
public class WhiteListExeclVo  extends BaseRowModel {
    /**
     * 设备序列号
     */
    @ExcelProperty(value = "设备序列号",index = 0)
    private String serialNumber;


}
