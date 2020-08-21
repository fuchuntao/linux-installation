package cn.meiot.controller.pc;

import cn.meiot.aop.Log;
import cn.meiot.common.ErrorCode;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.bo.ExportEnUserBo;
import cn.meiot.entity.bo.ExportSingleUserBo;
import cn.meiot.entity.vo.*;
import cn.meiot.enums.AccountType;
import cn.meiot.service.ISysUserService;
import cn.meiot.service.pc.PcUserService;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.ExcelUtils;
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

@RestController
@Slf4j
@RequestMapping("/pc")
public class PcUserController extends BaseController {

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private PcUserService pcUserService;

    /**
     * 查询企业的主账户
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/enterpriseList", method = RequestMethod.GET)
    @Log(operateContent = "查询用户列表",operateModule = "用户中心")
    public Result enterpriseList(@RequestParam(name = "currentPage", defaultValue = "1") Integer currentPage, @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize
    ,@RequestParam(name = "keyword", defaultValue = "")String keyword) {
        log.info("查询开始============>");
        currentPage = (currentPage - 1) * pageSize;
        return sysUserService.getEnterpriseList(getUserId(), currentPage, pageSize,keyword);
    }

    /**
     * 查询个人账户列表
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/personList", method = RequestMethod.GET)
    @Log(operateContent = "查询用户列表",operateModule = "用户中心")
    public Result personList(@RequestParam(name = "currentPage", defaultValue = "1") Integer currentPage, @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize
            ,@RequestParam(name = "keyword", defaultValue = "")String keyword) {
        log.info("查询开始============>");
        currentPage = (currentPage - 1) * pageSize;
        return sysUserService.getPersonList(getUserId(), currentPage, pageSize,keyword);
    }


    /**
     * 获取账号管理列表
     * @param currentPage
     * @param pageSize
     * @param keyword  关键字
     * @param type 类型  1:平台   2企业
     * @return
     */
    @RequestMapping(value = "adminList", method = RequestMethod.GET)
    @Log(operateContent = "查询账户列表",operateModule = "用户中心")
    public Result adminList(@RequestParam(name = "currentPage", defaultValue = "1") Integer currentPage, @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize
            ,@RequestParam(name = "keyword", defaultValue = "")String keyword,
                            @RequestParam(name = "type",required = true)Integer type) {
        PageVo page = new PageVo(currentPage, pageSize);
        if(type == null){
            return Result.faild(ErrorCodeUtil.LACK_REQ_PRARM);
            //return new Result().Faild("缺少请求参数");
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("type",AccountType.PLATFORM.value());
        map.put("keyword",keyword);
        map.put("page",page);
        map.put("type",type);
        return sysUserService.getAdminList(map,getUserId());
    }


    /**
     * 新增账户
     *
     * @param sysUserVo
     * @param bindingResult
     * @return
     */
    @PostMapping(value = "/addSysUser")
    @Log(operateContent = "新增账户",operateModule = "用户中心")
    public Result addSysUser(@RequestBody @Valid SysUserVo sysUserVo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Result result = Result.getDefaultFalse();
            result.setMsg(bindingResult.getFieldError().getDefaultMessage());
            return result;
        }
        if(!VerifyUtil.verifyPhone(sysUserVo.getAccount())){
            return new Result().Faild(ErrorCodeUtil.ACCOUNT_TYPE_NOT_BE_NULL);
        }
        if(!VerifyUtil.verifyEmail(sysUserVo.getEmail())){
            return new Result().Faild(ErrorCodeUtil.EMAIL_ERROR);
        }
        return sysUserService.addSysUser(sysUserVo,getUserId());
    }


    /**
     * 修改账户
     *
     * @param sysUserVo
     * @param bindingResult
     * @return
     */
    @PostMapping(value = "/updateSysUser")
    @Log(operateContent = "修改账户",operateModule = "用户中心")
    public Result updateSysUser(@RequestBody SysUserVo sysUserVo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Result result = Result.getDefaultFalse();
            result.setMsg(bindingResult.getFieldError().getDefaultMessage());
            return result;
        }
        if(!VerifyUtil.verifyPhone(sysUserVo.getAccount())){
            return Result.faild(ErrorCodeUtil.ACCOUNT_TYPE_NOT_BE_NULL);
        }

        return sysUserService.updateSysUser(sysUserVo,getUserId());
    }

    /**
     * 删除企业账户
     * @param userVo
     * @return
     */
    @PostMapping(value = "/deleteEnUser")
    @Log(operateContent = "删除企业账号",operateModule = "用户中心")
    public Result deleteEnUser(@RequestBody UserVo userVo){
        if(null == userVo || null == userVo.getUserId()){
            return new Result().Faild(ErrorCodeUtil.SELECT_DELETE_USER_PLEASE);
        }
        return sysUserService.deleteEnUser(userVo.getUserId());
    }

    /**
     * 禁用账号(只能平台使用)
     * @return
     */
    @PostMapping(value = "/forbidden")
    @Log(operateContent = "禁用企业账号",operateModule = "用户中心")
    public Result forbidden(@RequestBody UserVo userVo){
        if(null == userVo || null == userVo.getUserId()){
            return Result.faild(ErrorCodeUtil.SELECT_FORBIDDEN_USER_PLEASE);
           // return new Result().Faild(ErrorCodeUtil.SELECT_FORBIDDEN_USER_PLEASE);
        }
        return pcUserService.forbidden(userVo.getUserId());
    }

    /**
     * 启用账号(只能平台使用)
     * @param userVo
     * @return
     */
    @PostMapping(value = "/enable")
    @Log(operateContent = "启用企业账号",operateModule = "用户中心")
    public Result enable(@RequestBody UserVo userVo){
        if(null == userVo || null == userVo.getUserId()){
            //请选择需要启用的用户
            return new Result().Faild(ErrorCodeUtil.SELECT_ENABLE_USER_PLEASE);
        }
        return pcUserService.enable(userVo.getUserId());
    }

    /**
     * 重置密码
     * @param userVo
     * @return
     */
    @PostMapping(value = "/resetPwd")
    @Log(operateContent = "修改密码",operateModule = "用户中心")
    public Result resetPwd(@RequestBody UserVo userVo){
        if(null == userVo || null == userVo.getUserId()){
            return Result.faild(ErrorCodeUtil.SELECT_USER_PLEASE);
        }
        return pcUserService.resetPwd(getUserId(),userVo.getUserId());
    }



    /**
     *
     * @return
     */
    @PostMapping(value = "/deleteUser")
    @Log(operateContent = "删除管理账号",operateModule = "用户中心")
    public Result deleteUser(@RequestBody UserVo userVo){
        if(null == userVo.getIds() || userVo.getIds().size() == 0 ){
            return Result.faild(ErrorCodeUtil.SELECT_CHECK_USER_PLEASE);
        }
        return pcUserService.deleteUser(getUserId(),userVo.getIds());

    }



    /**
     * 添加企业用户
     *
     * @param enterpriseUserVo
     * @param bindingResult
     * @return
     */
    @PostMapping(value = "/addEnUser")
    @Log(operateContent = "新增企业用户",operateModule = "用户中心")
    public Result addEnUser(@RequestBody @Valid EnterpriseUserVo enterpriseUserVo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new Result().Faild(bindingResult.getFieldError().getDefaultMessage());
        }

        if(!VerifyUtil.verifyPhone(enterpriseUserVo.getAccount())){
            return Result.faild(ErrorCodeUtil.ACCOUNT_TYPE_NOT_BE_NULL);
        }
        if(!VerifyUtil.verifyEmail(enterpriseUserVo.getEmail())){
            return new Result().Faild(ErrorCodeUtil.EMAIL_ERROR);
        }

        //保存
        return pcUserService.addEnUser(enterpriseUserVo,getUserId());
    }

    /**
     * 修改企业用户
     *
     * @param enterpriseUserVo
     * @param bindingResult
     * @return
     */
    @PostMapping(value = "/updateEnUser")
    @Log(operateContent = "修改企业用户",operateModule = "用户中心")
    public Result updateEnUser(@RequestBody @Valid EnterpriseUserVo enterpriseUserVo, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new Result().Faild(bindingResult.getFieldError().getDefaultMessage());
        }
        if (null == enterpriseUserVo.getId()) {
            return Result.faild(ErrorCodeUtil.UPDATE_USER_ID_NOT_BE_NULL);
        }
        if(!VerifyUtil.verifyPhone(enterpriseUserVo.getAccount())){
            return Result.faild(ErrorCodeUtil.ACCOUNT_TYPE_NOT_BE_NULL);
        }
        if(!VerifyUtil.verifyEmail(enterpriseUserVo.getEmail())){
            return Result.faild(ErrorCodeUtil.EMAIL_ERROR);
        }

        //修改
        return pcUserService.updateEnUser(enterpriseUserVo);
    }

    /**
     *导出企业账号
     * @param keyword
     * @return
     */
    @GetMapping(value = "/exportEnUser")
    @Log(operateContent = "导出企业用户",operateModule = "用户中心")
    public Result exportEnUser(@RequestParam(name = "keyword", defaultValue = "")String keyword
    , HttpServletResponse response){
        log.info("查询开始============>");

        ExportUserVo exportUserVo = ExportUserVo.builder().keyword(keyword).type(AccountType.ENTERPRISE.value())
                .belongId(0).build();
        List<ExportEnUserBo> exportEnUserBos = pcUserService.getExportEnUser(exportUserVo);
        log.info("需要导出的内容长度为：{}",exportEnUserBos.size());
        pcUserService.exprot(exportEnUserBos,"企业用户信息列表",response);
        return null;
    }

    /**
     *导出个人账号
     * @param keyword
     * @return
     */
    @GetMapping(value = "/exportsingleUser")
    @Log(operateContent = "导出个人用户",operateModule = "用户中心")
    public Result exportsingleUser(@RequestParam(name = "keyword", defaultValue = "")String keyword
            , HttpServletResponse response){
        log.info("查询开始============>");
        ExportUserVo exportUserVo = ExportUserVo.builder().keyword(keyword).type(AccountType.PERSONAGE.value())
                .fileName("个人用户列表").build();
        List<ExportSingleUserBo> exportsingleUser = pcUserService.getExportsingleUser(exportUserVo);
        log.info("需要导出的内容长度为：{}",exportsingleUser.size());
        //导出
        ExcelUtils.export(exportsingleUser,"个人用户信息列表",response,ExportSingleUserBo.class);
//        List<ExportEnUserBo> exportUserBos = pcUserService.getExportUser(exportUserVo);
//        pcUserService.exprot(exportUserBos,"个人列表",response);
        return null;
    }

    /**
     *查出该项目所有电工
     * @param
     * @return
     */
    @GetMapping(value = "/queryElectrician")
    @Log(operateContent = "查询个人",operateModule = "用户中心")
    public Result queryElectrician(){
        Integer projectId = getProjectId();
        String permission = ConstantsUtil.SysPermission.ELECTRICIAN;
        List list = pcUserService.queryElectrician(projectId,permission);
        return Result.OK(list);
    }
}
