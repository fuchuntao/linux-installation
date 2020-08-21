package cn.meiot.entity.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;

@Data
public class PcUserInfo extends BaseRowModel {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 账号
     */
    @ExcelProperty(value = {"账号"},index = 0)
    private String userName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 昵称
     */
    @ExcelProperty(value = {"昵称"},index = 1)
    private String nickName;

    /**
     * 1 正常 2 禁用
     */
    private Integer status;

    /**
     * 邮箱
     */
    private String email;

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
    private String address;
    /**
     * 地址（导出使用）
     */
    @ExcelProperty(value = {"地址"},index = 4)
    private String addr;

    /**
     * 头像
     */
    private String headPortrait;

    /**
     * 企业名称
     */
    @ExcelProperty(value = {"企业名称"},index = 1)
    private  String enterpriseName;

    /**
     * 法人姓名
     */
    @ExcelProperty(value = {"法人姓名"},index = 2)
    private String contacts;

    /**
     * 联系电话
     */
    @ExcelProperty(value = {"联系电话"},index = 3)
    private String phone;

    /**
     * 注册时间
     */
    @ExcelProperty(value = {"注册时间"},index = 5)
    private String createTime;

    /**
     * 最近登录时间
     */
    @ExcelProperty(value = {"最近登录时间"},index = 6)
    private String loginTime;

    /**
     * 是否绑定设备
     */
    private Integer isBindDevice;

    /**
     * 设备数量
     */
    private Integer deviceNum;
}
