package cn.meiot.entity.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @ClassName:
 * @Description: 用户统计数据
 * @author: 符纯涛
 * @date: 2019/9/20
 */
@Data
public class PcUserStatistics {

    /**
     * 用户类型名
     */
    private String name;


    /**
     * 用户类型
     */
    private Integer type;


    private List<Map<String, Object>> pcDeviceStatisticsVoList;

}
