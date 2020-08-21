package cn.meiot.service;

import cn.meiot.entity.Maintenance;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.StatisticsVo;
import cn.meiot.entity.vo.StatusVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-28
 */
public interface IMaintenanceService extends IService<Maintenance> {

    /**
     * 保存报修订单
     * @param maintenance
     * @return
     */
    Result saveMaintenance(Maintenance maintenance);

    /**
     * 修改报修订单的状态
     * @param type 类型
     * @param id 记录id
     * @return
     */
    Result updateStatus(Integer type, Long id);

    /**
     * 通过id查询详情
     * @param id
     * @param userId
     * @return
     */
    Result getDetail(Long id, Long userId);

    /**
     * 查询保修列表
     * @param paramMap
     * @return
     */
    Result getAfterSaleByPage(Map<String, Object> paramMap);

    /**
     * 修改售后记录状态
     * @param statusVoList
     * @return
     */
    Result editStatus(List<StatusVo> statusVoList) throws Exception;

    /**
     * 根据ID查询报修记录
     * @param id
     * @return
     */
    Result getAfterSaleById(Long id,Long userId);

    /**
     * 统计
     * @return
     */
    List<StatisticsVo> getStatistics(String serialNumber);
}
