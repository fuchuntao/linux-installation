package cn.meiot.entity.water;

import lombok.Data;

import java.util.List;

@Data
public class Customer {
    /**
     * id
     */
    private Long id;
    /**
     * 客户编号
     */
    private String ccid;
    /**
     * 客户名称
     */
    private String cname;
    /**
     * 客户地址
     */
    private String caddress;
    /**
     * 客户备注信息
     */
    private String cinfo;
    /**
     * 客户资料所属册号
     */
    private String bookname;
    /**
     * 水表信息
     */
    private List<CustomerImeter> imeter;
}