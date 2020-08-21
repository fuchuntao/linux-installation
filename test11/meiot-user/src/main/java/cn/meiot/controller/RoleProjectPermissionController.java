package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.entity.vo.PermissionVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.IRoleProjectPermissionService;
import cn.meiot.utils.ErrorCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-02
 */
@RestController
@RequestMapping("/rp")
@Slf4j
public class RoleProjectPermissionController extends BaseController{

    @Autowired
    private IRoleProjectPermissionService roleProjectPermissionService;


    /**
     * 获取角色项目权限列表
     * @param roleId 角色id
     * @param projectId 项目id
     * @return
     */
    @GetMapping(value = "list")
    @Log(operateContent = "通过角色id获取项目权限列表",operateModule = "用户中心")
    public Result list(@RequestParam(value = "roleId") Integer roleId, @RequestParam("projectId") Integer projectId){
        if(null == roleId || null == projectId){
            log.info("角色id：{}，项目id：{}",roleId,projectId);
            return new Result().Faild(ErrorCodeUtil.REQ_PRARM_ERROR);
        }
        return roleProjectPermissionService.getList(roleId,projectId,getUserId());
    }

    /**
     * 设置权限
     * @param permissionVo
     * @param bindingResult
     * @return
     */
    @PostMapping(value = "setPermission")
    @Log(operateContent = "给项目设置权限",operateModule = "用户中心")
    public Result setPermission(@RequestBody @Valid PermissionVo permissionVo, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new Result().Faild(bindingResult.getFieldError().getDefaultMessage());
        }
        if(null == permissionVo.getProjectId()){
            return new Result().Faild(ErrorCodeUtil.PROJECT_ID_NOT_BE_BE_NULL);
        }
        return roleProjectPermissionService.setPermission(permissionVo,getUserId());
    }

}
