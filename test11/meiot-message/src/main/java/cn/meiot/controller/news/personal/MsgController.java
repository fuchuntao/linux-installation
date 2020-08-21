package cn.meiot.controller.news.personal;

import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.AppUserFaultMsgAlarm;
import cn.meiot.entity.Bulletin;
import cn.meiot.entity.EnterpriseUserFaultMsgAlarm;
import cn.meiot.entity.SystemMessage;
import cn.meiot.entity.vo.CarouselVo;
import cn.meiot.entity.vo.FileConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.ResultSysMsgVo;
import cn.meiot.feign.UserFeign;
import cn.meiot.service.*;
import cn.meiot.utils.RedisConstantUtil;
import cn.meiot.utils.UserImgUrl;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.Resources;
import java.util.*;

/**
 * @Package cn.meiot.controller.news.personal
 * @Description: 新版app个人的消息控制器
 * @author: 武有
 * @date: 2020/2/13 17:00
 * @Copyright: www.spacecg.cn
 */

@RestController
@RequestMapping("personalMsg")
@SuppressWarnings("all")

@Slf4j
public class MsgController extends BaseController {


    @Autowired
    private PersonalMsgService personalMsgService;

    @Autowired
    private PersonalNoticeService personalNoticeService;

    @Autowired
    private ISystemMessageService systemMessageService;

    @Autowired
    private UserImgUrl imgUrl;

    @Autowired
    private IEnterpriseUserFaultMsgAlarmService enterpriseUserFaultMsgAlarmService;


    @Autowired
    private UserFeign userFeign;

    @Resource(name = "bulletinServiceImpl")
    private IBulletinService bulletinService;



    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 个人app首页动态信息轮播
     *
     * @return
     */
    @GetMapping("getNewsMsg")
    public Result getNewsNotice() {
        //获取系统消息以及公告
        List<SystemMessage> systemMessages = personalMsgService.getNewsMsg(getUserId());
        //
        List<AppUserFaultMsgAlarm> appUserFaultMsgAlarms = personalNoticeService.newsNotice(getUserId());
        Set<CarouselVo> set = new HashSet();
        List<ResultSysMsgVo> sysMsgVos = getSysMsgVos(systemMessages, getUserId());
        // CarouselVo.type   0公告 1绑定请求 2解绑请求 3解绑子账户 4报警 5预警
        for (ResultSysMsgVo sysMsgVo : sysMsgVos) {
            CarouselVo carouselVo = new CarouselVo();
            carouselVo.setMsg(sysMsgVo.getContent());
            carouselVo.setTime(sysMsgVo.getTime());
            carouselVo.setType(sysMsgVo.getType());
            set.add(carouselVo);
        }
        for (AppUserFaultMsgAlarm appUserFaultMsgAlarm : appUserFaultMsgAlarms) {
            CarouselVo carouselVo = new CarouselVo();
            carouselVo.setMsg(appUserFaultMsgAlarm.getMsgContent());
            carouselVo.setTime(appUserFaultMsgAlarm.getCreateTime());
            if (appUserFaultMsgAlarm.getType().equals(1)){
            carouselVo.setType(4);

            }else if (appUserFaultMsgAlarm.getType().equals(2)){
                carouselVo.setType(5);
            }
            set.add(carouselVo);
        }
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("list",set);
        resultMap.put("total",personalMsgService.getUnreadTotal(getUserId()));
        Result result = Result.getDefaultTrue();
        result.setData(resultMap);
        return result;

    }

    /**
     * 根据用户ID查询系统未读消息总数
     */
    @GetMapping("getUnreadTotal")
    public Result getUnreadTotal() {
        Integer total = personalMsgService.getUnreadTotal(getUserId());
        Result result = Result.getDefaultTrue();
        result.setData(total);
        return result;
    }


    @GetMapping("msgList")
    public Result msgList(@RequestParam("currentPage") Integer currentPage, @RequestParam("pageSize") Integer pageSize) {
        Page<SystemMessage> page = new Page<SystemMessage>(currentPage, pageSize);
        Long userId = getUserId();
        IPage<SystemMessage> systemMessages = systemMessageService.page(page,
                new QueryWrapper<SystemMessage>().eq("user_id", userId).orderByDesc("create_time"));
        List<ResultSysMsgVo> sysMsgVos = getSysMsgVos(systemMessages.getRecords(),userId);

        //将此人的所有未读消息标记为已读
        Map<String, Object> re = new HashMap<>();
        re.put("list", sysMsgVos);
        re.put("total", systemMessages.getTotal());
        SystemMessage systemMessage = SystemMessage.builder().isRead(1).build();
        systemMessageService.update(systemMessage, new UpdateWrapper<SystemMessage>().eq("user_id", userId).eq("is_read", 0));
        Result result = Result.getDefaultTrue();
        result.setData(re);
        return result;
    }




    public List<ResultSysMsgVo> getSysMsgVos(List<SystemMessage> systemMessages,Long userId){
        List<ResultSysMsgVo> sysMsgVos = new ArrayList<>();
        for (SystemMessage systemMessage : systemMessages) {
            ResultSysMsgVo resultSysMsgVo = new ResultSysMsgVo();
            resultSysMsgVo.setType(systemMessage.getType());
            resultSysMsgVo.setTime(systemMessage.getCreateTime());
            resultSysMsgVo.setIsRead(systemMessage.getIsRead());


//            消息类型(0-系统公告,1-绑定请求,2-解绑请求)

            if (systemMessage.getType().equals(0)) {
                Bulletin bulletin = bulletinService.getById(systemMessage.getBulletinId());
                resultSysMsgVo.setId(String.valueOf(systemMessage.getBulletinId()));
                resultSysMsgVo.setTitle(systemMessage.getSubtitle());
                resultSysMsgVo.setContent(bulletin.getTitle());
//                resultSysMsgVo.setContent(systemMessage.getContent());
                resultSysMsgVo.setBulletinUrl(userFeign.getConfigValueByKey("sys_notice_key")+resultSysMsgVo.getId());
                resultSysMsgVo.setIconUrl(imgUrl.getImgUrl(imgUrl.getRel(RedisConstantUtil.ConfigItem.SYS_PROCLAMATION_DEFAULT_ICON_KEY,redisTemplate)));
                sysMsgVos.add(resultSysMsgVo);
//                log.info("公告消息：{}",res);
                //公告地址
            } else if (systemMessage.getType().equals(1)) {
                if (!StringUtils.isEmpty(systemMessage.getExtras())) {
                    Map<String, Object> extras = (Map<String, Object>) JSON.parse(systemMessage.getExtras());
                    systemMessage.setMap(extras);
                }
                resultSysMsgVo.setId(String.valueOf(systemMessage.getExtendId()));
                Long mainUser = Long.valueOf((String) systemMessage.getMap().get("mainUser"));
                String mainUserPhone=(String) systemMessage.getMap().get("mainUserPhone");
                resultSysMsgVo.setIsMain(mainUser.equals(userId));
                resultSysMsgVo.setDealStatus(systemMessage.getDealStatus());
                if (userId.equals(mainUser)) {
                    Object subUser = redisTemplate.opsForHash().get(RedisConstantUtil.USER_HEAD_PORTRAIT, systemMessage.getMap().get("subUser"));
                    log.info("subUser===>{}",subUser);
                    if (null == subUser) {
//                        subUser=userFeign.getConfigValueByKey(RedisConstantUtil.ConfigItem.USER_DEFAULT_HEAD_PORTRAIT);
                        subUser=imgUrl.getRel(RedisConstantUtil.ConfigItem.USER_DEFAULT_HEAD_PORTRAIT,redisTemplate);
                        log.info("ifnsubUser===>{}",subUser);
                        log.info("ifnSubUser Id >>>{}",systemMessage.getMap().get("subUser"));
                    }
                    resultSysMsgVo.setIconUrl(imgUrl.getImgUrl(redisTemplate) + subUser);
                    resultSysMsgVo.setTitle("账号 " + systemMessage.getMap().get("subUserPhone"));
                    resultSysMsgVo.setContent("申请添加绑定" + systemMessage.getSerialName());
                    resultSysMsgVo.setSerialNumber(systemMessage.getSerialNumber());
                } else {
                    resultSysMsgVo.setTitle(/*""+systemMessage.getMap().get("mainUserPhone")*/ "通知");
                    resultSysMsgVo.setSerialNumber(systemMessage.getSerialNumber());
//                    resultSysMsgVo.setIconUrl(imgUrl.getImgUrl(redisTemplate) + redisTemplate.opsForHash().get(RedisConstantUtil.USER_HEAD_PORTRAIT, systemMessage.getMap().get("mainUser")));
                    resultSysMsgVo.setIconUrl(imgUrl.getImgUrl(imgUrl.getRel(RedisConstantUtil.ConfigItem.SYS_INFORM_DEFAULT_ICON_KEY,redisTemplate)));

                    Integer dealStatus = systemMessage.getDealStatus();
                    if (dealStatus.equals(0)) {
                        resultSysMsgVo.setContent("拒绝了您添加绑定" + systemMessage.getSerialName()+"\n设备码："+systemMessage.getSerialNumber());
                    } else if (dealStatus.equals(1)) {
                        resultSysMsgVo.setContent("您已向" + systemMessage.getSerialName() + "(" + systemMessage.getSerialNumber() + ")" + "的主账号(" + mainUserPhone + ")提交申请，如有反馈将会第一时间通知您，请您耐心等待");
                    } else if (dealStatus.equals(2)) {
                        resultSysMsgVo.setContent("主账号同意了您添加绑定" + systemMessage.getSerialName()+"\n设备码："+systemMessage.getSerialNumber());
                    }
                }
                sysMsgVos.add(resultSysMsgVo);

            } else if (systemMessage.getType().equals(2)) {
                if (!StringUtils.isEmpty(systemMessage.getExtras())) {
                    Map<String, Object> extras = (Map<String, Object>) JSON.parse(systemMessage.getExtras());
                    systemMessage.setMap(extras);
                }
                Long mainUser = Long.valueOf((String) systemMessage.getMap().get("mainUser"));
                resultSysMsgVo.setIsMain(mainUser.equals(userId));
//                resultSysMsgVo.setDealStatus(systemMessage.getDealStatus());
                resultSysMsgVo.setTitle("通知");
                resultSysMsgVo.setIconUrl(imgUrl.getImgUrl(imgUrl.getRel(RedisConstantUtil.ConfigItem.SYS_INFORM_DEFAULT_ICON_KEY,redisTemplate)));
                if (userId.equals(mainUser)) {
                    resultSysMsgVo.setContent("您的账号(" + systemMessage.getMap().get("mainUserPhone") + ")对" + systemMessage.getSerialName() + "(" + systemMessage.getSerialNumber() + ")进行了解绑的操作");
                } else {
                    resultSysMsgVo.setContent("主账号(" + systemMessage.getMap().get("mainUserPhone") + ")解绑了" + systemMessage.getSerialName() +
                            ",您的账号也相应解除绑定,以后将无法监控该设备,请知悉。");
                }
                sysMsgVos.add(resultSysMsgVo);
            } else if (systemMessage.getType().equals(3)) {
                if (!StringUtils.isEmpty(systemMessage.getExtras())) {
                    Map<String, Object> extras = (Map<String, Object>) JSON.parse(systemMessage.getExtras());
                    systemMessage.setMap(extras);
                }
                Long mainUser = Long.valueOf((String) systemMessage.getMap().get("mainUser"));
                resultSysMsgVo.setIsMain(mainUser.equals(userId));
                resultSysMsgVo.setTitle("通知");
                resultSysMsgVo.setIconUrl(imgUrl.getImgUrl(imgUrl.getRel(RedisConstantUtil.ConfigItem.SYS_INFORM_DEFAULT_ICON_KEY,redisTemplate)));
                if (userId.equals(mainUser)) {
                    resultSysMsgVo.setContent("您解绑了" + systemMessage.getSerialName() + "");
                } else {
                    resultSysMsgVo.setContent("主账号(" + systemMessage.getMap().get("mainUserPhone") + ")解除了您对" + systemMessage.getSerialName() +
                            "的绑定,请知悉。");
                }
                sysMsgVos.add(resultSysMsgVo);
            } else if (systemMessage.getType().equals(5) || systemMessage.getType().equals(6) || systemMessage.getType().equals(7)) {
                if (systemMessage.getType().equals(5)) {
                    resultSysMsgVo.setType(8);
                }
                resultSysMsgVo.setTitle(systemMessage.getSubtitle());
                resultSysMsgVo.setContent(systemMessage.getContent());
                resultSysMsgVo.setIconUrl(imgUrl.getImgUrl(imgUrl.getRel(RedisConstantUtil.ConfigItem.SYS_INFORM_DEFAULT_ICON_KEY,redisTemplate)));
                resultSysMsgVo.setId(String.valueOf(systemMessage.getId()));
                resultSysMsgVo.setExpand(systemMessage.getExtras());
                sysMsgVos.add(resultSysMsgVo);
            } else {
                throw new RuntimeException("没有找到匹配的类型");
            }


        }
        return sysMsgVos;
    }

    @GetMapping("msgListEnterprise")
    public Result msgListEnterprise(@RequestParam("currentPage") Integer currentPage, @RequestParam("pageSize") Integer pageSize) {
        Page<SystemMessage> page = new Page<SystemMessage>(currentPage, pageSize);
        Long userId = getUserId();
        IPage<SystemMessage> systemMessages = systemMessageService.page(page,
                new QueryWrapper<SystemMessage>().eq("user_id", userId).in("project_id",0,getProjectId()).orderByDesc("create_time"));
        List<ResultSysMsgVo> sysMsgVos = getSysMsgVos(systemMessages.getRecords(),userId);

        //将此人的所有未读消息标记为已读
        Map<String, Object> re = new HashMap<>();
        re.put("list", sysMsgVos);
        re.put("total", systemMessages.getTotal());
        SystemMessage systemMessage = SystemMessage.builder().isRead(1).build();
        systemMessageService.update(systemMessage, new UpdateWrapper<SystemMessage>().eq("user_id", userId).eq("is_read", 0));
        Result result = Result.getDefaultTrue();
        result.setData(re);
        return result;
    }
    /**
     * 根据用户ID查询系统未读消息总数
     */
    /**
     * 获取当前用户未读消息数量
     *
     * @return
     */
    @RequestMapping(value = "unReadNum", method = RequestMethod.GET)
    @Log(operateContent = "获取当前用户未读消息数量", operateModule = "消息服务")
    public Result unReadNum() {
        Result result = Result.getDefaultTrue();
        Integer count = null;
        count = systemMessageService.count(new QueryWrapper<SystemMessage>().eq("user_id", getUserId()).eq("is_read", 0).in("project_id",0,getProjectId()));
        if (null == count) {
            result.setData(0);
        } else {
            result.setData(count);
        }
        return result;
    }


    /**
     * 企业app首页动态信息轮播
     *
     * @return
     */
    @GetMapping("getNewsMsgEnterprise")
    public Result getNewsNoticeEnterprise() {
        //获取系统消息以及公告
        List<SystemMessage> systemMessages = personalMsgService.getNewsMsgEnterprise(getUserId(),getProjectId());
        //
        List<EnterpriseUserFaultMsgAlarm> enterpriseUserFaultMsgAlarms = enterpriseUserFaultMsgAlarmService.newsNotice(getUserId(),getProjectId());
        Set<CarouselVo> set = new HashSet();
        List<ResultSysMsgVo> sysMsgVos = getSysMsgVos(systemMessages, getUserId());
        // CarouselVo.type   0公告 1绑定请求 2解绑请求 3解绑子账户 4报警 5预警
        for (ResultSysMsgVo sysMsgVo : sysMsgVos) {
            CarouselVo carouselVo = new CarouselVo();
            carouselVo.setMsg(sysMsgVo.getContent());
            carouselVo.setTime(sysMsgVo.getTime());
            carouselVo.setType(sysMsgVo.getType());
            set.add(carouselVo);
        }
        for (EnterpriseUserFaultMsgAlarm enterpriseUserFaultMsgAlarm : enterpriseUserFaultMsgAlarms) {
            CarouselVo carouselVo = new CarouselVo();
            carouselVo.setMsg(enterpriseUserFaultMsgAlarm.getMsgContent());
            carouselVo.setTime(enterpriseUserFaultMsgAlarm.getCreateTime());
            // 0公告 1报警 2预警  5开关控制 6功率限定 7漏电自检
            carouselVo.setType(enterpriseUserFaultMsgAlarm.getType());
            set.add(carouselVo);
        }
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("list",set);
        resultMap.put("total",personalMsgService.getUnreadTotal(getUserId()));
        Result result = Result.getDefaultTrue();
        result.setData(resultMap);
        return result;

    }
}
