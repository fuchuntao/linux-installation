package cn.meiot.entity.vo;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class EnterpriseUserVo {

    private Long id;

    /**
     * 账号
     */
    @NotEmpty(message = "账号不能为空")
    private String account;

    /**
     * 公司名称
     */
    private String companName;

    /**
     * 法人
     */
    private String legalName;

    /**
     * 邮箱
     */
    @NotEmpty(message = "邮箱不能为空")
    private String email;

    /**
     * 企业类型
     */
//    private Integer enterpriseType;


    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 县
     */
    private String district;

    /**
     * 详细地址
     */
    private  String addr;
}
