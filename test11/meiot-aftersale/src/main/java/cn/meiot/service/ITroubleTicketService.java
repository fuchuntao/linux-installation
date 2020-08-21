package cn.meiot.service;

import cn.meiot.entity.TroubleTicket;
import cn.meiot.entity.TroubleTicketVo;
import cn.meiot.entity.vo.PageVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.StatusVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wuyou
 * @since 2020-02-17
 */
public interface ITroubleTicketService extends IService<TroubleTicket> {


    /**
     * 编辑状态
     * @param statusVoList
     * @return
     */
    Result editStatus(List<StatusVo> statusVoList);

    /**
     * 根据用户ID查询报修列表
     * @param currentPage
     * @param pageSize
     * @param userId
     * @return
     */
    PageVo getAfatersaleList(Integer currentPage, Integer pageSize, Long userId);

    /**
     * 根据企业用户ID查询报修列表
     * @param currentPage 当前页
     * @param pageSize 每页多傻鸟
     * @param userId 用户id
     * @param projectId 项目ID
     * @return
     */
    PageVo getList(Integer currentPage, Integer pageSize, Long userId,Integer projectId);

//    /**
//     * 获取
//     * @param currentPage
//     * @param pageSize
//     * @param search
//     * @param type
//     * @return
//     */
//    List<TroubleTicket> getList(Integer currentPage, Integer pageSize, String search, Integer type);
}
