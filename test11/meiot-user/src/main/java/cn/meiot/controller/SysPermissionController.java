package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.entity.SysPermission;
import cn.meiot.entity.SysUser;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.RolePermissionVo;
import cn.meiot.enums.AccountType;
import cn.meiot.service.ISysPermissionService;
import cn.meiot.service.ISysUserService;
import cn.meiot.utils.ErrorCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-05
 */
@RestController
@RequestMapping("/permission")
public class SysPermissionController extends BaseController {

    @Autowired
    private ISysPermissionService sysPermissionService;

    @Autowired
    private ISysUserService sysUserService;


    /**
     * 通过角色id获取权限列表（平台）
     * @param roleId
     * @return
     */
    @RequestMapping(value = "platformListByRoleId",method = RequestMethod.GET)
    @Log(operateContent = "获取平台用户所有的操作权限列表",operateModule = "用户中心")
    public Result platformListById(@RequestParam("roleId") Integer roleId){
        if(null == roleId){
            Result result = Result.getDefaultFalse();
            //result.setMsg("角色不可为空");
            result.setMsg(ErrorCodeUtil.ROLE_NOT_BE_NULL);
            return result;
        }
        return sysPermissionService.listMenu(getUserId(),roleId);
    }


    /**
     * 获取其他用户所具有的权限列表
     * @param roleId
     * @return
     */
    @RequestMapping(value = "otherListById",method = RequestMethod.GET)
    @Log(operateContent = "获取其他用户所具有的权限列表",operateModule = "用户中心")
    public Result otherListById(@RequestParam("roleId")Integer roleId,@RequestParam("projectId") Integer projectId){
        if(null == roleId){
            Result result = Result.getDefaultFalse();
            result.setMsg(ErrorCodeUtil.TYPE_NOT_BE_NULL);
            return result;
        }
        return sysPermissionService.enterpriseListById(getUserId(),roleId,projectId);
    }



    /**
     * 获取权限列表
     * @param type   权限类型
     * @return
     */
    public Result list(@RequestParam("type") Integer type){

        if(null == type){
            Result result = Result.getDefaultFalse();
            //result.setMsg("类型不可为空");
            result.setMsg(ErrorCodeUtil.TYPE_NOT_BE_NULL);
            return result;
        }
        return sysPermissionService.list(getUserId(),type);
    }

    /**
     * 获取所有的权限列表
     * @param type 1:平台   2：企业
     * @return
     */
   /* @GetMapping(value = "/queryList")
    public Result queryList(@RequestParam("type") Integer type){
        Result result = Result.getDefaultTrue();

        Long userId = getUserId();
        SysUser user = sysUserService.getById(userId);
        if(!user.getType().equals(AccountType.PLATFORM.value())  && !type.equals(AccountType.ENTERPRISE.value())){
            return new Result().Faild("越权操作");
        }
        List<SysPermission> list = sysPermissionService.list(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getType, AccountType.ENTERPRISE.value()));
        result.setData(list);
        return result;
    }*/

}
