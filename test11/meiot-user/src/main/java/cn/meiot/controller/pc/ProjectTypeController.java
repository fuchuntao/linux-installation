package cn.meiot.controller.pc;


import cn.meiot.aop.Log;
import cn.meiot.entity.Project;
import cn.meiot.entity.ProjectType;
import cn.meiot.entity.vo.DeleteProjectVo;
import cn.meiot.entity.vo.PermissionVo;
import cn.meiot.entity.vo.ProjectTypeVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.pc.IProjectService;
import cn.meiot.service.pc.IProjectTypeService;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.QueueConstantUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-19
 */
@RestController
@RequestMapping("/prtype")
public class ProjectTypeController {

    @Autowired
    private IProjectTypeService projectTypeService;

    @Autowired
    private IProjectService projectService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 项目类型列表
     *
     * @param current  当前页
     * @param pageSize 每页展示多少行
     * @return
     */
    @GetMapping(value = "/list")
    @Log(operateContent = "获取项目类型列表",operateModule = "用户中心")
    public Result list(@RequestParam(name = "current", defaultValue = "1") Integer current,
                       @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize) {
        Result result = Result.getDefaultTrue();
        Page<ProjectType> page = new Page<>(current, pageSize);
        IPage<ProjectType> iPage = projectTypeService.page(page);
        result.setData(iPage);
        return result;
    }

    /**
     * 新增项目类型
     *
     * @param projectTypeVo
     * @return
     */
    @PostMapping(value = "/add")
    @Log(operateContent = "新增项目类型",operateModule = "用户中心")
    public Result add(@RequestBody @Valid ProjectTypeVo projectTypeVo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new Result().Faild(bindingResult.getFieldError().getDefaultMessage());
        }
        ProjectType projectType = new ProjectType();
        projectType.setName(projectTypeVo.getName());
        boolean save = projectTypeService.save(projectType);
        if (save) {
            PermissionVo permissionVo = new PermissionVo();
            permissionVo.setPerimissions(projectTypeVo.getPermissions());
            permissionVo.setId(projectType.getId());
            projectTypeService.setPermission(permissionVo);
            return Result.getDefaultTrue();
        }
        return Result.getDefaultFalse();
    }

    /**
     * 新增项目类型
     *
     * @param projectTypeVo
     * @return
     */
    @PostMapping(value = "/update")
    @Log(operateContent = "修改项目类型",operateModule = "用户中心")
    public Result update(@RequestBody @Valid ProjectTypeVo projectTypeVo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new Result().Faild(bindingResult.getFieldError().getDefaultMessage());
        }
        if (null == projectTypeVo.getId()) {
            return new Result().Faild(ErrorCodeUtil.TYPE_ID_NOT_BE_NULL);
        }
        //判断项目类型是否存在
        int count = projectTypeService.count(new QueryWrapper<ProjectType>().eq("id", projectTypeVo.getId()));
        if (count == 0) {
            return new Result().Faild(ErrorCodeUtil.PROJECT_TYPE_IS_NOT_EXIST);
        }
        ProjectType projectType = new ProjectType();
        projectType.setId(projectTypeVo.getId());
        projectType.setName(projectTypeVo.getName());
        boolean save = projectTypeService.updateById(projectType);
        if (save) {
            PermissionVo permissionVo = new PermissionVo();
            permissionVo.setPerimissions(projectTypeVo.getPermissions());
            permissionVo.setId(projectType.getId());
            projectTypeService.setPermission(permissionVo);

            return Result.getDefaultTrue();
        }
        return Result.getDefaultFalse();
    }


    /**
     * 删除项目类型
     *
     * @param deleteProjectVo
     * @return
     */
    @PostMapping(value = "delete")
    @Log(operateContent = "删除项目类型",operateModule = "用户中心")
    public Result delete(@RequestBody DeleteProjectVo deleteProjectVo) {
        if (null == deleteProjectVo.getIds()) {
            return new Result().Faild("类型id不能为空");
        }
        //判断当前类型是否绑定了项目
        int count = projectService.count(new QueryWrapper<Project>().lambda().in(Project::getProjectType, deleteProjectVo.getIds()));
        if (count > 0) {
            if(deleteProjectVo.getIds().size() > 1 ){
                return new Result().Faild(ErrorCodeUtil.DELETE_PROJECT_TYPE_IS_USE_NOT_DELETE);
            }
            return new Result().Faild(ErrorCodeUtil.DELETE_PROJECT_TYPE_IS_USE_NOT_DELETE);
        }
        boolean flag = projectTypeService.removeByIds(deleteProjectVo.getIds());
        if (flag) {
            return Result.getDefaultTrue();
        }
        return Result.getDefaultFalse();
    }


    /**
     * 根据项目类型id查询权限信息（已放弃）
     *
     * @param id 项目权限id
     * @return
     */
    @GetMapping(value = "/typePermissionlist")
    @Log(operateContent = "查询项目类型的权限",operateModule = "用户中心")
    public Result typePermissionlist(@RequestParam("id") Integer id) {
        return projectTypeService.typePermissionlist(id);
    }

    /**
     * 获取项目类型的权限列表  （目前正在使用）
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/permissionList")
    @Log(operateContent = "查询项目类型的权限",operateModule = "用户中心")
    public Result checkedPer(@RequestParam(value = "id", defaultValue = "") Integer id) {
        return projectTypeService.permissionList(id);
    }


    /**
     * 设置项目类型的权限信息(已弃用)
     *
     * @param permissionVo
     * @return
     */
    @PostMapping(value = "/setPermission")
    @Log(operateContent = "设置项目类型权限",operateModule = "用户中心")
    public Result setPermission(@RequestBody @Valid PermissionVo permissionVo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new Result().Faild(bindingResult.getFieldError().getDefaultMessage());
        }
        return projectTypeService.setPermission(permissionVo);
    }

}
