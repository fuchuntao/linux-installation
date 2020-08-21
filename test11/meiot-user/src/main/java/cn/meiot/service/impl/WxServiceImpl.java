package cn.meiot.service.impl;

import cn.meiot.entity.SysUser;
import cn.meiot.entity.UserOpenid;
import cn.meiot.entity.UserUnionid;
import cn.meiot.entity.bo.TicketBo;
import cn.meiot.entity.vo.*;
import cn.meiot.exception.MyServiceException;
import cn.meiot.mapper.UserOpenidMapper;
import cn.meiot.mapper.UserUnionidMapper;
import cn.meiot.mq.WxReceive;
import cn.meiot.service.ILoginService;
import cn.meiot.service.ISysUserService;
import cn.meiot.service.WxService;
import cn.meiot.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

@Service
@Slf4j
public class WxServiceImpl implements WxService {

    private RestTemplate restTemplate;

    private RedisUtil redisUtil;

    private WxUtil wxUtil;

    private UserOpenidMapper userOpenidMapper;

    private ILoginService loginService;

    private UserUnionidMapper userUnionidMapper;

    private ISysUserService userService;

    private WxReceive wxReceive;

    private static final long EXPIRE_TIME = 604800;

    private static String TICKET_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";

    /**
     * ticket有效时间
     */
    @Value("${wx.ticketExpire}")
    private long ticketExpire;


    WxServiceImpl(RestTemplate restTemplate, RedisUtil redisUtil, WxUtil wxUtil, UserOpenidMapper userOpenidMapper,
                  ILoginService loginService, UserUnionidMapper userUnionidMapper, ISysUserService userService,
                  WxReceive wxReceive) {
        this.restTemplate = restTemplate;
        this.redisUtil = redisUtil;
        this.wxUtil = wxUtil;
        this.userOpenidMapper = userOpenidMapper;
        this.loginService = loginService;
        this.userUnionidMapper = userUnionidMapper;
        this.userService = userService;
        this.wxReceive = wxReceive;
    }


    private WxQRCodeVo setData(Long userId) {
        Scene wxScene = new Scene();
        wxScene.setScene_str(userId.toString());
        WxactionInfo wxactionInfo = new WxactionInfo();
        wxactionInfo.setScene(wxScene);
        WxQRCodeVo wxQRCodeVo = new WxQRCodeVo();
        wxQRCodeVo.setAction_name("QR_STR_SCENE");
        wxQRCodeVo.setExpire_seconds(EXPIRE_TIME);
        wxQRCodeVo.setWxactionInfo(wxactionInfo);
        return wxQRCodeVo;
    }


    @Override
    public Result getTicket(Long userId) {
        log.info("获取ticket开始===========>");
        Result result = Result.getDefaultTrue();
        //获取读锁
        Lock readLock = ReadWriteLockUtil.readLock(ReadWriteLockUtil.qrCodeTicketLock);
        readLock.lock();
        try {
            String ticket = getTicketByRedis(userId);
            if (ticket != null) {
                log.info("在缓存中获取到了ticket信息，直接返回");
                result.setData(WxUtil.TICKET_URL.replace("TICKET", ticket));
                return result;
            }
        } finally {
            readLock.unlock();
        }
        Lock writeLock = ReadWriteLockUtil.writeLock(ReadWriteLockUtil.qrCodeTicketLock);
        writeLock.lock();
        try {
            WxQRCodeVo wxQRCodeVo = setData(userId);
            String accessToken = wxUtil.getAccessToken();

            String ticket = getTicket(accessToken, wxQRCodeVo);
            if(StringUtils.isEmpty(ticket)){
                //重新获取accesstoken
                accessToken =  wxUtil.getAccessTOkenAgain();
                ticket = getTicket(accessToken, wxQRCodeVo);
            }
            redisUtil.saveStringValue(RedisConstantUtil.QRCODE_TICKET + userId, ticket, ticketExpire);
            result.setData(WxUtil.TICKET_URL.replace("TICKET", ticket));
            return result;

//            log.info("accessToken:{}",accessToken);
//            String ticket_url = TICKET_URL + accessToken;
//            String param = new Gson().toJson(wxQRCodeVo);
//            log.info("获取ticket的链接：{}", TICKET_URL);
//            param = param.replace("wxactionInfo", "action_info");
//            log.info("获取ticket的参数：{}", param);
//            ResponseEntity<String> entity = restTemplate.postForEntity(ticket_url, param, String.class);
//            String body = entity.getBody();
//            log.info("获取ticket结果：{}", body);
//            TicketBo ticketBo = new Gson().fromJson(body, TicketBo.class);
//            String ticket = ticketBo.getTicket();
//            log.info("ticket：{}", ticket);
//            result.setData(WxUtil.TICKET_URL.replace("TICKET", ticket));
//            redisUtil.saveStringValue(RedisConstantUtil.QRCODE_TICKET + userId, ticket, ticketExpire);
//            return result;
        } finally {
            writeLock.unlock();
        }
    }


    private  String getTicket(String accessToken,WxQRCodeVo wxQRCodeVo){
        String ticket_url = TICKET_URL + accessToken;
        String param = new Gson().toJson(wxQRCodeVo);
        log.info("获取ticket的链接：{}", TICKET_URL);
        param = param.replace("wxactionInfo", "action_info");
        log.info("获取ticket的参数：{}", param);
        ResponseEntity<String> entity = restTemplate.postForEntity(ticket_url, param, String.class);
        String body = entity.getBody();
        log.info("获取ticket结果：{}", body);
        TicketBo ticketBo = new Gson().fromJson(body, TicketBo.class);
        return  ticketBo.getTicket();
    }

    @Override
    public Result qrCodeLogin(String code,String device) {
        //通过code获取accesstoken
        WxCodeAccessTokenVo wxCodeAccessTokenVo = wxUtil.getAccessTokenByCode(code,device);
        if (null == wxCodeAccessTokenVo) {
            return new Result().Faild(ErrorCodeUtil.LOGIN_ERROR);
        }
        log.info("通过");
        //判断是否是合法用户
        // UserOpenid userOpenid = userOpenidMapper.selectOne(new QueryWrapper<UserOpenid>().lambda().eq(UserOpenid::getUnionid, wxCodeAccessTokenVo.getUnionid()).eq(UserOpenid::getDeleted, 0));

        UserUnionid userUnionid = userUnionidMapper.selectOne(new QueryWrapper<UserUnionid>().lambda()
                .eq(UserUnionid::getUnionid, wxCodeAccessTokenVo.getUnionid())
                .eq(UserUnionid::getDeleted, 0));
        Map<String, Object> map = new HashMap<String, Object>();
        if (null == userUnionid) {
            redisUtil.saveStringValue(RedisConstantUtil.WX_CODE_UNIONID + "_" + code, wxCodeAccessTokenVo.getUnionid(), 3600l);
            //通过openid查询用户信息
            WxUserInfoVo userInfoVo = wxUtil.getWxUserInfoByopenId(wxCodeAccessTokenVo.getOpenid(),wxCodeAccessTokenVo.getAccess_token());
            redisUtil.saveStringValue(RedisConstantUtil.WX_USER_INFO + "_" + wxCodeAccessTokenVo.getUnionid(), new Gson().toJson(userInfoVo), 3600l);
            Result result = Result.getDefaultTrue();
            result.setMsg(ErrorCodeUtil.BIND_ACCOUNT_PLEASE);
            map.put("resultStatus", -1);
            map.put("successData", null);
            result.setData(map);
            return result;
        }
        //登录处理
        Result result = loginService.wxQrCodeLogin(userUnionid.getUId(),device);
        map.put("resultStatus", 0);
        map.put("successData", result.getData());
        result.setData(map);
        return result;
    }

    @Override
    public Result bindingWx(UserWXBindVo userWXBindVo,String device) {
        String unionid = redisUtil.getValueByKey(RedisConstantUtil.WX_CODE_UNIONID + "_" + userWXBindVo.getWxCode());

        if (null == unionid ) {
            WxCodeAccessTokenVo wxCodeAccessTokenVo = wxUtil.getAccessTokenByCode(userWXBindVo.getWxCode(),device);
            if (null == wxCodeAccessTokenVo) {
                return new Result().Faild(ErrorCodeUtil.BIND_ERROR);
            }
            unionid = wxCodeAccessTokenVo.getUnionid();
        }

        WxUserInfoVo wxUserInfoVo = null;
        Object wxUserInfo = redisUtil.getValueByKey(RedisConstantUtil.WX_USER_INFO + "_" + unionid);
        if(wxUserInfo !=null){
            wxUserInfoVo = new Gson().fromJson(wxUserInfo.toString(),WxUserInfoVo.class);
        }

        Result result = userService.chechPassword(userWXBindVo.getAccount(), userWXBindVo.getPassword());
        if (!result.isResult()) {
            return result;
        }
        //判断是否存在此用户
        //绑定
        SysUser user = (SysUser) result.getData();
        UserUnionid userUnionid = new UserUnionid();
        UserUnionid unionidi = userUnionidMapper.selectOne(new QueryWrapper<UserUnionid>().lambda().eq(UserUnionid::getUnionid, unionid).eq(UserUnionid::getDeleted, 0));
        if(null != unionidi){
            return new Result().Faild(ErrorCodeUtil.PLEASE_LOGIN_DIRECTLY);
        }else{
            UserUnionid selectOne = userUnionidMapper.selectOne(new QueryWrapper<UserUnionid>().lambda().eq(UserUnionid::getUId, user.getId()).eq(UserUnionid::getDeleted, 0));
            if(null == selectOne){
                //通过openid获取用户信息
                userUnionid.setNickName(wxUserInfoVo.getNickname());
                userUnionid.setHeadImgurl(wxUserInfoVo.getHeadimgurl());
                userUnionid.setUId(user.getId());
                userUnionid.setUnionid(unionid);
                userUnionid.setCreateTime(ConstantsUtil.DF.format(new Date()));
                userUnionid.setDeleted(0);
                userUnionidMapper.insert(userUnionid);
                return Result.getDefaultTrue();
            }else if(!selectOne.getUnionid().equals(unionid)){
                return new Result().Faild(ErrorCodeUtil.ACCOUNT_BIND_OTHER_WX);
            }
            return Result.getDefaultTrue();

        }
//        Integer count = userUnionidMapper.selectCount(new QueryWrapper<UserUnionid>().lambda().eq(UserUnionid::getUnionid, unionid).eq(UserUnionid::getDeleted, 0));
//        if (count > 0) {
//            return new Result().Faild("此微信已绑定账号，请直接扫码登录！");
//        }
//        UserUnionid userUnionid = userUnionidMapper.selectOne(new QueryWrapper<UserUnionid>().lambda().eq(UserUnionid::getUId, user.getId()).eq(UserUnionid::getDeleted, 0));
//        if (userUnionid == null) {
//            UserUnionid u = new UserUnionid();
//            u.setUId(user.getId());
//            u.setUnionid(unionid);
//            u.setCreateTime(ConstantsUtil.DF.format(new Date()));
//            u.setDeleted(0);
//            int insert = userUnionidMapper.insert(u);
//            if (insert == 0) {
//                return new Result().Faild("绑定失败！");
//            }
//            redisUtil.deleteString(RedisConstantUtil.WX_CODE_UNIONID + "_" + userWXBindVo.getWxCode());
//            return loginService.wxQrCodeLogin(user.getId());
//        }
//
//        if (userUnionid.getUnionid().equals(unionid)) {
//            return new Result().Faild("无需重复绑定！");
//        } else {
//            return new Result().Faild("此账号已被其他微信绑定！");
//        }

    }

    @Override
    public Result unBindWx(Long userId) {
        UserUnionid userUnionid = userUnionidMapper.selectOne(new QueryWrapper<UserUnionid>().lambda().eq(UserUnionid::getUId, userId).eq(UserUnionid::getDeleted, 0));
        if(null != userUnionid){
            userUnionid.setDeleted(1);
            userUnionidMapper.updateById(userUnionid);
            redisUtil.deleteHashKey(RedisConstantUtil.USER_OPENID, userUnionid.getUId().toString());
        }
        return Result.getDefaultTrue();

        //解绑微信通知
//        UserOpenid userOpenid = userOpenidMapper.selectOne(new QueryWrapper<UserOpenid>().lambda().eq(UserOpenid::getUserId, userId).eq(UserOpenid::getDeleted, 0));
//        if (null == userOpenid) {
//            return Result.getDefaultTrue();
//        }
//        userOpenid.setDeleted(1);
//        int update = userOpenidMapper.updateById(userOpenid);
//        if (update == 1) {
//            //删除缓存中的数据
//            redisUtil.deleteHashKey(RedisConstantUtil.USER_OPENID, userOpenid.getUserId().toString());
//        }else{
//            throw new MyServiceException("解绑失败", "解绑失败");
//        }
//        unWxLogin(userId);
//        return Result.getDefaultTrue();
    }

    /**
     * 解绑微信登录
     *
     * @param userId
     * @return
     */
    @Deprecated
    private void unWxLogin(Long userId) {
        UserUnionid userUnionid = userUnionidMapper.selectOne(new QueryWrapper<UserUnionid>().lambda().eq(UserUnionid::getUId, userId).eq(UserUnionid::getDeleted, 0));
        if (null == userUnionid) {
            //throw new MyServiceException("解绑失败","解绑失败");
            return;
        }
        userUnionid.setDeleted(1);
        int update = userUnionidMapper.updateById(userUnionid);
        if (update == 0) {
            throw new MyServiceException(ErrorCodeUtil.UNBIND_ERROR);
        }

    }

    /**
     * 在缓存中获取accessToken
     *
     * @return
     */
    private String getTicketByRedis(Long userId) {
        return redisUtil.getValueByKey(RedisConstantUtil.QRCODE_TICKET + userId);
    }


}
