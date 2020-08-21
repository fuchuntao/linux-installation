package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.entity.SysRole;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SysRoleVo;
import cn.meiot.enums.AccountType;
import cn.meiot.service.ISysRoleService;
import cn.meiot.utils.ErrorCodeUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  角色管理
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-02
 */
@RestController
@RequestMapping("/role")
@Slf4j
public class SysRoleController extends BaseController {

    @Autowired
    private ISysRoleService sysRoleService;



    /**
     * 根据类型查看角色
     * @param type  1:平台   2：企业
     * @return
     */
    @RequestMapping(value = "/all",method = RequestMethod.GET)
    @Log(operateContent = "查看角色列表",operateModule = "用户中心")
    public Result all(@RequestParam("type") Integer type,@RequestParam(name = "current",defaultValue = "1") Integer current,
                      @RequestParam(name = "pageSize",defaultValue = "15") Integer pageSize,
                      @RequestParam(value = "keyword",defaultValue = "") String keyword){
        Long userId = getUserId();
        Page<SysRole> page = new Page(current,pageSize);
        return sysRoleService.getList(userId,type,keyword,page);
    }

    /**
     * 新增角色
     * @param sysRoleVo
     * @return
     */
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @Log(operateContent = "添加角色",operateModule = "用户中心")
    public Result addRole(@RequestBody SysRoleVo sysRoleVo, BindingResult bindResult){
        if(bindResult.hasErrors()){
            Result result =Result.getDefaultFalse();
            result.setMsg(bindResult.getFieldError().getDefaultMessage());
            return result;
        }
        sysRoleVo.setUserId(getUserId());
        return sysRoleService.saveRole(sysRoleVo);
    }

    /**
     * 修改角色
     * @param sysRoleVo
     * @return
     */
    @RequestMapping(value = "edit",method = RequestMethod.POST)
    @Log(operateContent = "修改角色",operateModule = "用户中心")
    public Result edit(@RequestBody SysRoleVo sysRoleVo, BindingResult bindResult){
        Result result = Result.getDefaultTrue();
        if(bindResult.hasErrors()){
            result.setMsg(bindResult.getFieldError().getDefaultMessage());
            return result;
        }
        if(null == sysRoleVo.getId()){
            //result.setMsg("角色id不能为空");
            result.setMsg(ErrorCodeUtil.ROLE_NOT_BE_NULL);
            return result;
        }
        sysRoleVo.setUserId(getUserId());
        return sysRoleService.edit(sysRoleVo);
    }

    /**
     * 删除角色
     * @return
     */
    @RequestMapping(value = "/deleteRole",method = RequestMethod.POST)
    @Log(operateContent = "删除角色",operateModule = "用户中心")
    public  Result deleteRole(@RequestBody SysRoleVo sysRoleVo) throws Exception {
        if(null == sysRoleVo || null == sysRoleVo.getId()){
            Result result = Result.getDefaultFalse();
            //result.setMsg("id不可为空");
            result.setMsg(ErrorCodeUtil.ROLE_NOT_BE_NULL);
            return result;
        }
        return sysRoleService.deleteRole(sysRoleVo.getId(),getUserId());
    }



}
