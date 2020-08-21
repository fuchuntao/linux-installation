package cn.meiot.entity.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

@Data
public class ExportSingleUserBo  extends BaseRowModel {

    /**
     * 账号
     */
    @ExcelProperty(value ={"账号"},index = 0)
    private String account;

    /**
     *昵称
     */
    @ExcelProperty(value ={"昵称"},index = 1)
    private String  nikName;

    /**
     *地址
     */
    @ExcelProperty(value ={"地址"},index = 2)
    private String  addr;

    /**
     * 注册时间
     */
    @ExcelProperty(value ={"注册时间"},index = 3)
    private String  createTime;

    /**
     *最近登录时间
     */
    @ExcelProperty(value ={"最近登录时间"},index = 4)
    private String  loginTime;
}
