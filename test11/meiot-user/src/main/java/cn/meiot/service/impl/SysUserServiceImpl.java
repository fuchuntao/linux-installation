package cn.meiot.service.impl;

import cn.meiot.entity.*;
import cn.meiot.entity.bo.PcUserInfo;
import cn.meiot.entity.bo.PlatUser;
import cn.meiot.entity.vo.*;
import cn.meiot.enums.AccountType;
import cn.meiot.exception.MyServiceException;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.mapper.*;
import cn.meiot.service.ISysUserRoleService;
import cn.meiot.service.ISysUserService;
import cn.meiot.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-07-29
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    private static final ReadWriteLock rwl = new ReentrantReadWriteLock();

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Value("${img.map}")
    private String mapping;

    @Value("${img.servername}")
    private String serverName;

    @Autowired
    private ImgConfigVo imgConfigVo;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private SysUserProfileMapper sysUserProfileMapper;

    @Autowired
    private RedisUtil  redisUtil;



    @Override
    public SysUser selectUserByUserName(String account,Integer type) {

        QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<SysUser>().eq("user_name", account).eq("type",type);
        return sysUserMapper.selectOne(sysUserQueryWrapper);
    }

    @Override
    public Result resetPwd(ResetPwd resetPwd) {
        Result result = Result.getDefaultFalse();
        //根据用户id查询用户信息
        QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<SysUser>().eq("id", resetPwd.getUserId());
        SysUser sysUser = sysUserMapper.selectOne(sysUserQueryWrapper);
        if (null == sysUser) {
            // result.setMsg("用户信息不存在");
            result.setMsg(ErrorCodeUtil.USER_NOT_EXIST);
            return result;
        }
        //加密原密码
        String oldPwd = Md5.md5(resetPwd.getOldPwd(), sysUser.getSalt());
        //与数据库的密码进行对比
        if (!oldPwd.equals(sysUser.getPassword())) {
            //result.setMsg("原密码不正确");
            result.setMsg(ErrorCodeUtil.OLD_PWD_NOT_BE_NULL);
            return result;
        }

        //获取新的密码盐
        String salt = RandomUtil.getStr(ConstantUtil.SALT_LENGTH);
        //将新密码加密
        String newPwd = Md5.md5(resetPwd.getNewPwd(), salt);

        //更新数据库的密码与密码盐
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<SysUser>().eq("id", resetPwd.getUserId());
        sysUser = new SysUser();
        sysUser.setPassword(newPwd);
        sysUser.setSalt(salt);
        Integer count = sysUserMapper.update(sysUser, updateWrapper);
        if (count == 1) {
            return Result.getDefaultTrue();
        }
        result.setMsg("失败");
        return result;
    }

    @Override
    public Result addUser(NewSysUserVo newSysUserVo) {
        synchronized (ConstantUtil.USER_LOCK){
            //条件是否满足
            Result result = verifySysUser(newSysUserVo);
            if (!result.isResult()) {
                return result;
            }
            //获取新的密码盐
            String salt = RandomUtil.getStr(ConstantUtil.SALT_LENGTH);
            //将密码加密
            String pwd = Md5.md5(newSysUserVo.getPwd(), salt);
            //修改
            SysUser sysUser = SysUser.builder().userName(newSysUserVo.getAccount())
                    .password(pwd).salt(salt).status(ConstantsUtil.NORMAL_ROLE_STATUS).build();
            Integer count = sysUserMapper.insert(sysUser);
            if (count == 1) {
                return Result.getDefaultTrue();
            }
        }

        return Result.getDefaultFalse();
    }

    @Override
    public Result updateUser(NewSysUserVo newSysUserVo) {
        synchronized (ConstantUtil.USER_LOCK){
            //条件是否满足
            Result result = verifySysUser(newSysUserVo);
            if (!result.isResult()) {
                return result;
            }

            //获取新的密码盐
            String salt = RandomUtil.getStr(ConstantUtil.SALT_LENGTH);
            //将密码加密
            String pwd = Md5.md5(newSysUserVo.getPwd(), salt);
            //修改
            UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<SysUser>().eq("id", newSysUserVo.getId());
            SysUser sysUser = SysUser.builder().userName(newSysUserVo.getAccount())
                    .password(pwd).salt(salt).status(ConstantsUtil.NORMAL_ROLE_STATUS).build();
            Integer count = sysUserMapper.update(sysUser, updateWrapper);
            if (count == 1) {
                return Result.getDefaultTrue();
            }
        }

        return Result.getDefaultFalse();
    }

//    @Override
//    public Result getList(Long userId, Integer type,Page<SysUser> Page) {
//        SysUser sysUser = sysUserMapper.selectById(userId);
//        if(null == sysUser){
//            log.info("未查询到当前用户：{}",userId);
//            return Result.getDefaultFalse();
//        }
//        Result result = AccountTypeUtil.check(sysUser.getType(), type);
//        if(!result.isResult()){
//            return result;
//        }
//        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<SysUser>();
//        queryWrapper.eq("type",type);
//        if(sysUser.getType() == AccountType.PLATFORM.value()){
//            queryWrapper.eq("belong_id",0);
//        }else{
//            //企业用户
//            queryWrapper.eq("belong_id",0 == sysUser.getBelongId() ? sysUser.getId() :sysUser.getBelongId());
//        }
//
//
//        IPage<SysUser> sysUsers = sysUserMapper.selectPage(Page, queryWrapper);
//        result.setData(sysUsers);
//        return result;
//    }

    @Override
    public Result editNikname(UserVo userVo) {
        Result result = Result.getDefaultFalse();
        Lock readLock = rwl.readLock();
        try {
            readLock.lock();
            Long id = sysUserMapper.selectIdByNikName(userVo.getNikName());
            if (null != id && id > 0) {
                log.info("昵称已存在，请换一个重试");
                result.setMsg(ErrorCodeUtil.NIKENAME_IS_EXIST);
                return result;
            }
        } finally {
            //解锁（读锁）
            readLock.unlock();
        }
        //此时昵称还未存在  加锁（写锁）
        Lock writeLock = rwl.writeLock();
        try {
            writeLock.lock();
            Long id = sysUserMapper.selectIdByNikName(userVo.getNikName());
            if (null != id && id > 0) {
                log.info("昵称已存在，请换一个重试");
                result.setMsg(ErrorCodeUtil.NIKENAME_IS_EXIST);
                return result;
            }
            SysUser sysUser = SysUser.builder().id(userVo.getUserId()).nickName(userVo.getNikName()).build();
            Integer count = sysUserMapper.updateById(sysUser);
            if (null != count && count == 1) {
                redisUtil.deleteHashKey(RedisConstantUtil.USER_NIKNAMES,userVo.getUserId().toString());
                return Result.getDefaultTrue();
            }
            return result;
        } finally {
            //解锁（写锁）
            writeLock.unlock();
        }

    }

    @Override
    public Result getEnterpriseList(Long userId, Integer currentPage, Integer pageSize, String keyword) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (null == sysUser) {
            log.info("未查询到当前用户：{}", userId);
            return new Result().Faild(ErrorCodeUtil.USER_NOT_EXIST);
        }
        if (AccountType.PLATFORM.value() != sysUser.getType()) {
            return new Result().Faild(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
        }
        SysUser user = new SysUser();
        user.setType(AccountType.ENTERPRISE.value());
        user.setBelongId(0l);
        List<PcUserInfo> pcUserInfoIPage = sysUserMapper.getPersonList(user, currentPage, pageSize, keyword);
        Integer count = sysUserMapper.getPersonListCount(user, keyword);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", pcUserInfoIPage);
        map.put("count", count);
        Result result = Result.getDefaultTrue();
        result.setData(map);
        return result;
    }

    @Override
    public Result getPersonList(Long userId, Integer currentPage, Integer pageSize, String keyword) {
        Result result = Result.getDefaultFalse();
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (null == sysUser) {
            log.info("未查询到当前用户：{}", userId);
            return Result.getDefaultFalse();
        }
        if (AccountType.PLATFORM.value() != sysUser.getType()) {
            result.setMsg(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
            return result;
        }
        sysUser = new SysUser();
        sysUser.setType(AccountType.PERSONAGE.value());
        Map<String, Object> map = new HashMap<String, Object>();
        result = Result.getDefaultTrue();
        Integer count = sysUserMapper.getPersonListCount(sysUser, keyword);
        List<PcUserInfo> pcUserInfoIPage = null;
        if(null != count && count >=  0){
           pcUserInfoIPage = sysUserMapper.getPersonList(sysUser, currentPage, pageSize, keyword);
           for(PcUserInfo user: pcUserInfoIPage){
                //获取当前用户的设备数量
               user.setDeviceNum(deviceFeign.queryBindNum(user.getUserId()));
               user.setHeadPortrait(null == user.getHeadPortrait() ? null : getHeadPortrait(user.getHeadPortrait()));
           }
        }
        map.put("list", pcUserInfoIPage);
        map.put("count", count);
        result.setData(map);
        return result;
    }
    @Override
    public String getHeadPortrait(String headPortrait) {
        return imgConfigVo.getServername()+imgConfigVo.getMap()+imgConfigVo.getImg()+headPortrait;

    }

    @Override
    public Result chechPassword(String account, String password) {
        SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getUserName, account)
                .eq(SysUser::getType, AccountType.ENTERPRISE.value()).eq(SysUser::getDeleted, 0));
        if(sysUser == null ){
            return new Result().Faild(ErrorCodeUtil.USER_NOT_EXIST);
        }
        //加密前端传过来的密码
        password = Md5.md5(password, sysUser.getSalt());
        if(!password.equals(sysUser.getPassword())){
            return new Result().Faild(ErrorCodeUtil.PWD_ERROR);
        }
        Result result = Result.getDefaultTrue();
        result.setData(sysUser);
        return result;
    }

    @Override
    @Transactional
    public Result addSysUser(SysUserVo sysUserVo, Long userId) {

        synchronized (ConstantUtil.USER_LOCK){
            SysUser user = sysUserMapper.selectById(userId);
            if (null == user || user.getType() != sysUserVo.getType()) {
                return new Result().Faild(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
            }
            //判断用户是否已经存在
            Integer count = sysUserMapper.selectCount(new QueryWrapper<SysUser>().eq("user_name", sysUserVo.getAccount()).eq("type",sysUserVo.getType()));
            if (null != count && count > 0) {
                return new Result().Faild(ErrorCodeUtil.ACCOUNT_EXIST);
            }
            count = sysUserMapper.selectCount(new QueryWrapper<SysUser>().eq("email",sysUserVo.getEmail()));
            if (null != count && count > 0) {
                return new Result().Faild(ErrorCodeUtil.EMAIL_IS_USE);
            }

            //获取主账号id
            Long mainUserId = AccountTypeUtil.getMainUserId(user);
            //获取新的密码盐
            String salt = RandomUtil.getStr(ConstantUtil.SALT_LENGTH);
            String newPwd = RandomUtil.getStr(ConstantUtil.INIT_PWD_LEN);
            //将密码加密
            String pwd = Md5.md5(newPwd, salt);
            SysUser sysUser = SysUser.builder().userName(sysUserVo.getAccount()).salt(salt).password(pwd)
                    .createUserId(userId).email(sysUserVo.getEmail()).deleted(0)
                    .nickName(sysUserVo.getNikName()).status(1).belongId(mainUserId).type(sysUserVo.getType()).build();
            if (AccountType.ENTERPRISE.value().equals(sysUserVo.getType())) {
                sysUser.setBelongId(user.getBelongId() == 0 ? user.getId() : user.getBelongId());
                sysUser.setEnterpriseId(user.getEnterpriseId());
            }
            //添加用户
            Integer insert = sysUserMapper.insert(sysUser);
            if (null == insert || insert == 0) {
                throw new MyServiceException(ErrorCodeUtil.ADD_FAILD);
            }
            //添加角色
            addRole(sysUser.getId(), sysUserVo.getRoles());

            SysUserProfile sysUserProfile = new SysUserProfile();
            sysUserProfile.setUserId(sysUser.getId());
            //设置默认头像
//            Config config = configMapper.selectOne(new QueryWrapper<Config>().lambda().eq(Config::getCKey, ConstantsUtil.userManager.USER_DEFAULT_HEAD_PORTRAIT));
//            if(null != config){
//                sysUserProfile.setHeadPortrait(config.getValue());
//            }
            count = sysUserProfileMapper.insert(sysUserProfile);
            if(null == count || count == 0){
                throw  new MyServiceException(ErrorCodeUtil.ADD_USER_ERROR);
            }


            //发送密码
            EmailVo emailVo = EmailVo.builder().password(newPwd).to(sysUserVo.getEmail()).accountType(user.getType()).build();
            log.info("通过邮箱发送密码,参数{}", emailVo);
            rabbitTemplate.convertAndSend(QueueConstantUtil.SEND_EMAIL_QUEUE, emailVo);
        }
        return Result.getDefaultTrue();
    }

    @Override
    @Transactional
    public Result updateSysUser(SysUserVo sysUserVo, Long userId) {
        synchronized (ConstantUtil.USER_LOCK){
            SysUser adminUser = sysUserMapper.selectById(userId);
            SysUser userInfo = sysUserMapper.selectById(sysUserVo.getId());
            if (null == adminUser || null == userInfo || !adminUser.getType().equals(userInfo.getType())) {
                return new Result().Faild(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
            }
            if (AccountType.ENTERPRISE.value() == userInfo.getType()) {
                if (!userInfo.getBelongId().equals(adminUser.getBelongId()) && !userInfo.getBelongId().equals(adminUser.getId())) {
                    return new Result().Faild(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
                }
            }
            if(1 == userInfo.getIsAdmin()){
                //超级管理员账号不能未修改
                return new Result().Faild(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
            }
            Integer count = sysUserMapper.selectCount(new QueryWrapper<SysUser>().lambda().eq(SysUser::getEmail,sysUserVo.getEmail()).ne(SysUser::getId,sysUserVo.getId()));
            if(count > 0){
                return new Result().Faild(ErrorCodeUtil.EMAIL_IS_USE);
            }
            Long id = sysUserMapper.selectIdByAccount( sysUserVo.getAccount());
            if(null != id  && !id.equals(userInfo.getId()) ){
                return new Result().Faild(ErrorCodeUtil.ACCOUNT_EXIST);
            }
            //修改
            SysUser sysUser = SysUser.builder().id(sysUserVo.getId()).userName(sysUserVo.getAccount()).email(sysUserVo.getEmail())
                    .nickName(sysUserVo.getNikName()).build();
            int i = sysUserMapper.updateById(sysUser);
            if (i == 0) {
                return Result.getDefaultFalse();
            }
            //删除当前用户的所有角色
            sysUserRoleService.remove(new UpdateWrapper<SysUserRole>().eq("sys_user_id", sysUserVo.getId()));
//        if (!flag) {
//            throw new MyServiceException("删除角色失败", "删除角色失败");
//        }
            //修改角色
            addRole(sysUserVo.getId(), sysUserVo.getRoles());
        }
        return Result.getDefaultTrue();
    }

    @Override
    public Result getAdminList(Map<String, Object> map, Long userId) {
        Result result = Result.getDefaultTrue();
        Integer type = (Integer) map.get("type");
        //判断是否越权
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (null == sysUser ) {
            return new Result().Faild(ErrorCodeUtil.USER_NOT_EXIST);
        }
        if (type != sysUser.getType()) {
            return new Result().Faild(ErrorCodeUtil.PERMISSION_NOT_ALLOW);
        }
        if(type == AccountType.ENTERPRISE.value()){
            map.put("belongId",0 == sysUser.getBelongId() ? sysUser.getId() : sysUser.getBelongId());
        }
        Integer count = sysUserMapper.getAdminListCount(map);
        Map<String, Object> res = new HashMap<String, Object>();
        if (null == count || count == 0) {
            res.put("total", 0);
            res.put("list", null);
            result.setData(res);
            return result;
        }
        List<PlatUser> list = sysUserMapper.getAdminList(map);
        list.forEach(user ->{
            SysRole sysRole = sysRoleMapper.queryNameById(user.getId());
            if(null != sysRole){
                user.setRoleName(sysRole.getName());
                user.setRoleId(sysRole.getId());
            }
        });
        res.put("total", count);
        res.put("list", list);

        result.setData(res);
        return result;
    }

    @Override
    public Result deleteEnUser(Long userId) {
        //查询此账号是否是企业账号的主账号
        SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().lambda().eq(SysUser::getId, userId).eq(SysUser::getBelongId, 0));
        if(sysUser ==null){
            return new Result().Faild(ErrorCodeUtil.USER_NOT_EXIST);
        }
        //判断当前账号是否已经绑定了项目
        Integer num = projectMapper.selectCount(new QueryWrapper<Project>().lambda().eq(Project::getEnterpriseId, sysUser.getEnterpriseId()));
        if(num != num && num > 0){
            return new Result().Faild(ErrorCodeUtil.USER_HAVE_PROJECT_CAN_NOT_DELETE);
        }
        Integer count = sysUserMapper.deleteById(userId);
        if(count > 0 ){
            //注册人数-1
            PcUserStatisticsVo pcUserStatisticsVo = PcUserStatisticsVo.builder().userType(AccountType.ENTERPRISE.value())
                    .type(0).date(sysUser.getCreateTime()).build();
            rabbitTemplate.convertAndSend(QueueConstantUtil.MODIFY_USER_NOTIFICATION,pcUserStatisticsVo);
            return Result.getDefaultTrue();
        }
        return Result.getDefaultFalse();
    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        return sysUserMapper.getStatusByUserId(userId);
    }


    /**
     * 给用户添加角色
     *
     * @param userId 用户id
     * @param roles  角色列表
     */
    private void addRole(Long userId, List<Integer> roles) {
        if (null == roles || roles.size() == 0) {
            log.info("喂给用户添加角色==============>");
            return;
        }
        List<SysUserRole> list = new ArrayList<SysUserRole>();
        for (Integer roleId : roles) {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setSysUserId(userId);
            sysUserRole.setSysRoleId(roleId);
            list.add(sysUserRole);
        }
        boolean flag = sysUserRoleService.saveBatch(list);
        if (!flag) {
            throw new MyServiceException(ErrorCodeUtil.ADD_ROLE_ERROR);
        }
    }


    /**
     * 数据校验
     *
     * @param newSysUserVo
     * @return
     */
    private Result verifySysUser(NewSysUserVo newSysUserVo) {
        Result result = Result.getDefaultFalse();
        if (null == newSysUserVo) {
            //result.setMsg("参数不能为空");
            result.setMsg(ErrorCodeUtil.PARMA_NOT_BE_NULL);
            return result;
        }
        if (StringUtils.isEmpty(newSysUserVo.getAccount())) {
            // result.setMsg("账号不能为空");
            result.setMsg(ErrorCodeUtil.ACCOUNT_NOT_BE_NULL);
            return result;
        }
        if (StringUtils.isEmpty(newSysUserVo.getNickName())) {
            //result.setMsg("昵称不能为空");
            result.setMsg(ErrorCodeUtil.NIKNAME_NOT_BE_NULL);
            return result;
        }
        if (null == newSysUserVo.getPositionId()) {
            //result.setMsg("职位不能为空");
            result.setMsg(ErrorCodeUtil.POSITION_NOT_BE_NULL);
            return result;
        }
        if (StringUtils.isEmpty(newSysUserVo.getPwd())) {
            //result.setMsg("密码不能为空");
            result.setMsg(ErrorCodeUtil.PWD_NOT_BE_NULL);
            return result;
        }
        if (!VerifyUtil.verifyPhone(newSysUserVo.getAccount())) {
            //result.setMsg("手机号不合法");
            result.setMsg(ErrorCodeUtil.PHONE_FORMAT_ERROR);
            return result;
        }
        if (!newSysUserVo.getPwd().matches(ConstantsUtil.PWD_RULE)) {
            //result.setMsg("密码不符合规则");
            result.setMsg(ErrorCodeUtil.PWD_FORMAT_ERROR);
            return result;
        }
        return Result.getDefaultTrue();
    }
}
