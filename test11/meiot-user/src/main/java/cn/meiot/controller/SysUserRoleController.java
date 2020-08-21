package cn.meiot.controller;


import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.SysUserRoleVo;
import cn.meiot.service.ISysRoleService;
import cn.meiot.service.ISysUserRoleService;
import cn.meiot.utils.ErrorCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  用户-角色管理
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-02
 */
@RestController
@RequestMapping("/user-role")
@Slf4j
public class SysUserRoleController extends BaseController {

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private ISysRoleService sysRoleService;


    @PutMapping(value = "list/{userId}")
    public Result listByUserId(@PathVariable("userId") Long userId){
        if(null == userId){
            Result result = Result.getDefaultFalse();
            log.info("请选择你需要查询的用户id");
            //result.setMsg("请选择你需要查询的用户id");
            result.setMsg(ErrorCodeUtil.USER_ID_NOT_BE_NULL);
            return result;
        }
        return sysUserRoleService.getListByUserId(getUserId(),userId);
    }

    /**
     * 新增角色（平台用户）
     * @param sysUserRoleVo
     * @return
     */
    @RequestMapping(value = "add",method = RequestMethod.POST)
    public Result add(@RequestBody SysUserRoleVo sysUserRoleVo){
        Result result = Result.getDefaultFalse();
        if(null == sysUserRoleVo){
            //result.setMsg("参数不能为空");
            result.setMsg(ErrorCodeUtil.PARMA_NOT_BE_NULL);
            return result;
        }
        sysUserRoleVo.setUserId(getUserId());
        if(null == sysUserRoleVo.getType()){
            //result.setMsg("类型不可为空");
            result.setMsg(ErrorCodeUtil.TYPE_NOT_BE_NULL);
            return result;
        }
        return sysUserRoleService.add(sysUserRoleVo,getUserId());
    }


}
