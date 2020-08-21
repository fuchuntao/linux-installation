package cn.meiot.service;

import cn.meiot.entity.PcMeterYears;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 企业平台年电量统计 服务类
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
public interface IPcMeterYearsService extends IService<PcMeterYears> {


    /**
     * 将上个月的电流总数统计出来存放到年度表中
     * @return
     */
    Result monthStatisticsPc(int year, int month);

    /**
     *
     * @Title: pullMonthStatisticsPc
     * @Description: 手动拉取数据到年表中
     * @param year
     * @param month
     * @return: cn.meiot.entity.vo.Result
     */
    Result pullMonthStatisticsPc(int year, int month);

    /**
     * 获取当前与去年每个月的用电量信息
     * @param projectId
     * @return
     */
    Result queryYearData(Integer projectId);

    /**
     * 通过项目id与年份查询所有的设备号
     * @param projectId
     * @param year
     * @return
     */
    List<String> querySerialNumberByProject(Integer projectId, Integer year,Integer month);



    /**
     *
     * @Title: querySerialNumberByProject
     * @Description: 根据项目获取设备的设备号
     * @param projectId
     * @return: java.util.List<java.lang.String>
     */
    List<String> getIndexAllByProjectId(Integer projectId, int year, int month, int day);
}
