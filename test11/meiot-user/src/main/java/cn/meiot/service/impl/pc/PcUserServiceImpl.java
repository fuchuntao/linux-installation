package cn.meiot.service.impl.pc;

import cn.meiot.entity.*;
import cn.meiot.entity.bo.ExportEnUserBo;
import cn.meiot.entity.bo.ExportSingleUserBo;
import cn.meiot.entity.vo.*;
import cn.meiot.enums.AccountType;
import cn.meiot.exception.MyServiceException;
import cn.meiot.mapper.*;
import cn.meiot.service.ISysUserRoleService;
import cn.meiot.service.api.ApiService;
import cn.meiot.service.pc.PcUserService;
import cn.meiot.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PcUserServiceImpl implements PcUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserProfileMapper sysUserProfileMapper;

    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private ApiService apiService;


    @Override
    @Transactional
    public Result addEnUser(EnterpriseUserVo enterpriseUserVo,Long userId) {
        //查询此账号是否已经存在数据库中
        Integer count = sysUserMapper.selectCount(new QueryWrapper<SysUser>().eq("user_name", enterpriseUserVo.getAccount()));
        if(count > 0 ){
            return new Result().Faild(ErrorCodeUtil.ACCOUNT_IS_EXIST);
        }
        //判断企业是否已经存在
        Integer enterprise_name = enterpriseMapper.selectCount(new QueryWrapper<Enterprise>().eq("enterprise_name", enterpriseUserVo.getCompanName()));
        if(null !=enterprise_name && enterprise_name > 0 ){
            return new Result().Faild(ErrorCodeUtil.COMPANY_NAME_IS_ECIST);
        }
        //保存企业信息
        Enterprise enterprise = Enterprise.builder().enterpriseName(enterpriseUserVo.getCompanName())
                .contacts(enterpriseUserVo.getLegalName()).provice(enterpriseUserVo.getProvince()).deleted(0)
                .city(enterpriseUserVo.getCity()).area(enterpriseUserVo.getDistrict()).address(enterpriseUserVo.getAddr()).build();
        count = enterpriseMapper.insert(enterprise);
        if(null == count || count == 0){
            throw  new MyServiceException(ErrorCodeUtil.ADD_USER_ERROR);
        }
        //保存用户基础信息
        //设置初始密码，通过邮件发送给用户
        String salt = RandomUtil.getStr(ConstantUtil.SALT_LENGTH);
        String password = RandomUtil.getStr(ConstantUtil.INIT_PWD_LEN);
        EmailVo emailVo = EmailVo.builder().password(password).to(enterpriseUserVo.getEmail()).accountType(AccountType.ENTERPRISE.value()).build();
        password = Md5.md5(password,salt);
        SysUser sysUser = SysUser.builder().userName(enterpriseUserVo.getAccount()).deleted(0).nickName("超级管理员")
                .email(enterpriseUserVo.getEmail()).enterpriseId(enterprise.getId()).password(password).isAdmin(1).createUserId(userId)
                .status(1).type(AccountType.ENTERPRISE.value()).salt(salt).build();
        count = sysUserMapper.insert(sysUser);
        if(null == count || count == 0){
            throw  new MyServiceException(ErrorCodeUtil.ADD_USER_ERROR);
        }

        //保存详细信息
        SysUserProfile sysUserProfile = SysUserProfile.builder().province(enterpriseUserVo.getProvince()).city(enterpriseUserVo.getCity())
                .district(enterpriseUserVo.getDistrict()).addr(enterpriseUserVo.getAddr()).userId(sysUser.getId()).build();

        //设置默认头像
        Config config = configMapper.selectOne(new QueryWrapper<Config>().lambda().eq(Config::getCKey, ConstantsUtil.userManager.USER_DEFAULT_HEAD_PORTRAIT));
        if(null != config){
            sysUserProfile.setHeadPortrait(config.getValue());
        }
        count = sysUserProfileMapper.insert(sysUserProfile);
        if(null == count || count == 0){
            throw  new MyServiceException(ErrorCodeUtil.ADD_USER_ERROR);
        }
        //在角色表中添加一条超级管理员的信息
        SysRole sysRole = SysRole.builder().name("超级管理员").isSuperAdmin(1).type(2).belongUserId(sysUser.getId()).build();
        count = sysRoleMapper.insert(sysRole);
        if(null == count || count == 0){
            throw  new MyServiceException(ErrorCodeUtil.ADD_ROLE_ERROR);
        }
        //将用户设置角色
        SysUserRole sysUserRole = SysUserRole.builder().sysUserId(sysUser.getId()).sysRoleId(sysRole.getId()).build();
        count = sysUserRoleMapper.insert(sysUserRole);
        if(null == count || count == 0){
            throw  new MyServiceException(ErrorCodeUtil.SET_ROLE_ERROR);
        }
        //注册人数+1
        PcUserStatisticsVo pcUserStatisticsVo = PcUserStatisticsVo.builder().userType(AccountType.ENTERPRISE.value())
                .type(1).date(ConstantsUtil.DF.format(new Date())).build();
        rabbitTemplate.convertAndSend(QueueConstantUtil.MODIFY_USER_NOTIFICATION,pcUserStatisticsVo);
        //发送邮件
        rabbitTemplate.convertAndSend(QueueConstantUtil.SEND_EMAIL_QUEUE,emailVo);
        return Result.getDefaultTrue();
    }

    @Override
    @Transactional
    public Result updateEnUser(EnterpriseUserVo enterpriseUserVo) {
        //查询用户信息
        SysUser sysUser = sysUserMapper.selectById(enterpriseUserVo.getId());
        if(null == sysUser){
            log.info("用户id:{}不存在",enterpriseUserVo.getId());
            return new Result().Faild(ErrorCodeUtil.USER_NOT_EXIST);
        }
//        if(1 == sysUser.getIsAdmin()){
//            log.info("修改的用户属于超级管理员，无法修改");
//            return new Result().Faild("无法修改超级管理员信息");
//        }
        //修改用户信息
        SysUser user = SysUser.builder().id(sysUser.getId()).userName(enterpriseUserVo.getAccount()).email(enterpriseUserVo.getEmail())
                .status(1).type(AccountType.ENTERPRISE.value()).build();
        Integer count = sysUserMapper.updateById(user);
        if(null == count || count == 0){
            throw  new MyServiceException(ErrorCodeUtil.UPDATE_BASE_INFO_ERROR);
        }
        //修改企业信息
        Enterprise enterprise = Enterprise.builder().id(sysUser.getEnterpriseId()).enterpriseName(enterpriseUserVo.getCompanName()).contacts(enterpriseUserVo.getLegalName()).provice(enterpriseUserVo.getProvince())
                .city(enterpriseUserVo.getCity()).area(enterpriseUserVo.getDistrict()).address(enterpriseUserVo.getAddr()).build();
        count = enterpriseMapper.updateById(enterprise);
        if(null == count || count == 0){
            throw  new MyServiceException(ErrorCodeUtil.UPDATE_ENT_INFO_ERROR);
        }
        //修改用户详细信息
        SysUserProfile sysUserProfile = SysUserProfile.builder().province(enterpriseUserVo.getProvince()).city(enterpriseUserVo.getCity())
                .district(enterpriseUserVo.getDistrict()).addr(enterpriseUserVo.getAddr()).build();
        count = sysUserProfileMapper.update(sysUserProfile,new UpdateWrapper<SysUserProfile>().eq("user_id",sysUser.getId()));
        if(null == count || count == 0){
            throw  new MyServiceException(ErrorCodeUtil.UPDATE_DETAIL_INFO_ERROR);
        }
        return Result.getDefaultTrue();
    }

    @Override
    @Transactional
    public Result deleteUser(Long adminId,List<Long> ids) {
        Result result = chechpermission(adminId, ids);
        if(!result.isResult()){
            return result;
        }
        if(ids.contains(adminId)){
            return new Result().Faild(ErrorCodeUtil.NOT_CAN_DELETE_SELF);
        }
        //判断删除的是否有超级管理员
        Integer count = sysUserMapper.selectCount(new QueryWrapper<SysUser>().lambda().eq(SysUser::getIsAdmin, 1).in(SysUser::getId, ids));
        if(count > 0 ){
            return new Result().Faild(ErrorCodeUtil.NOT_CAN_DELETE_SUP_ADMIN);
        }
        count = sysUserMapper.deleteBatchIds(ids);
        if(count > 0 ){
            //清除用户角色信息
            sysUserRoleService.remove(new UpdateWrapper<SysUserRole>().lambda().in(SysUserRole::getSysUserId,ids));
            return Result.getDefaultTrue();
        }
        return Result.getDefaultFalse();
    }


    @Override
    public List<ExportEnUserBo> getExportEnUser(ExportUserVo exportUserVo) {
//        SysUser sysUser = sysUserMapper.selectById(userId);
//        if(null == sysUser){
//            log.info("未查询到当前用户：{}",userId);
//            return Result.getDefaultFalse();
//        }
//        if(AccountType.PLATFORM.value() !=sysUser.getType()){
//            return new Result().Faild("越权操作！");
//        }
        return sysUserMapper.getExportEnUser(exportUserVo);
    }


    /**
     * 设置导出的省市区
     */
    public void exprot(List<ExportEnUserBo> list, String fileName, HttpServletResponse response){
//        for(ExportEnUserBo p: list){
//            StringBuffer sb = new StringBuffer();
//            p.setAddr(sb.append(p.getProvince()).toString());
//            p.setAddr(sb.append(p.getCity()).toString());
//            p.setAddr(sb.append(p.getDistrict()).toString());
//            p.setAddr(sb.append(p.getAddress()).toString());
//        }
        ExcelUtils.export(list,fileName,response, ExportEnUserBo.class);
    }

    @Override
    public List<ExportSingleUserBo> getExportsingleUser(ExportUserVo exportUserVo) {

        return sysUserMapper.getExportsingleUser(exportUserVo);
    }

    @Override
    public Result forbidden(Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        Result result = userStatusCheck(sysUser);
        if(!result.isResult()){
            return result;
        }
        //禁用当前用户
        Integer count = sysUserMapper.forbidden(userId);
        if(null != count && count > 0){
            //将用户踢下线
            rabbitTemplate.convertAndSend(QueueConstantUtil.TAKE_THE_USER_OFFLINE,sysUser);
            return Result.getDefaultTrue();
        }
        return Result.getDefaultFalse();
    }

    @Override
    public Result enable(Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        Result result = userStatusCheck(sysUser);
        if(!result.isResult()){
            return result;
        }
        sysUser.setStatus(1);
        int count = sysUserMapper.updateById(sysUser);
        if(count > 0 ){
            return Result.getDefaultTrue();
        }
        return Result.getDefaultFalse();
    }

    @Override
    public Result resetPwd(Long adminId, Long userId) {
        //获取主账号信息
        SysUser adminUser = sysUserMapper.selectById(adminId);
        //获取操作对象信息
        SysUser user = sysUserMapper.selectById(userId);
        if(adminUser.getType().equals(AccountType.PLATFORM.value())){
            //当前操作用户是平台账号，只能禁用平台账号（超级管理员除外）+企业账号（主账号）
            if(user.getType().equals(AccountType.PLATFORM.value()) && user.getIsAdmin() == 1){
                log.info("不能重置主账号");
                return new Result().Faild(ErrorCodeUtil.SIMPLY_OVERSTEPPED_HIS_BOUNDS);
            }
            if(user.getType().equals(AccountType.ENTERPRISE.value()) && user.getBelongId() != 0){
                log.info("不能重置企业账号的子账号");
                return new Result().Faild(ErrorCodeUtil.SIMPLY_OVERSTEPPED_HIS_BOUNDS);
            }
        }else if(adminUser.getType().equals(AccountType.ENTERPRISE.value())){
            //只能禁用企业账号（主账号除外）
            if(user.getBelongId() == 0){
                log.info("不能重置企业账号的主账号");
                return new Result().Faild(ErrorCodeUtil.SIMPLY_OVERSTEPPED_HIS_BOUNDS);
            }
        }else{
            return new Result().Faild(ErrorCodeUtil.SIMPLY_OVERSTEPPED_HIS_BOUNDS);
        }

        String salt = RandomUtil.getStr(ConstantUtil.SALT_LENGTH);
        String password = RandomUtil.getStr(ConstantUtil.INIT_PWD_LEN);
        String newPwd= Md5.md5(password, salt);
        user.setSalt(salt);
        user.setPassword(newPwd);
        Integer count = sysUserMapper.updateById(user);
        if(null != count && count > 0){
            //发送密码
            EmailVo emailVo = EmailVo.builder().password(password).to(user.getEmail()).accountType(user.getType()).build();
            log.info("通过邮箱发送密码,参数{}", emailVo);
            rabbitTemplate.convertAndSend(QueueConstantUtil.SEND_EMAIL_QUEUE, emailVo);
            return Result.getDefaultTrue();
        }

        return Result.getDefaultFalse();
    }

    @Override
    public List queryElectrician(Integer projectId,String permissionCode) {
        List<Long> longs = apiService.listUserIdByPermission(permissionCode, projectId);
        if(longs == null ){
            return new ArrayList();
        }
        List<SysUser> maps = sysUserMapper.selectList(new QueryWrapper<SysUser>()
                .lambda().select(SysUser::getId, SysUser::getNickName, SysUser::getUserName)
                .in(SysUser::getId, longs));
        return maps;
    }

    /**
     * 启用和禁用的条件判断
     * @param sysUser
     * @return
     */
    private Result userStatusCheck(SysUser sysUser){
        if(null == sysUser){
            return new Result().Faild(ErrorCodeUtil.USER_NOT_EXIST);
        }
        if(sysUser.getType() ==AccountType.PLATFORM.value() && sysUser.getIsAdmin() == 1){
            // 不能操作超级管理员
            return new Result().Faild(ErrorCodeUtil.SIMPLY_OVERSTEPPED_HIS_BOUNDS);
        }
        if(AccountType.ENTERPRISE.value() == sysUser.getType() && sysUser.getBelongId() != 0){
            //请操作主账号
            return new Result().Faild(ErrorCodeUtil.SIMPLY_OVERSTEPPED_HIS_BOUNDS);
        }
        return Result.getDefaultTrue();
    }


    private Result chechpermission(Long adminId, List<Long> ids){
        SysUser adminUser = sysUserMapper.selectById(adminId);
        Integer count = sysUserMapper.selectCount(new QueryWrapper<SysUser>().in("id", ids)
                .and(i -> i.eq("is_admin",1).or().ne("type", adminUser.getType())));
        if(count > 0 ){
            return new Result().Faild(ErrorCodeUtil.SIMPLY_OVERSTEPPED_HIS_BOUNDS);
        }
//        SysUser sysUser = sysUserMapper.selectById(userId);
//        if(adminUser.getType() != sysUser.getType()){
//            return new Result().Faild("越权操作");
//        }
        return Result.getDefaultTrue();
    }
}
