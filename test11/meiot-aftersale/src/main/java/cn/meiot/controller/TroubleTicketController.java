package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.entity.TroubleTicket;
import cn.meiot.entity.vo.PageVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.StatusVo;
import cn.meiot.exception.MyServiceException;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.feign.UserFeign;
import cn.meiot.service.ITroubleTicketService;
import cn.meiot.utils.UserInfoUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 * 新版本app的接口 包括控制 管理平台的接口
 *
 * @author wuyou
 * @since 2020-02-17
 */
@RestController
@RequestMapping("/trouble-ticket")
@SuppressWarnings("all")
@Slf4j
public class TroubleTicketController extends BaseController {

    @Autowired
    private ITroubleTicketService troubleTicketService;

    @Autowired
    private UserInfoUtil userInfoUtil;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private DeviceFeign deviceFeign;

    /**
     * 用户获取自己工单列表
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    @GetMapping("list")
    public Result<TroubleTicket> list(@RequestParam("currentPage") Integer currentPage,
                                      @RequestParam("pageSize") Integer pageSize,
                                      @RequestParam("type") Integer type) {
        Long userId = getUserId();
        Page<TroubleTicket> page = new Page<>(currentPage, pageSize);
        QueryWrapper<TroubleTicket> queryWrapper=new QueryWrapper<TroubleTicket>();
        queryWrapper.and(wrapper -> wrapper.lambda().eq(TroubleTicket::getUserId, userId));
        if (type.equals(0)) {
            queryWrapper.and(wrapper -> wrapper.lambda().eq(TroubleTicket::getType, type));
            queryWrapper.lambda().orderByDesc(TroubleTicket::getId);
        }else{
            queryWrapper.and(wrapper -> wrapper.lambda().ne(TroubleTicket::getType, 0));
            queryWrapper.lambda().orderByDesc(TroubleTicket::getRepairTime);
        }

        IPage<TroubleTicket> iPage = troubleTicketService.page(page, queryWrapper);
        Result result = Result.getDefaultTrue();
        result.setData(iPage);

        return result;
    }

    /**
     * 用户获取自己的报修列表
     */
    @GetMapping("getAfatersaleList")
    public Result getAfatersaleList(@RequestParam("currentPage") Integer currentPage,
                                    @RequestParam("pageSize") Integer pageSize){
        Long userId=getUserId();
        currentPage=(currentPage-1)*pageSize;
       PageVo pageVo =  troubleTicketService.getAfatersaleList(currentPage,pageSize,userId);
       return Result.OK(pageVo);
    }


    /**
     * 一键报修
     */

    @PostMapping("clickRepair")
    public Result clickRepair(@RequestBody Map map) {
        Long userId = getUserId();
        String mobile = userInfoUtil.getUserInfo().getUser().getUserName();
        Integer id = (Integer) map.get("id");
        if (null == id) {
            throw new MyServiceException("ID不能为空", "500");
        }
        String note = (String) map.get("note");
//        if (StringUtils.isEmpty(note)) {
//            throw new MyServiceException("备注内容不能为空","500");
//        }
        TroubleTicket troubleTicket = troubleTicketService.getOne(new QueryWrapper<TroubleTicket>().lambda().eq(TroubleTicket::getId, id).eq(TroubleTicket::getUserId, userId));
        if (troubleTicket == null) {
            throw new MyServiceException("参数不符合规定", "500");
        }
//        TroubleTicket troubleTicket=new TroubleTicket();
        SimpleDateFormat SD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        troubleTicket.setUpdateTime(SD.format(new Date()));
        troubleTicket.setType(1);
        troubleTicket.setNote(note);
        troubleTicket.setTel(mobile);
        troubleTicket.setRepairTime(SD.format(new Date()));
        troubleTicketService.updateById(troubleTicket);
        return Result.getDefaultTrue();
    }

    /**
     * 给出管理平台获取工单列表
     */
    @GetMapping("getList")
    @Log(operateContent = "获取工单列表", operateModule = "售后服务")
    public Result getList(@RequestParam("currentPage") Integer currentPage,
                          @RequestParam("pageSize") Integer pageSize,
                          @RequestParam(value = "search", required = false) String search,
                          @RequestParam(value = "status", required = false) Integer status,
                          @RequestParam(value = "type", required = false) Integer type) {

        QueryWrapper<TroubleTicket> queryWrapper = new QueryWrapper<TroubleTicket>();
        Page<TroubleTicket> page = new Page<>(currentPage, pageSize);
//        queryWrapper.apply(" 1 = 1");
        queryWrapper.and(wrapper-> wrapper.lambda().eq(TroubleTicket::getIsApp,0));
        if (StringUtils.isNotEmpty(search)) {
            queryWrapper.and(wrapper -> wrapper.lambda().like(TroubleTicket::getTel, search).or().like(TroubleTicket::getId, search));
        }
        if (null != status) {
            queryWrapper.and(wrapper -> wrapper.lambda().eq(TroubleTicket::getType, status));
        }
        if (null != type) {
            queryWrapper.and(wrapper -> wrapper.lambda().eq(TroubleTicket::getAlarmType, type));
        }
        queryWrapper.lambda().orderByDesc(TroubleTicket::getCreateTime);
        IPage<TroubleTicket> iPage = troubleTicketService.page(page, queryWrapper);
        Result result = Result.getDefaultTrue();
        result.setData(iPage);
        return result;
    }

    /**
     * 管理平台完成按钮
     *
     * @param id
     * @return
     */
    @GetMapping("editStatus")
    public Result editStatus(@RequestParam(value = "id") Long id) {
        TroubleTicket troubleTicket = troubleTicketService.getOne(new QueryWrapper<TroubleTicket>().lambda().eq(TroubleTicket::getId, id));
        if (troubleTicket == null) {
            throw new MyServiceException("参数不符合规定", "500");
        }
        if (!troubleTicket.getType().equals(1)) {
            throw new MyServiceException("参数不符合规定", "500");
        }
        SimpleDateFormat SD = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        troubleTicket.setUpdateTime(SD.format(new Date()));
        troubleTicket.setType(2);
        troubleTicket.setRepairTime(SD.format(new Date()));
        troubleTicketService.updateById(troubleTicket);
        return Result.getDefaultTrue();
    }

    @PostMapping("updateStatus")
    @Log(operateContent = "更新状态", operateModule = "售后服务")
    public Result updateStatus(@RequestBody List<StatusVo> statusVoList) throws Exception {
        return troubleTicketService.editStatus(statusVoList);
    }


    /**
     * 企业app获取工单列表
     *
     * @param currentPage 当前页
     * @param pageSize    每页大小
     * @param type        0获取一键报修列表，其他获取历史列表
     * @return
     */
    @GetMapping("getEnterpriseList")
    public Result getEnterpriseList(@RequestParam("currentPage") Integer currentPage,
                                    @RequestParam("pageSize") Integer pageSize,
                                    @RequestParam("type") Integer type) {
        Long mainUserId = userFeign.getMainUserIdByUserId(getUserId());
        //查询用户ID的角色 主账号不管
        List<Integer> roles=null;
        List<String> deviceid=null;
        Integer projectId = getProjectId();
        if (!getUserId().equals(mainUserId)) {
            roles= userFeign.getRoleIdByUserId(getUserId());
            deviceid = deviceFeign.queryRoleEquipment(projectId, roles);
            mainUserId=getUserId();
        }
        Page<TroubleTicket> page = new Page<>(currentPage, pageSize);
            QueryWrapper<TroubleTicket> queryWrapper=new QueryWrapper<TroubleTicket>();
            queryWrapper.and(wrapper -> wrapper.lambda().eq(TroubleTicket::getProjectId,projectId));
        Long finalMainUserId = mainUserId;
        queryWrapper.and(wrapper ->wrapper.lambda().eq(TroubleTicket::getUserId, finalMainUserId));
        if (null != deviceid && deviceid.size() > 0) {
            List<String> finalDeviceid = deviceid;
            queryWrapper.and(wrapper -> wrapper.lambda().in(TroubleTicket::getDeviceId, finalDeviceid));
        }
            if (type.equals(0)) {
                queryWrapper.and(wrapper -> wrapper.lambda().eq(TroubleTicket::getType, type));
                queryWrapper.lambda().orderByDesc(TroubleTicket::getId);
            }else{
//                queryWrapper.and(wrapper -> wrapper.lambda().ne(TroubleTicket::getType, 0));
                queryWrapper.lambda().orderByDesc(TroubleTicket::getRepairTime);
            }
            IPage<TroubleTicket> iPage = troubleTicketService.page(page, queryWrapper);
            return Result.OK(iPage);
    }



    /**
     * 给出控制平台获取工单列表
     */
    @GetMapping("getEnterprise")
    @Log(operateContent = "获取工单列表", operateModule = "售后服务")
    public Result getEnterprise(@RequestParam("currentPage") Integer currentPage,
                          @RequestParam("pageSize") Integer pageSize,
                          @RequestParam(value = "search", required = false) String search,
                          @RequestParam(value = "status", required = false) Integer status,
                          @RequestParam(value = "type", required = false) Integer type) {
        Integer projectId=getProjectId();
        Long userId = getUserId();
        Long mainUserId = userFeign.getMainUserIdByUserId(userId);
        QueryWrapper<TroubleTicket> queryWrapper = new QueryWrapper<TroubleTicket>();
        Page<TroubleTicket> page = new Page<>(currentPage, pageSize);
        queryWrapper.and(wrapper->wrapper.lambda().eq(TroubleTicket::getIsApp,1).eq(TroubleTicket::getProjectId,projectId).eq(TroubleTicket::getUserId,mainUserId));
        if (StringUtils.isNotEmpty(search)) {
            queryWrapper.and(wrapper -> wrapper.lambda().like(TroubleTicket::getTel, search).or().like(TroubleTicket::getId, search));
        }
        if (null != status) {
            queryWrapper.and(wrapper -> wrapper.lambda().eq(TroubleTicket::getType, status));
        }
        if (null != type) {
            queryWrapper.and(wrapper -> wrapper.lambda().eq(TroubleTicket::getAlarmType, type));
        }
//        queryWrapper.lambda().ne(TroubleTicket::getType, 0).orderByDesc(TroubleTicket::getCreateTime);
        queryWrapper.lambda().orderByDesc(TroubleTicket::getCreateTime);
        IPage<TroubleTicket> iPage = troubleTicketService.page(page, queryWrapper);
        Result result = Result.getDefaultTrue();
        result.setData(iPage);
        return result;
    }


    @PostMapping("updateEnterpriseStatus")
    @Log(operateContent = "更新状态", operateModule = "售后服务")
    public Result updateEnterpriseStatus(@RequestBody List<StatusVo> statusVoList) throws Exception {
        return troubleTicketService.editStatus(statusVoList);
    }


    @GetMapping("getEnterpriseApp")
    public Result getEnterpriseApp(@RequestParam("currentPage") Integer currentPage,
                                   @RequestParam("pageSize") Integer pageSize) {
        Long userId=getUserId();
        currentPage=(currentPage-1)*pageSize;
        PageVo pageVo =  troubleTicketService.getList(currentPage,pageSize,userId,getProjectId());
        return Result.OK(pageVo);
    }
}
