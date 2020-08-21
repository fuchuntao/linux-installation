package cn.meiot.controller.pc;


import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.Project;
import cn.meiot.entity.SysUser;
import cn.meiot.entity.bo.EnterpriseBo;
import cn.meiot.entity.bo.ProjectTypeBo;
import cn.meiot.entity.vo.PageVo;
import cn.meiot.entity.vo.PermissionVo;
import cn.meiot.entity.vo.QueryProjectVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.ISysUserService;
import cn.meiot.service.pc.IEnterpriseService;
import cn.meiot.service.pc.IProjectService;
import cn.meiot.service.pc.IProjectTypeService;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.VerifyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-19
 */
@RestController
@RequestMapping("/project")
@Slf4j
public class ProjectController extends BaseController {

    @Autowired
    private IProjectService projectService;

    @Autowired
    private IEnterpriseService enterpriseService;

    @Autowired
    private IProjectTypeService projectTypeService;

    @Autowired
    private ISysUserService sysUserService;


    /**
     *
     * @param current  当前页
     * @param pageSize 每页展示多少行
     * @param keyword 关键字
     * @param enterpriseId  企业id
     * @param type  企业类型
     * @return
     */
    @GetMapping(value = "/list")
    @Log(operateContent = "查询项目列表",operateModule = "用户中心")
    public Result list(@RequestParam(name = "current", defaultValue = "1") Integer current, @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize,
                       @RequestParam(name = "keyword", defaultValue = "")String keyword,
                       @RequestParam(name = "enterpriseId", defaultValue = "")Integer enterpriseId,
                       @RequestParam(name = "type", defaultValue = "")Integer type){
        PageVo pageVo = new PageVo(current,pageSize);
        QueryProjectVo projectVo = QueryProjectVo.builder().keyword(keyword).enterpriseId(enterpriseId).type(type).pageVo(pageVo).build();
        return projectService.getList(projectVo);
    }

    /**
     * 添加项目
     * @param project
     * @param bindingResult
     * @return
     */
    @PostMapping(value = "/add")
    @Log(operateContent = "新增项目",operateModule = "用户中心")
    public Result add(@RequestBody @Valid Project project, BindingResult bindingResult){
        log.info("接收的参数：{}",project);
        if(bindingResult.hasErrors()){
            return new Result().Faild(bindingResult.getFieldError().getDefaultMessage());
        }
        project.setId(null);
        if(!VerifyUtil.verifyEmail(project.getEmail())){
            return new Result().Faild(ErrorCodeUtil.EMAIL_ERROR);
        }
        return projectService.addProject(project);
    }


    /**
     * 修改项目
     * @param project
     * @param bindingResult
     * @return
     */
    @PostMapping(value = "update")
    @Log(operateContent = "修改项目",operateModule = "用户中心")
    public Result update(@RequestBody @Valid Project project, BindingResult bindingResult){
        log.info("接收的参数：{}",project);
        if(bindingResult.hasErrors()){
            return new Result().Faild(bindingResult.getFieldError().getDefaultMessage());
        }
        if(null == project.getId()){
            return new Result().Faild(ErrorCodeUtil.PROJECT_ID_NOT_BE_BE_NULL);
        }
        if(!VerifyUtil.verifyEmail(project.getEmail())){
            return Result.faild(ErrorCodeUtil.EMAIL_ERROR);
        }
        return projectService.updateProject(project);
    }


    /**
     * 查询搜索的条件   企业类型and企业列表
     * @return
     */
    @GetMapping(value = "/filterCondition")
    public Result filterCondition(){
        Result result = Result.getDefaultTrue();
        //获取所有企业
        List<EnterpriseBo> enterpriseBos = enterpriseService.getList();
        //获取所有项目类型
        List<ProjectTypeBo> projectTypeBos = projectTypeService.getList();

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("enterprise",enterpriseBos);
        map.put("projectType",projectTypeBos);
        result.setData(map);
        return result;
    }


    /**
     * 根据项目id查询权限信息
     * @param id 项目权限id
     * @return
     */
    @GetMapping(value = "/projectPermissionlist")
    @Log(operateContent = "根据项目id查询权限信息",operateModule = "用户中心")
    public Result projectPermissionlist(@RequestParam("id") Integer id){
        return projectService.projectPermissionlist(id);
    }

    /**
     * 设置项目的权限信息
     * @param permissionVo
     * @return
     */
    @PostMapping(value = "/setPermission")
    @Log(operateContent = "设置项目权限",operateModule = "用户中心")
    public Result setPermission(@RequestBody @Valid PermissionVo permissionVo, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new Result().Faild(bindingResult.getFieldError().getDefaultMessage());
        }
        return projectService.setPermission(permissionVo);
    }

    /**
     * 获取当前用户所拥有的项目列表
     * @return
     */
    @GetMapping(value = "/getListById")
    @Log(operateContent = "获取本人的项列表",operateModule = "用户中心")
    public Result getListById(){

        return projectService.getListById(getUserId());
    }


    /**
     *导出项目列表
     * @param keyword 关键字
     * @param enterpriseId  公司id
     * @param type 项目类型
     * @return
     */
    @Log(operateContent = "导出项目列表",operateModule = "用户中心")
    @GetMapping(value = "/exportProject")
    public Result exportProject(@RequestParam(name = "keyword", defaultValue = "")String keyword,
                                @RequestParam(name = "enterpriseId", defaultValue = "")Integer enterpriseId,
                                @RequestParam(name = "type", defaultValue = "")Integer type,
                                HttpServletResponse response){
        QueryProjectVo projectVo = QueryProjectVo.builder().keyword(keyword).enterpriseId(enterpriseId).type(type).build();
        return projectService.exportProject(projectVo,response);
    }

    /**
     * 获取当前企业所拥有的项目列表
     * @return
     */
    @GetMapping(value = "/queryList")
    public Result queryList(){
        return projectService.queryList(getUserId());
    }

    /**
     * 切换项目id
     * @param projectId
     * @return
     */
    @GetMapping(value = "/cutProjectId")
    @Log(operateContent = "切换项目",operateModule = "用户中心")
    public Result cutProjectId(@RequestParam("projectId") Integer projectId){
        if(null == projectId){
            return new Result().Faild(ErrorCodeUtil.PROJECT_ID_NOT_BE_BE_NULL);
        }
        return projectService.cutProjectId(getUserId(),projectId);
    }

}
