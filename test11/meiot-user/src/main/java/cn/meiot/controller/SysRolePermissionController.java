package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.RolePermissionVo;
import cn.meiot.service.ISysRolePermissionService;
import cn.meiot.utils.ErrorCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-02
 */
@RestController
@RequestMapping("/permission")
public class SysRolePermissionController extends BaseController {

    @Autowired
    private ISysRolePermissionService sysRolePermissionService;



    /**
     * 管理平台添加权限
     * @param rolePermissionVo
     * @return
     */
    @RequestMapping(value = "/addPermission",method = RequestMethod.POST)
    @Log(operateContent = "平台账户角色赋权",operateModule = "用户中心")
    public Result addPlatform(@RequestBody RolePermissionVo rolePermissionVo){
        Result result = Result.getDefaultFalse();
        if(null == rolePermissionVo){
           // result.setMsg("参数不可为空");
            result.setMsg(ErrorCodeUtil.PARMA_NOT_BE_NULL);
            return result;
        }
        return sysRolePermissionService.addPlatform(rolePermissionVo,getUserId());
    }


    /**
     * 添加企业用户权限
     * @param rolePermissionVo
     *
     *
     * @return
     */
//    @RequestMapping(value = "/addEnPermission",method = RequestMethod.POST)
//    @Log(operateContent = "企业账号赋权")
//    public Result addEnPermission(@RequestBody RolePermissionVo rolePermissionVo){
//        Result result = Result.getDefaultFalse();
//        if(null == rolePermissionVo){
//            // result.setMsg("参数不可为空");
//            result.setMsg(ErrorCodeUtil.PARMA_NOT_BE_NULL);
//            return result;
//        }
//        return sysRolePermissionService.addEnPermission(rolePermissionVo,getUserId());
//    }



}
