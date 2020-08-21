package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.entity.SysUser;
import cn.meiot.entity.SysUserProfile;
import cn.meiot.entity.UserOpenid;
import cn.meiot.entity.UserUnionid;
import cn.meiot.entity.bo.UserInfo;
import cn.meiot.entity.vo.FileConfigVo;
import cn.meiot.entity.vo.NewSysUserVo;
import cn.meiot.entity.vo.ResetPwd;
import cn.meiot.entity.vo.Result;
import cn.meiot.enums.AccountType;
import cn.meiot.service.*;
import cn.meiot.utils.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-07-29
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class SysUserController extends BaseController {

    @Value("${img.map}")
    private String imgMap;

    @Value("${img.servername}")
    private String serverName;

    @Value("${img.img}")
    private  String img;

    @Value("${img.thumbnail}")
    private  String thum;


    @Autowired
    private ISysUserProfileService sysUserProfileService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private IUserOpenidService userOpenidService;

    @Autowired
    private IUserUnionidService  userUnionidService;

    @Autowired
    private IConfigService configService;


    /**
     *新增用户
     * @return
     */
    @RequestMapping(value = "/saveUser",method = RequestMethod.POST)
    public Result addUser(@RequestBody NewSysUserVo newSysUserVo){
        return sysUserService.addUser(newSysUserVo);

    }

    /**
     * 查询用户里列表
     * @param type  用户类型   1：平台用户   2：企业用户
     * @return
     */
//    @RequestMapping(value = "listBy",method = RequestMethod.GET)
//    @Log(operateContent = "查询用户列表")
//    public Result list(@RequestParam("type") Integer type,@RequestParam("currentPage")Integer currentPage,@RequestParam("pageSize")Integer pageSize){
//       log.info("查询开始============>");
//        if(null == type){
//            Result result = Result.getDefaultFalse();
//            result.setMsg("");
//            return result;
//        }
//        if( null == currentPage){
//            currentPage = 1;
//        }
//        if(null == pageSize){
//            pageSize = 15;
//        }
//        Page<SysUser> Page = new Page<SysUser>(currentPage,pageSize);
//        return sysUserService.getList(getUserId(),type,Page);
//    }


    /**
     * 获取本人的用户信息
     * @return
     */
    @GetMapping(value = "/info")
    @Log(operateContent = "查询用户信息",operateModule = "用户中心")
    public Result info(HttpServletRequest request){
        Result result = Result.getDefaultFalse();
        Serializable id =getUserId();
        if(null == id){
            //result.setMsg("用户id不能为空");
            result.setMsg(ErrorCodeUtil.USER_ID_NOT_BE_NULL);
            return result;
        }
        SysUser user = sysUserService.getById(id);
        if(null == user){
            result.setMsg(ErrorCodeUtil.USER_NOT_EXIST);
            return result;
        }
        SysUserProfile userProfile = sysUserProfileService.getOne(new QueryWrapper<SysUserProfile>().eq("user_id",id));
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user,userInfo);
        if(null != userProfile){
            BeanUtils.copyProperties(userProfile,userInfo);
            if(null == userInfo.getHeadPortrait()){
                String path = FileConfigVo.getMPath(configService.getConfigValueByKey(ConstantsUtil.ConfigItem.USER_DEFAULT_HEAD_PORTRAIT));
                userInfo.setHeadPortrait(path);
                log.info("图片路径：{}",path);
            }else{
                log.info("图片路径：{}",FileConfigVo.getMPath(userProfile.getHeadPortrait()));
                userInfo.setHeadPortrait(FileConfigVo.getMPath(userProfile.getHeadPortrait()));
                userInfo.setThumHeadPortrait(FileConfigVo.getThuPath(userProfile.getHeadPortrait()));
            }

            if(StringUtils.isEmpty(userInfo.getNickName())){
                userInfo.setNickName(configService.getConfigValueByKey(ConstantsUtil.ConfigItem.USER_DEFAULT_NIKNAME));
            }

        }
        user.setPassword(null);
        user.setSalt(null);
        //获取是企业用户，拉取用户角色
        if(user.getType().equals(AccountType.ENTERPRISE.value())){
            String roleName = sysRoleService.queryNameByUserId(user.getId());
            userInfo.setRoleName(roleName == null ? "" : roleName);
        }
        //获取当前用户的微信绑定信息
        UserUnionid userUnionid = userUnionidService.getOne(new QueryWrapper<UserUnionid>().lambda().eq(UserUnionid::getUId, id).eq(UserUnionid::getDeleted, 0));
        if(null == userUnionid){
            userInfo.setWxBindstatus(0);
            userInfo.setWxBindstatus(0);
        }else{
            if (userUnionid.getOpenid() == null ){
                userInfo.setWxBindstatus(0);
            }else{
                userInfo.setWxBindstatus(1);
            }
            if (userUnionid.getUnionid() == null ){
                userInfo.setWxLoginstatus(0);
            }else{
                userInfo.setWxLoginstatus(1);
            }
            userInfo.setWxNickName(userUnionid.getNickName());
            userInfo.setHeadImgurl(userUnionid.getHeadImgurl());
        }
        result = Result.getDefaultTrue();
        result.setData(userInfo);
        return result;
    }

    /**
     * 修改用户信息
     * @param newSysUserVo
     * @return
     */
    @Log(operateContent = "修改用户信息",operateModule = "用户中心")
    @RequestMapping(value = "updateUser",method = RequestMethod.POST)
    public Result updateUser(@RequestBody NewSysUserVo newSysUserVo){

        return sysUserService.updateUser(newSysUserVo);
    }


    /**
     * 重置（修改）密码
     * @param resetPwd
     * @return
     */
//    @Log(operateContent = "重置/修改密码")
//    @RequestMapping(value = "resetPwd",method = RequestMethod.POST)
//    public Result resetPwd(@RequestBody ResetPwd resetPwd){
//        Result result = Result.getDefaultFalse();
//        if(null == resetPwd){
//            //result.setMsg("参数不能为空");
//            result.setMsg(ErrorCodeUtil.PARMA_NOT_BE_NULL);
//            return result;
//        }
//        if(StringUtils.isEmpty(resetPwd.getOldPwd())){
//            //result.setMsg("原密码不能为空");
//            result.setMsg(ErrorCodeUtil.OLD_PWD_NOT_BE_NULL);
//            return result;
//        }
//        if(StringUtils.isEmpty(resetPwd.getNewPwd())){
//            //result.setMsg("新密码不能为空");
//            result.setMsg(ErrorCodeUtil.NEW_PWD_NOT_BE_NULL);
//            return result;
//        }
//        return sysUserService.resetPwd(resetPwd);
//    }



    @Log(operateContent = "修改本人密码",operateModule = "用户中心")
    @RequestMapping(value = "/updatePwd",method = RequestMethod.POST)
    public Result updatePwd(@RequestBody @Valid ResetPwd resetPwd, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new Result().Faild(bindingResult.getFieldError().getDefaultMessage());
        }
        SysUser user = sysUserService.getById(getUserId());
        log.info("修改密码的用户信息：{}",user);
        if(null == user){
            return new Result().Faild(ErrorCodeUtil.USER_NOT_EXIST);
        }
        String oldPwd = Md5.md5(resetPwd.getOldPwd(),user.getSalt());
        log.info("加密后的旧密码：{}",oldPwd);
        if(!oldPwd.equals(user.getPassword())){
            return new Result().Faild(ErrorCodeUtil.OLD_PWD_ERROR);
        }
        //获取新的密码盐
        String salt = RandomUtil.getStr(ConstantUtil.SALT_LENGTH);
        //将新密码加密
        String newPwd = Md5.md5(resetPwd.getNewPwd(),salt);
        SysUser sysUser = SysUser.builder().password(newPwd).salt(salt).updateTime(ConstantsUtil.DF.format(new Date())).build();

        boolean flag = sysUserService.update(sysUser,new UpdateWrapper<SysUser>().eq("id", getUserId()));
        if(flag){
            return Result.getDefaultTrue();
        }
        return Result.getDefaultFalse();
    }

}
