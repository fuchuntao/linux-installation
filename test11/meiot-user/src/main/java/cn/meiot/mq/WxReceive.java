package cn.meiot.mq;

import cn.meiot.entity.UserOpenid;
import cn.meiot.entity.UserUnionid;
import cn.meiot.entity.Wss;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.vo.WxStatusVo;
import cn.meiot.entity.vo.WxUserInfoDTO;
import cn.meiot.entity.vo.WxUserInfoVo;
import cn.meiot.exception.MyServiceException;
import cn.meiot.mapper.UserUnionidMapper;
import cn.meiot.service.IUserOpenidService;
import cn.meiot.service.IUserUnionidService;
import cn.meiot.utils.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class WxReceive {

    private IUserOpenidService userOpenidService;

    private RedisUtil redisUtil;

    private WxUtil wxUtil;

    private RestTemplate restTemplate;

    private UserUnionidMapper userUnionidMapper;

    private IUserUnionidService userUnionidService;

    private RabbitTemplate rabbitTemplate;

    private RedisTemplate redisTemplate;

    WxReceive(IUserOpenidService userOpenidService, RedisUtil redisUtil, WxUtil wxUtil, RestTemplate restTemplate, UserUnionidMapper userUnionidMapper,
              RabbitTemplate rabbitTemplate, RedisTemplate redisTemplate,
              IUserUnionidService userUnionidService) {
        this.redisUtil = redisUtil;
        this.userOpenidService = userOpenidService;
        this.wxUtil = wxUtil;
        this.restTemplate = restTemplate;
        this.userUnionidMapper = userUnionidMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.redisTemplate = redisTemplate;
        this.userUnionidService = userUnionidService;
    }

    /**
     * 保存用户openid
     *
     * @param content
     */
    @RabbitListener(queues = QueueConstantUtil.SAVE_USER_OPENID)
    public void saveUserOpenId(String content) {
        WxStatusVo wxStatusVo = new WxStatusVo();
        wxStatusVo.setStatus(200);
        if (null == content) {
            log.info("入参为空");
            return;
        }
        log.info("添加用户openid，参数：{}", content);
        WxUserInfoDTO wxUserInfoDTO = new Gson().fromJson(content, WxUserInfoDTO.class);
        if (0 == wxUserInfoDTO.getUserId() || null == wxUserInfoDTO.getOpenId()) {
            log.info("参数不正确");
            return;
        }
        long userId = wxUserInfoDTO.getUserId();
        String myUnionid = getUnionid(wxUserInfoDTO.getOpenId());
        String msg = "";

        //绑定公众号
        UserUnionid u = new UserUnionid();
        u.setUId(userId);
        //判断此微信是否绑定了其他的账号
        UserUnionid openid = userUnionidMapper.selectOne(new QueryWrapper<UserUnionid>().lambda().eq(UserUnionid::getOpenid, wxUserInfoDTO.getOpenId()).eq(UserUnionid::getDeleted, 0));
        if (openid != null && !openid.getUId().equals(wxUserInfoDTO.getUserId())) {
            log.info("此微信已经和其他账号绑定关注了公众号！");
            wxStatusVo.setMsg("此微信已经和其他账号绑定关注了公众号！");
            wxStatusVo.setStatus(500);
            pushStatus(wxStatusVo,userId);
            return ;
        }
        //判断当前账号是否被绑定过

        UserUnionid userUnionid = userUnionidMapper.selectOne(new QueryWrapper<UserUnionid>().lambda().eq(UserUnionid::getUId, userId).eq(UserUnionid::getDeleted, 0));
        if (userUnionid == null) {
            //没有被绑定
            WxUserInfoVo wxUserInfoVo = wxUtil.getWxUserInfoByopenId(wxUserInfoDTO.getOpenId());
            u.setNickName(wxUserInfoVo.getNickname());
            u.setHeadImgurl(wxUserInfoVo.getHeadimgurl());
            u.setCreateTime(ConstantsUtil.DF.format(new Date()));
            u.setOpenid(wxUserInfoDTO.getOpenId());
            u.setDeleted(0);
            userUnionidMapper.insert(u);
            userUnionid = userUnionidMapper.selectOne(new QueryWrapper<UserUnionid>().lambda().eq(UserUnionid::getUId, userId).eq(UserUnionid::getDeleted, 0));
        } else {
            if (userUnionid.getOpenid() == null) {
                userUnionidService.update(new UpdateWrapper<UserUnionid>().lambda().set(UserUnionid::getOpenid, wxUserInfoDTO.getOpenId())
                        .eq(UserUnionid::getUId, userUnionid.getUId()).eq(UserUnionid::getDeleted, 0));

            }else if(!userUnionid.getOpenid().equals(wxUserInfoDTO.getOpenId())){
                log.info("此账号已被其他微信绑定");
                pushStatus(wxStatusVo,userId);
                wxStatusVo.setStatus(500);
                return ;
            }
        }
        //判断此微信是否绑定了其他账号的登录功能
        UserUnionid unionid = userUnionidMapper.selectOne(new QueryWrapper<UserUnionid>().lambda().eq(UserUnionid::getUnionid, wxUserInfoDTO.getUnionid()).eq(UserUnionid::getDeleted, 0));
        if (unionid != null && !unionid.getUId().equals(wxUserInfoDTO.getUserId())) {
            log.info("此微信已经绑定其他账号登录功能");
            wxStatusVo.setMsg("此微信已经绑定其他账号登录功能");
            wxStatusVo.setStatus(500);
            pushStatus(wxStatusVo,userId);
            return ;
        }
        if(null == userUnionid.getUnionid() ){
            userUnionidService.update(new UpdateWrapper<UserUnionid>().lambda().set(UserUnionid::getUnionid, myUnionid)
                    .eq(UserUnionid::getUId, userUnionid.getUId()).eq(UserUnionid::getDeleted, 0));
        }else if(!userUnionid.getUnionid().equals(myUnionid)){
            log.info("此账号已被其他微信绑定");
            pushStatus(wxStatusVo,userId);
            wxStatusVo.setStatus(500);
            return ;
        }
        pushStatus(wxStatusVo,userId);
        System.out.println("最终结果：" + wxStatusVo);
    }

    @Deprecated
    private Integer bindWXLogin(WxUserInfoDTO wxUserInfoDTO, WxStatusVo wxStatusVo) {
        String unionid = getUnionid(wxUserInfoDTO.getOpenId());
        if (unionid == null) {
            log.info("没有找到用户的唯一标识------------");
            return 500;
        }
        Integer count = userUnionidMapper.selectCount(new QueryWrapper<UserUnionid>().lambda().eq(UserUnionid::getUId, wxUserInfoDTO.getUserId()).eq(UserUnionid::getDeleted, 0));
        WxUserInfoVo wxUserInfoVo = wxUtil.getWxUserInfoByopenId(wxUserInfoDTO.getOpenId());
        UserUnionid userUnionid = new UserUnionid();
        userUnionid.setNickName(wxUserInfoVo.getNickname());
        userUnionid.setHeadImgurl(wxUserInfoVo.getHeadimgurl());
        userUnionid.setUId(wxUserInfoDTO.getUserId());
        userUnionid.setUnionid(unionid);
        userUnionid.setDeleted(0);
        if (count == 0) {
            Integer num = userUnionidMapper.selectCount(new QueryWrapper<UserUnionid>().lambda().eq(UserUnionid::getUnionid, wxUserInfoDTO.getUnionid()).eq(UserUnionid::getDeleted, 0));
            if (num > 0) {
                log.info("此微信已经绑定过其他账号");
                wxStatusVo.setStatus(500);
                wxStatusVo.setMsg("此微信已经绑定过其他账号");
                return 500;
            } else {
                userUnionid.setCreateTime(ConstantsUtil.DF.format(new Date()));
                userUnionidMapper.insert(userUnionid);
                wxStatusVo.setMsg(null);
                return 200;
            }
        } else {
            //userUnionidMapper.insert(userUnionid);
            wxStatusVo.setStatus(500);
            wxStatusVo.setMsg("此账号已被其他微信绑定");
            return 500;
            // userUnionidMapper.update(userUnionid,new UpdateWrapper<UserUnionid>().lambda().eq(UserUnionid::getUId,wxUserInfoDTO.getUserId()));
        }
    }


    /**
     * 取消订阅删除用户openid
     *
     * @param content
     */
    @RabbitListener(queues = QueueConstantUtil.DELETE_USER_OPENID)
    public void deleteUserOpenId(String content) {
        if (null == content) {
            log.info("入参为空");
            return;
        }
        log.info("删除用户openid，参数：{}", content);
        WxUserInfoDTO wxUserInfoDTO = new Gson().fromJson(content, WxUserInfoDTO.class);
        if (null == wxUserInfoDTO.getOpenId()) {
            log.info("参数不正确");
            return;
        }
        UserUnionid userUnionid = userUnionidMapper.selectOne(new QueryWrapper<UserUnionid>().lambda().eq(UserUnionid::getOpenid, wxUserInfoDTO.getOpenId()).eq(UserUnionid::getDeleted, 0));
        if (null != userUnionid) {
            userUnionidService.update(new UpdateWrapper<UserUnionid>().lambda()
            .set(UserUnionid::getOpenid,null).eq(UserUnionid::getOpenid,userUnionid.getOpenid()).eq(UserUnionid::getDeleted,0));
            redisUtil.deleteHashKey(RedisConstantUtil.USER_OPENID, userUnionid.getUId().toString());
        }
    }


    private void pushStatus(WxStatusVo wxStatusVo, Long userId) {
        //获取token
        String token = getToken(userId);
        if (null != token) {
            rabbitTemplate.convertAndSend(QueueConstantUtil.WSS_CMD_21, QueueConstantUtil.WSS_KEY, JSONObject.toJSONString(new Wss(ConstantsUtil.SocketType.BIND_WX, token, wxStatusVo)));
        }
    }

    private String getToken(Long userId) {
        Object object = redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN + "pc" + "_" + userId);
        if (null == object) {
            object = redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN + "phone" + "_" + userId);
        }
        AuthUserBo authUserBo = null;
        try {
            authUserBo = new Gson().fromJson(object.toString(), AuthUserBo.class);
        } catch (Exception e) {
            log.info("获取token失败");
            return null;

        }
        return authUserBo.getToken();

    }

    /**
     * 删除绑定登录的信息
     *
     * @param unionid
     */
    private void deleteUserUnionid(String unionid) {
        log.info("删除用户基础信息");
        Integer count = userUnionidMapper.selectCount(new QueryWrapper<UserUnionid>().lambda().eq(UserUnionid::getUnionid, unionid).eq(UserUnionid::getDeleted, 0));
        if (count > 0) {
            UserUnionid userUnionid = new UserUnionid();
            userUnionid.setDeleted(1);
            userUnionidMapper.update(userUnionid, new UpdateWrapper<UserUnionid>().lambda().eq(UserUnionid::getUnionid, unionid).eq(UserUnionid::getDeleted, 0));
        }

    }


    /**
     * 获取用户基础信息
     *
     * @param openId
     * @return
     */
    public String getUnionid(String openId) {
        String accessToken = wxUtil.getAccessToken();
        String unionid_url = wxUtil.UNIONID_URL.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
        log.info("获取用户信息的url：{}", unionid_url);
        String object = restTemplate.getForObject(unionid_url, String.class);
        log.info("获取微信用户信息返回结果：{}", object);
        JSONObject json = JSON.parseObject(object);
        String unionid = (String) json.get("unionid");
        log.info("openid：{}，unionid：{}", openId, unionid);
        return unionid;

    }
}
