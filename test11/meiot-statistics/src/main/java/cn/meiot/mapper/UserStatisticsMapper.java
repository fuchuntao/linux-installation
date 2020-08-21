package cn.meiot.mapper;

import cn.meiot.entity.UserStatistics;
import cn.meiot.entity.vo.PcDataVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户统计表 Mapper 接口
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
@Mapper
public interface UserStatisticsMapper extends BaseMapper<UserStatistics> {


    /**
     *
     * @Title: getUserTotal
     * @Description: 获取用户数
     * @param
     * @return: java.lang.Integer
     */
    Integer getUserTotal(@Param("userStatistics") UserStatistics userStatistics);


    /**
     *
     * @Title: updateUserTotal
     * @Description: 修改用户数
     * @param userStatistics
     * @return: int
     */
    int updateUserTotal(@Param("userStatistics") UserStatistics userStatistics);


    /**
     *
     * @Title: getUserTotalList
     * @Description: 查询不同平台所有用户的统计
     * @param pcDataVo
     * @return: java.util.List<cn.meiot.entity.vo.PcDeviceStatisticsVo>
     */
    List<Map<String, Object>> getUserTotalList(@Param("pcDataVo") PcDataVo pcDataVo);


    /**
     *
     * @Title: getList
     * @Description: 获取上个月用户数
     * @param
     * @return:
     */
    List<UserStatistics> getList(@Param("userStatistics") UserStatistics userStatistics);

    /**
     * 获取个人以及企业的用户数量
     * @param single  个人
     * @param enterprise 企业
     * @return
     */
//    @Select(" SELECT IFNULL((SELECT SUM(user_total) FROM user_statistics WHERE TYPE = #{single}),0) AS companyUserSum ,IFNULL((SELECT SUM(user_total) FROM user_statistics WHERE TYPE = #{enterprise}),0 )  AS userSum ")
//    UserNumBo getCount(@Param("single") Integer single,@Param("enterprise") Integer enterprise);
}
