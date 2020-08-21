package cn.meiot.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 省市县数据表
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ProvinceCityDistrict extends Model<ProvinceCityDistrict> {

    private static final long serialVersionUID = 1L;

    /**
     * 地区代码
     */
    private Integer id;

    /**
     * 当前地区的上一级地区代码
     */
    private Integer pid;

    /**
     * 地区名称
     */
    private String name;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
