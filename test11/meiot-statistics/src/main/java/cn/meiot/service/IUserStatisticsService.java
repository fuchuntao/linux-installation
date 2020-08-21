package cn.meiot.service;

import cn.meiot.entity.UserStatistics;
import cn.meiot.entity.bo.UserNumBo;
import cn.meiot.entity.vo.PcDataVo;
import cn.meiot.entity.vo.PcUserStatisticsVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户统计表 服务类
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
public interface IUserStatisticsService extends IService<UserStatistics> {

    /**
     *
     * @Title: updateUser
     * @Description: 拉取队列添加用户
     * @param pcUserStatisticsVo
     * @return: void
     */
    void updateUser(PcUserStatisticsVo pcUserStatisticsVo);




    /**
     *
     * @Title: getUserTotal
     * @Description: 根据用户类型查询所有的用户统计
     * @param pcDataVo
     * @return: java.util.List<cn.meiot.entity.vo.PcDeviceStatisticsVo>
     */
    List<Map<String, Object>> getUserTotalList(PcDataVo pcDataVo);


    /**
     *
     * @Title: statisticsUser
     * @Description: 根据定时拉取队列添加用户
     * @param
     * @return: void
     */
    void  statisticsUser();

    /**
     * 获取个人以及企业的用户数量
     * @param sigle  个人
     * @param enterprise 企业
     * @return
     */
    //UserNumBo getCount(Integer sigle, Integer enterprise);
}
