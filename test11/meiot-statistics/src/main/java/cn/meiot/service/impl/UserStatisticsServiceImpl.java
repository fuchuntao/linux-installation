package cn.meiot.service.impl;

import cn.meiot.entity.UserStatistics;
import cn.meiot.entity.vo.PcDataVo;
import cn.meiot.entity.vo.PcUserStatisticsVo;
import cn.meiot.mapper.UserStatisticsMapper;
import cn.meiot.service.IUserStatisticsService;
import cn.meiot.utils.TimerUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户统计表 服务实现类
 * </p>
 *
 * @author 符纯涛
 * @since 2019-09-28
 */
@Slf4j
@Service
public class UserStatisticsServiceImpl extends ServiceImpl<UserStatisticsMapper, UserStatistics> implements IUserStatisticsService {


    @Autowired
    private UserStatisticsMapper userStatisticsMapper;

    private Calendar cal = Calendar.getInstance();

    /**
     *
     * @Title: updateUser
     * @Description: 添加用户
     * @param pcUserStatisticsVo
     * @return: void
     */
    @Override
    public void updateUser(PcUserStatisticsVo pcUserStatisticsVo) {

        //获取添加类型type(0:减少账户， 1添加账户)
        Integer type = pcUserStatisticsVo.getType();
        if(type == null) {
            log.info("数据统计获取添加账户类型为空");
            return;
        }
        UserStatistics userStatistics = new UserStatistics();
        //获取当前年份
        int year = cal.get(Calendar.YEAR);
        //获取当前月份
        int month = cal.get(Calendar.MONTH)+1;

        String date = pcUserStatisticsVo.getDate();

        Integer userType = pcUserStatisticsVo.getUserType();
        if(userType == null) {
            log.info("数据统计获取添加账户是企业还是个人为空");
            return;
        }


        if(!StringUtils.isNotBlank(date)) {
            log.info("数据统计获取添加账户创建时间为空");
            return;
        }
        if(userType.equals(2) || userType.equals(5)) {
            //获取创建时间
            Long time = TimerUtil.getTime(date) * 1000;
            Calendar calTime = Calendar.getInstance();
            calTime.setTimeInMillis(time);
            int userYear = calTime.get(Calendar.YEAR);
            int userMonth = calTime.get(Calendar.MONTH)+1;

            //修改账户
            if(type.equals(0)) {
                userStatistics.setMonth(month);
                userStatistics.setYear(year);
                //判断创建的时间的月是否和当前时间月一样
                if(userYear == year && userMonth == month) {
                    userStatistics.setType(userType);
                    //查询当前月的用户并且更新
                    Integer userTotal = userStatisticsMapper.getUserTotal(userStatistics);
                    if(userTotal >= 1) {
                        userTotal = userTotal - 1;
                        userStatistics.setUserTotal(userTotal);
                        userStatisticsMapper.updateUserTotal(userStatistics);
                        log.info("数据统计获取减少账户成功！");
                    }
                }
                //添加账户
            } else if(type.equals(1)) {
                //查询创建时间的月份中是否有数据
                userStatistics.setMonth(userMonth);
                userStatistics.setYear(userYear);
                userStatistics.setType(userType);
                Integer userTotal = userStatisticsMapper.getUserTotal(userStatistics);
                //有更新操作
                if(userTotal != null) {
                    userTotal = userTotal + 1;
                    userStatistics.setUserTotal(userTotal);
                    userStatisticsMapper.updateUserTotal(userStatistics);
                    log.info("数据统计获取修改添加账户成功！");
                }else {
                    //没有添加数据
                    userStatistics.setUserTotal(1);
                    userStatisticsMapper.insert(userStatistics);
                    log.info("数据统计获取添加账户成功！");
                }
            }
        }
    }

    /**
     *
     * @Title: getUserTotal
     * @Description: 根据用户类型查询所有的用户统计
     * @param pcDataVo
     * @return: java.util.List<cn.meiot.entity.vo.PcDeviceStatisticsVo>
     */
    @Override
    public List<Map<String, Object>> getUserTotalList(PcDataVo pcDataVo) {
        return userStatisticsMapper.getUserTotalList(pcDataVo);
    }

    /**
     *
     * @Title: statisticsUser
     * @Description: 根据定时拉取队列添加用户
     * @param
     * @return: void
     */
    @Override
    public void statisticsUser() {
        //获取当前年份
        int year = cal.get(Calendar.YEAR);
        //获取当前月份
        int month = cal.get(Calendar.MONTH);
        UserStatistics userStatistics = new UserStatistics();

        List<Integer> typeList = new ArrayList<>();
        typeList.add(2);
        typeList.add(5);



        List<UserStatistics> list = userStatisticsMapper.getList(userStatistics);
        if(list == null) {
            for(Integer integer : typeList) {
                userStatistics.setYear(year);
                userStatistics.setMonth(month);
                userStatistics.setType(integer);
                userStatistics.setUserTotal(0);
                userStatisticsMapper.insert(userStatistics);
            }
        }else if(list.size() == 1){
          for (UserStatistics userStatistics1 : list){
              Integer type = userStatistics1.getType();
              userStatistics.setYear(year);
              userStatistics.setMonth(month);
              userStatistics.setUserTotal(0);
              if(type.equals(2)) {
                  userStatistics.setType(5);
                  userStatisticsMapper.insert(userStatistics);
              }else {
                  userStatistics.setType(2);
                  userStatisticsMapper.insert(userStatistics);
              }
          }
        }


    }

//    @Override
//    public UserNumBo getCount(Integer sigle, Integer enterprise) {
//
//
//        return userStatisticsMapper.getCount(sigle,enterprise);
//    }
}
