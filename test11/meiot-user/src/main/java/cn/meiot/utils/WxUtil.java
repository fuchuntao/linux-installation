package cn.meiot.utils;

import cn.meiot.entity.vo.AccessToken;
import cn.meiot.entity.vo.WxCodeAccessTokenVo;
import cn.meiot.entity.vo.WxUserInfoVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.locks.Lock;

@Component
@Slf4j
public class WxUtil {


    private static String ACCESSTOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    /**
     * 类型
     */
    public static final String GRANT_TYPE = "client_credential";

    public static final String TICKET_URL = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET";

    /**
     * 获取UNIONID_URL 链接
     */
    public static final String UNIONID_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID";

    public static final String CODE_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";


    /**
     * 通过openid查询微信信息
     */
    public static final String WX_INFO = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";


    /**
     * 通过openid查询微信信息(开放平台)
     */
    public static final String WX_INFO_OPEN = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";


    private RestTemplate restTemplate;

    private RedisUtil redisUtil;

//    @Value("${wx.appId}")
    private String appId;

//    @Value("${wx.appSecret}")
    private String appSecret;


//    @Value("${wx.dwdid}")
    private String dwdid;

//    @Value("${wx.dwdSecret}")
    private String dwdSecret;


//    @Value("${wx.dwdPhoneId}")
    private String dwdPhoneid;

//    @Value("${wx.dwdPhoneSecret}")
    private String dwdPhoneSecret;


    /**
     * accessToken有效时间
     */
    @Value("${wx.tokenExpire}")
    private long tokenExpire;


    WxUtil(RestTemplate restTemplate, RedisUtil redisUtil) {
        this.restTemplate = restTemplate;
        this.redisUtil = redisUtil;
    }


    /**
     * 获取accessToken
     *
     * @return
     */
    public String getAccessToken() {
        //获取读锁
        Lock readLock = ReadWriteLockUtil.readLock(ReadWriteLockUtil.ACCESSTOKENLOCK);
        //加锁
        readLock.lock();
        try {
            //缓存中获取accessToken
            String token = getAccessTokenByRedis();
            if (null != token) {
                log.info("在缓存中获取到了accessToken，直接返回");
                return token;
            }
        } finally {
            //释放读锁
            readLock.unlock();
        }
        //获取写锁
        Lock writeLock = ReadWriteLockUtil.writeLock(ReadWriteLockUtil.ACCESSTOKENLOCK);
        //加锁
        writeLock.lock();
        try {
            StringBuilder url = new StringBuilder(ACCESSTOKEN_URL);
            url.append("?");
            url.append("grant_type=" + GRANT_TYPE);
            url.append("&appid=" + appId);
            url.append("&secret=" + appSecret);
            log.info("获取accessToken的链接：{}", url.toString());
            String object = restTemplate.getForObject(url.toString(), String.class);
            AccessToken accessToken = new Gson().fromJson(object, AccessToken.class);
            log.info("accessToken:{}", accessToken.getAccess_token());
            //保存缓存
            redisUtil.saveStringValue(RedisConstantUtil.ACESS_TOKEN, accessToken.getAccess_token(), tokenExpire);
            return accessToken.getAccess_token();
        } finally {
            //释放写锁 一定要释放，否者其他线程获取不到写锁/读锁
            writeLock.unlock();
        }

    }


    /**
     * 在缓存中获取accessToken
     *
     * @return
     */
    private String getAccessTokenByRedis() {
        String accessToken = redisUtil.getValueByKey(RedisConstantUtil.ACESS_TOKEN);
        return accessToken;
    }

    /**
     * 通过code获取accessToken
     *
     * @param code
     * @return
     */
    public WxCodeAccessTokenVo getAccessTokenByCode(String code,String device) {

        String codeAccessTokenUrl = getCodeAccessTokenUrl(code,device);
        log.info("通过code获取accessToken的链接：{}", codeAccessTokenUrl);
        String object = restTemplate.getForObject(codeAccessTokenUrl, String.class);
        log.info("通过code获取accessToken结果：{}", object);
        JSONObject json = JSONObject.parseObject(object);
        if (json.containsKey("access_token")) {
            WxCodeAccessTokenVo wxCodeAccessTokenVo = new Gson().fromJson(object, WxCodeAccessTokenVo.class);
            log.info("accessToken:{}", json.getString("access_token"));
            return wxCodeAccessTokenVo;
        }
        return null;

    }

    private String getCodeAccessTokenUrl(String code, String device) {
        if("pc".equals(device)){
            return CODE_ACCESS_TOKEN_URL.replace("APPID", dwdid).replace("SECRET", dwdSecret).replace("CODE", code);
        }else{
            return CODE_ACCESS_TOKEN_URL.replace("APPID", dwdPhoneid).replace("SECRET", dwdPhoneSecret).replace("CODE", code);
        }
    }


    /**
     * 通过openid查询微信用户信息
     *
     * @param openid
     * @return
     */
    public WxUserInfoVo getWxUserInfoByopenId(String openid) {
        String accessToken = getAccessToken();
        return getWxUserByopenId(WX_INFO, openid, accessToken);
//        String accessToken = getAccessToken();
//        String wxUserInfoUrl = WX_INFO.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openid);
//        log.info("通过openid获取微信用户信息的链接：{}",wxUserInfoUrl);
//        String object = restTemplate.getForObject(wxUserInfoUrl, String.class);
//        JSONObject json = JSONObject.parseObject(object);
//        WxUserInfoVo wxUserInfoVo = new WxUserInfoVo();
//        if(json.containsKey("nickname")){
//            wxUserInfoVo.setNickname(json.getString("nickname"));
//        }
//        if(json.containsKey("headimgurl")){
//            wxUserInfoVo.setHeadimgurl(json.getString("headimgurl"));
//        }
//        return wxUserInfoVo;

    }

    /**
     * 通过openid获取用户信息
     *
     * @param openid
     * @param accessToken
     * @return
     */
    private WxUserInfoVo getWxUserByopenId(String url, String openid, String accessToken) {
        String wxUserInfoUrl = url.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openid);
        log.info("通过openid获取微信用户信息的链接：{}", wxUserInfoUrl);
        String object = restTemplate.getForObject(wxUserInfoUrl, String.class);
        JSONObject json = JSONObject.parseObject(object);
        log.info("查询用户信息结果：{}", json);
        WxUserInfoVo wxUserInfoVo = new WxUserInfoVo();
        wxUserInfoVo.setNickname(json.getString("nickname"));
        log.info("昵称：{}", wxUserInfoVo.getNickname());
        wxUserInfoVo.setHeadimgurl(json.getString("headimgurl"));
        log.info("微信头像：{}", wxUserInfoVo.getHeadimgurl());
        return wxUserInfoVo;
    }

    /**
     * 通过openid查询微信用户信息
     *
     * @param openid
     * @return
     */
    public WxUserInfoVo getWxUserInfoByopenId(String openid, String accessToken) {
        return getWxUserByopenId(WX_INFO_OPEN, openid, accessToken);

    }

    /**
     * 再一次获取accesstoken
     *
     * @return
     */
    public String getAccessTOkenAgain() {
        //第一步删除缓存中的token
        redisUtil.deleteString(RedisConstantUtil.ACESS_TOKEN);
        String accessToken = getAccessToken();
        return accessToken;
    }
}
