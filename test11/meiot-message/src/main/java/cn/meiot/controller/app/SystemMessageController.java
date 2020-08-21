package cn.meiot.controller.app;


import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.SystemMessage;
import cn.meiot.entity.bo.UserInfoBo;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.ISystemMessageService;
import cn.meiot.utils.RedisConstantUtil;
import cn.meiot.utils.UserInfoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统消息 前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-12
 */
@RestController
@RequestMapping("/app/sys-msg")
public class SystemMessageController extends BaseController {

    private ISystemMessageService systemMessageService;

    @Autowired
    private UserInfoUtil userInfoUtil;

    @Autowired
    private RedisTemplate redisTemplate;



    public SystemMessageController(ISystemMessageService systemMessageService) {
        this.systemMessageService = systemMessageService;
    }

    /**
     * 系统消息列表
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    @Log(operateContent = "系统消息列表", operateModule = "消息服务")
    public Result list(@RequestParam("currentPage") Integer currentPage, @RequestParam("pageSize") Integer pageSize) {
        Page<SystemMessage> page = new Page<SystemMessage>(currentPage, pageSize);
        Long userId = getUserId();
        IPage<SystemMessage> systemMessages = systemMessageService.page(page,
                new QueryWrapper<SystemMessage>().eq("user_id", userId).orderByDesc("create_time"));
        for (SystemMessage systemMessage : systemMessages.getRecords()) {
            if (!StringUtils.isEmpty(systemMessage.getExtras())) {
                Map<String, Object> extras = (Map<String, Object>) JSON.parse(systemMessage.getExtras());
                //Map<String,Object> extras = json.fromJson(systemMessage.getExtras(), Map.class);
                systemMessage.setMap(extras);
            }
        }
        //将此人的所有未读消息标记为已读
        SystemMessage systemMessage = SystemMessage.builder().isRead(1).build();
        systemMessageService.update(systemMessage, new UpdateWrapper<SystemMessage>().eq("user_id", userId).eq("is_read", 0));
        Result result = Result.getDefaultTrue();
        result.setData(systemMessages);
        return result;

    }

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
        count = systemMessageService.count(new QueryWrapper<SystemMessage>().eq("user_id", getUserId()).eq("is_read", 0));
        if (null == count) {
            result.setData(0);
        } else {
            result.setData(count);
        }
        return result;
    }

}
