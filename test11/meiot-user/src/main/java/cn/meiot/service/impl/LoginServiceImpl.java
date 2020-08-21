package cn.meiot.service.impl;

import cn.meiot.dao.LoginRedisDao;
import cn.meiot.entity.*;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.bo.LoginUserBo;
import cn.meiot.entity.bo.UserInfoBo;
import cn.meiot.entity.vo.*;
import cn.meiot.enums.AccountType;
import cn.meiot.enums.SmsType;
import cn.meiot.exception.MyServiceException;
import cn.meiot.feign.SmsFeign;
import cn.meiot.jg.JPushClientExample;
import cn.meiot.jg.MyJPushClient;
import cn.meiot.mapper.*;
import cn.meiot.service.*;
import cn.meiot.service.pc.IProjectService;
import cn.meiot.utils.*;
import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class LoginServiceImpl implements ILoginService {

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private SysUserProfileMapper sysUserProfileMapper;

    @Autowired
    private LoginRedisDao loginRedisDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SmsFeign smsFeign;

    @Autowired
    private ISysMenuService sysMenuService;

    @Autowired
    private MyJPushClient myJPushClient;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private LoginUtil loginUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IUserProjectService userProjectService;

    @Autowired
    private ISysPermissionService sysPermissionService;

    @Autowired
    private ConfigMapper configMapper;

    /**
     * 保存token信息
     * @param user
     * @param device
     * @return
     */
    private AuthUserBo saveTokenInfo(SysUser user,String device){
        String secretKey = EncryptUtil.createKey();
        log.info("此用户的密钥为：{}",secretKey);
        //加密
        String token = EncryptUtil.encryptAES(String.valueOf(user.getId()),secretKey );
        log.info("生成的token：{}",token);
        log.info("redis存储的用户信息，{}",user);
        //将用户id进行加密
        AuthUserBo authUserBo = AuthUserBo.builder().token(token).device(device).build();
        //返回用户信息
        UserInfoBo userInfoBo = user.convertToUserBo();
        log.info("需要展示给前端的用户数据：{}",userInfoBo);
        authUserBo.setUser(userInfoBo);
        //将用户的角色id保存到缓存中
        List<Integer> list = sysUserRoleMapper.selectRoleByUserId(user.getId());
        //保存角色信息
        redisTemplate.opsForValue().set(RedisConstantUtil.USER_ROLES+user.getId(),list,ConstantsUtil.OTHER_TOKEN_EXPIRE_TIME,ConstantsUtil.EXPIRE_TYPE);
        if(AccountType.ENTERPRISE.value().equals(user.getType())){
            //获取当前用户的项目id
            List<Integer> ids = userProjectService.getProjectIdByUser(user,list);
            authUserBo.setProjectIds(ids);
            //获取默认项目的权限
            if(ids != null && ids.size() > 0){
                sysPermissionService.queryEnPermissionIds(user,list,ids.get(0));
                authUserBo.setDefaultProjectId(ids.get(0));
            }

        }else if(AccountType.PLATFORM.value().equals(user.getType())){
            sysPermissionService.setRunPlatformPermission(user,list);
        }

        //将用户信息保存到redis中
        loginRedisDao.saveToken(authUserBo,secretKey);

        //修改登录时间
        sysUserService.update(new UpdateWrapper<SysUser>().set("login_time",ConstantsUtil.DF.format(new Date())).eq("id",user.getId()));
        return authUserBo;
    }

    @Override
    public Result login(Login login) {
        Result result = Result.getDefaultFalse();
        //加密前端传过来的密码
        result = loginCheck(login,AccountType.PERSONAGE.value());
        if(!result.isResult()){
            return result;
        }
        AuthUserBo authUserBo = (AuthUserBo) result.getData();
        //清楚极光tag
        myJPushClient.deleteTag(authUserBo.getUser().getId(),AccountType.PERSONAGE.value());
        log.info("登录成功");
        result = Result.getDefaultTrue();
        LoginUserBo loginUserBo = copyToLoginUser(authUserBo);
        result.setData(loginUserBo);
        return result;
    }

    @Override
    public Result forgetPwd(ForgetPwdVo forgetPwdVo) {
        Result result = Result.getDefaultFalse();
        //查询手机号码是否正确
        SysUser sysUser = sysUserService.getOne(new QueryWrapper<SysUser>().eq("user_name",forgetPwdVo.getAccount())
                .eq("type",forgetPwdVo.getType()).eq("deleted",0).last("limit 1"));
        if(null == sysUser || !sysUser.getUserName().equals(forgetPwdVo.getAccount())){
            result.setMsg(ErrorCodeUtil.PHONE_IS_NOT_REGISTER);
            return result;
        }
        //获取短信验证码
        SmsVo smsVo = new SmsVo();
        smsVo.setAccount(forgetPwdVo.getAccount());
        smsVo.setSmsType(SmsType.FORGETPWD.value());
        Result smsResult = smsFeign.getSms(smsVo);
        log.info("返回结果：{}",smsResult);
        Object code = smsResult.getData();
        if(null == smsResult || null == code ){
            result.setMsg(ErrorCodeUtil.PLEASE_PULL_SMS_CODE);
            return result;
        }
        //校验验证码
        if(!code.toString().equals(forgetPwdVo.getCode())){
            result.setMsg(ErrorCodeUtil.CODE_ERROR);
            return result;
        }
        //获取新的密码盐
        String salt = RandomUtil.getStr(ConstantUtil.SALT_LENGTH);
        //将新的密码进行加密
        String newPwd = Md5.md5(forgetPwdVo.getNewPwd(),salt);
        //修改数据库
        sysUser.setPassword(newPwd);
        sysUser.setSalt(salt);
        sysUser.setUpdateTime(DateUtil.getNowDate());
        Boolean flag = sysUserService.update(sysUser,new UpdateWrapper<SysUser>().eq("id",sysUser.getId()));
        if(flag){
            //使验证码失效
            rabbitTemplate.convertAndSend("delCode",smsVo);
            //删除当前登陆token信息
            //redisUtil.deleteValueByKey(RedisConstantUtil.USER_TOKEN+sysUser.getId());
            //log.info("删除");
            //redisTemplate.opsForHash().delete(RedisConstantUtil.USER_TOKEN+sysUser.getId());
            result = Result.getDefaultTrue();
            return result;
        }
        result.setMsg(ErrorCodeUtil.UPDATE_FAIL);
        return result;
    }

    @Override
    @Transactional
    public Result register(RegisterVo registerVo) {
        log.info("注册参数：{}",registerVo);
        Result result = Result.getDefaultFalse();
        if(!VerifyUtil.verifyPhone(registerVo.getAccount())){
            //result.setMsg("手机账号不符合要求");
            result.setMsg(ErrorCodeUtil.PHONE_FORMAT_ERROR);
            return result;
        }
        //获取短信验证码
        SmsVo smsVo = new SmsVo();
        smsVo.setAccount(registerVo.getAccount());
        smsVo.setSmsType(SmsType.REGISTER.value());
        Result sms = smsFeign.getSms(smsVo);
        log.info("获取验证码结果：{}",sms);
        if(!sms.isResult() || null == sms.getData()){
            //result.setMsg("请获取验证码");
            result.setMsg(ErrorCodeUtil.PLEASE_PULL_SMS_CODE);
            return result;
        }
        String code = (String) sms.getData();
        if(!registerVo.getCode().equals(code)){
            //result.setMsg("验证码不正确");
            result.setMsg(ErrorCodeUtil.CODE_ERROR);
            return result;
        }

        //查询手机号码是否存在
        Integer count = sysUserService.count(new QueryWrapper<SysUser>().eq("user_name", registerVo.getAccount()).eq("type",AccountType.PERSONAGE.value()));
        if(null != count && count > 0){
            result.setMsg(ErrorCodeUtil.ACCOUNT_IS_EXIST);
            result.setMsg(ErrorCodeUtil.ACCOUNT_EXIST);
            return result;
        }
        //获取新的密码盐
        String salt = RandomUtil.getStr(ConstantUtil.SALT_LENGTH);
        log.info("密码盐：{}",salt);
        //将新的密码进行加密
        String newPwd = Md5.md5(registerVo.getNewPwd(),salt);
        //新增账户
        SysUser sysUser = SysUser.builder().userName(registerVo.getAccount())
                .password(newPwd).salt(salt).type(AccountType.PERSONAGE.value())
                .status(ConstantsUtil.NORMAL_ROLE_STATUS).build();
        //保存基础信息
        boolean save = sysUserService.save(sysUser);
        //新增一条扩展记录
        SysUserProfile sysUserProfile = SysUserProfile.builder().userId(sysUser.getId()).build();
        //设置默认头像
//        Config config = configMapper.selectOne(new QueryWrapper<Config>().lambda().eq(Config::getCKey, ConstantsUtil.userManager.USER_DEFAULT_HEAD_PORTRAIT));
//        if(null != config){
//            sysUserProfile.setHeadPortrait(config.getValue());
//        }
        int insert = sysUserProfileMapper.insert(sysUserProfile);
        if(save && insert == 1){
            //app注册人数+1
            PcUserStatisticsVo pcUserStatisticsVo = PcUserStatisticsVo.builder().userType(AccountType.PERSONAGE.value())
                    .type(1).date(ConstantsUtil.DF.format(new Date())).build();
            rabbitTemplate.convertAndSend(QueueConstantUtil.MODIFY_USER_NOTIFICATION,pcUserStatisticsVo);
            return Result.getDefaultTrue();
        }
        throw  new MyServiceException(ErrorCodeUtil.FAIL_TO_REGISTER);
    }

    @Override
    public Result logout(Long userId,String device) {
        //清空用户的缓存
        loginRedisDao.clearLoginInfo(userId);
        SysUser user = sysUserService.getById(userId);
        SaveLogVo saveLogVo = SaveLogVo.builder().userId(user.getId()).content("用户注销")
                .username(user.getUserName())
                .name(user.getNickName() == null ? "-" : user.getNickName())
                .useragent(device)
                .userType(user.getType())
                .mainUserId(user.getBelongId() == 0 ? user.getId() : user.getBelongId())
                .status(1)
                .build();
        loginUtil.saveLoginLog(saveLogVo);
        //清楚极光tag
        myJPushClient.deleteTag(user.getId(),user.getType());
        log.info("清除token信息成功");
        return Result.getDefaultTrue();
    }

    @Override
    public Result updatePwd(ResetPwd resetPwd) {
        Result result = Result.getDefaultFalse();
        SysUser sysUser = sysUserService.getById(resetPwd.getUserId());
        if(null == sysUser ){
            result.setMsg(ErrorCodeUtil.USER_NOT_EXIST);
            return result;
        }
        //判断旧密码是否正确
        String oldPwd = Md5.md5(resetPwd.getNewPwd(),sysUser.getSalt());
        if(!oldPwd.equals(sysUser.getPassword())){
            return new Result().Faild(ErrorCodeUtil.OLD_PWD_ERROR);
        }

        //获取新的密码盐
        String salt = RandomUtil.getStr(ConstantUtil.SALT_LENGTH);
        log.info("密码盐：{}",salt);
        //将新的密码进行加密
        String newPwd = Md5.md5(resetPwd.getNewPwd(),salt);
        boolean flag = sysUserService.update(new UpdateWrapper<SysUser>().set("password", newPwd).set("salt", salt).eq("id", resetPwd.getUserId()));
        if(flag){
            return Result.getDefaultTrue();
        }
        return Result.getDefaultFalse();
    }



    @Override
    public Result paltLogin(Login login) {

        Result result = loginCheck(login,AccountType.PLATFORM.value());
        if(!result.isResult()){
            return result;
        }
        AuthUserBo authUserBo = (AuthUserBo) result.getData();
//        List<String> list  = sysMenuService.getListByUserId(userId);
//        log.info("当前用户：{}，所拥有权限：{}",userId,list);
//        if(null != list && list.size() > 0 ){
//            loginRedisDao.savePersission(list,userId);
//        }
        //修改登录时间
        sysUserService.update(new UpdateWrapper<SysUser>().set("login_time",ConstantsUtil.DF.format(new Date())).eq("id",authUserBo.getUser().getId()));
        LoginUserBo loginUserBo = copyToLoginUser(authUserBo);
        result.setData(loginUserBo);
        return result;
    }

    @Override
    public Result enterpriseLogin(Login login) {
        Result result = loginCheck(login,AccountType.ENTERPRISE.value());
        if(!result.isResult()){
            return result;
        }
        //清楚极光tag
        AuthUserBo authUserBo = (AuthUserBo) result.getData();
        myJPushClient.deleteTag(authUserBo.getUser().getId(),AccountType.ENTERPRISE.value());
        Long userId = authUserBo.getUser().getId();
        sysUserService.update(new UpdateWrapper<SysUser>().set("login_time",ConstantsUtil.DF.format(new Date())).eq("id",userId));
        LoginUserBo loginUserBo = copyToLoginUser(authUserBo);
        result.setData(loginUserBo);
        return result;
    }

    @Override
    public Result updatePwdBySms(Long userId, ForgetPwdVo forgetPwdVo) {
        Result result =Result.getDefaultFalse();
        SysUser sysUser = sysUserService.getById(userId);
        if(null == sysUser){
            return  new Result().Faild(ErrorCodeUtil.USER_NOT_EXIST);
        }
        //获取短信验证码
        SmsVo smsVo = new SmsVo();
        smsVo.setAccount(sysUser.getUserName());
        smsVo.setSmsType(SmsType.UPDATEPWD.value());
        Result sms = smsFeign.getSms(smsVo);
        log.info("获取验证码结果：{}",sms);
        if(!sms.isResult() || null == sms.getData()){
            //result.setMsg("请获取验证码");
            result.setMsg(ErrorCodeUtil.PLEASE_PULL_SMS_CODE);
            return result;
        }
        String code = (String) sms.getData();
        if(!forgetPwdVo.getCode().equals(code)){
            //result.setMsg("验证码不正确");
            result.setMsg(ErrorCodeUtil.CODE_ERROR);
            return result;
        }
        //获取新的密码盐
        String salt = RandomUtil.getStr(ConstantUtil.SALT_LENGTH);
        log.info("密码盐：{}",salt);
        //将新的密码进行加密
        String newPwd = Md5.md5(forgetPwdVo.getNewPwd(),salt);
        boolean update = sysUserService.update(new UpdateWrapper<SysUser>().set("password", newPwd).set("salt", salt).eq("id", userId));
        if(update){
            return Result.getDefaultTrue();
        }
        return result;
    }

    @Override
    public Result wxQrCodeLogin(Long userId,String device) {
        SysUser byId = sysUserService.getById(userId);
        if(null == byId){
            return new Result().Faild(ErrorCodeUtil.USER_NOT_EXIST);
        }
        if(byId.getType().equals(AccountType.PERSONAGE.value())){

        }
        AuthUserBo authUserBo = saveTokenInfo(byId, device);
        Result result = Result.getDefaultTrue();
        result.setData(authUserBo);
        return result;
    }


    private LoginUserBo copyToLoginUser(AuthUserBo authUserBo){
        LoginUserBo loginUserBo = new LoginUserBo();
        BeanUtils.copyProperties(authUserBo,loginUserBo);
        return loginUserBo;
    }


    /**
     * 校验登录
     * @param login 登录信息
     * @param type 账号类型
     * @return
     */
    private Result loginCheck(Login login,Integer type){
        Result result = Result.getDefaultFalse();

        SysUser user = sysUserService.selectUserByUserName(login.getAccount(),type);
        if(null == user){
            result.setMsg(ErrorCodeUtil.USER_NOT_EXIST);
            return result;
        }
        if(type != user.getType()){
            result.setMsg(ErrorCodeUtil.ILLEGAL_REQUEST);
            return result;
        }
        SaveLogVo saveLogVo = SaveLogVo.builder().userId(user.getId()).content("用户登录")
                .name(user.getNickName() == null ? "-" : user.getNickName())
                .username(user.getUserName())
                .useragent(login.getDevice())
                .userType(user.getType())
                .mainUserId(user.getBelongId() == 0 ? user.getId() : user.getBelongId())
                .build();
        if(user.getType() != 5 && "pc".equals(login.getDevice())){
            if(StringUtils.isEmpty(login.getCode())){
                log.info("请输入验证码");
                result.setMsg(ErrorCodeUtil.CODE_ERROR);
                return result;
            }
            //判断验证码是否正确
            String code = redisService.getValueByKey(login.getRandomData());
            if(StringUtils.isEmpty(code) || !code.equals(login.getCode().toUpperCase())){
                //result.setMsg("验证码错误");
                saveLogVo.setFailMsg("验证码错误");
                saveLogVo.setStatus(0);
                loginUtil.saveLoginLog(saveLogVo);
                log.info("图片验证码错误");
                result.setMsg(ErrorCodeUtil.CODE_ERROR);
                return result;
            }
        }
        //加密前端传过来的密码
        String password = Md5.md5(login.getPassword(), user.getSalt());
        //判断密码是否正确

        if(!password.equals(user.getPassword())){
            //保存登录日志
            saveLogVo.setFailMsg(ErrorCodeUtil.PWD_ERROR);
            saveLogVo.setStatus(0);
            loginUtil.saveLoginLog(saveLogVo);
            result.setMsg(ErrorCodeUtil.ACCOUNT_OR_PWD_ERROR);
           // result.setMsg(ErrorCodeUtil.USER_NAME_OR_PWD_ERROR);
            return result;
        }
        checkUserStatus(user);
        //生成token
        user.setPassword(null);
        //生成密钥
        AuthUserBo authUserBo = saveTokenInfo(user,login.getDevice());
        //保存登录日志
        saveLogVo.setStatus(1);
        loginUtil.saveLoginLog(saveLogVo);
        log.info("保存token信息成功");
        result = Result.getDefaultTrue();
        result.setData(authUserBo);
        return result;
    }

    /**
     * 判断用户是否被禁用
     * @param user
     * @return
     */
    private Result checkUserStatus(SysUser user){
        //判断账号是否是企业账号
        if(AccountType.ENTERPRISE.value() == user.getType()){
            //判断当前用户是否是企业用户并且属于主账号
            if(user.getBelongId() == 0){
                if(user.getStatus() == 2){
                    throw new MyServiceException(ErrorCodeUtil.FORBIDDEN_USER);
                }
            }else{
                //子账号校验主账号是否被禁用
                Integer status = sysUserService.getStatusByUserId(user.getBelongId());
                if(2 == status ){
                    throw new MyServiceException(ErrorCodeUtil.FORBIDDEN_USER);
                }
            }
        }else{
            //其他类型的账号是否被禁用
            if( user.getStatus() == 2){
                throw new MyServiceException(ErrorCodeUtil.FORBIDDEN_USER);
            }
        }
        return Result.getDefaultTrue();
    }




}
