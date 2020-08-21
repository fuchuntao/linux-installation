package cn.meiot.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName:
 * @Description: 用户统计数据
 * @author: 符纯涛
 * @date: 2019/9/20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PcUserStatisticsVo implements Serializable {


    /**
     * 用户类型 1：平台，2：企业 ，5：个人
     */
    private Integer userType;

    /**
     * 修改类型 0:减少账户， 1添加账户
     */
    private Integer type;

    /**
     * 创建时间
     */
    private String date;



}
