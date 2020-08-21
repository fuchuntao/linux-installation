package cn.meiot.controller.pc;


import cn.meiot.aop.Log;
import cn.meiot.entity.EnterpriseType;
import cn.meiot.entity.SysUser;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.ISysUserService;
import cn.meiot.service.pc.IEnterpriseTypeService;
import cn.meiot.utils.ErrorCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 *  前端控制器  (已经弃用)
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-17
 */
@RestController
@RequestMapping("/enType")
@Slf4j
public class EnterpriseTypeController {

    @Autowired
    private IEnterpriseTypeService enterpriseTypeService;

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 企业类型列表
     * @param current 当前页
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/list")
    @Log(operateContent = "查看公司类型列表",operateModule = "用户中心")
    public Result list(@RequestParam(name = "current",defaultValue = "1")Integer current, @RequestParam(name = "pageSize",defaultValue = "15")Integer pageSize){
        Page<EnterpriseType> page = new Page<EnterpriseType>(current,pageSize);
        IPage<EnterpriseType>  enterpriseTypes= enterpriseTypeService.page(page);
        Result result = Result.getDefaultTrue();
        result.setData(enterpriseTypes);
        return result;
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    @Log(operateContent = "添加公司列表",operateModule = "用户中心")
    public Result add(@RequestBody @Valid EnterpriseType enterpriseType, BindingResult bindingResult){
        log.info("接收参数：{}",enterpriseType);
        if(bindingResult.hasErrors()){
            return new Result().Faild(bindingResult.getFieldError().getDefaultMessage());
        }
        boolean save = enterpriseTypeService.save(enterpriseType);
        if(save){
            return Result.getDefaultTrue();
        }
        return Result.getDefaultFalse();
    }

    /**
     * 修改企业类型
     * @param enterpriseType
     * @return
     */
    @PostMapping(value = "/edit")
    @Log(operateContent = "修改公司列表",operateModule = "用户中心")
    public Result edit(@RequestBody EnterpriseType enterpriseType){
        if(null == enterpriseType){
            return new Result().Faild(ErrorCodeUtil.PRARM_NOT_CAN_BE_NULL);
        }
        if(null == enterpriseType.getId()){
            return new Result().Faild(ErrorCodeUtil.ID_NOT_BE_NULL);
        }
        //修改
        boolean flag = enterpriseTypeService.updateById(enterpriseType);
        if(flag){
            return Result.getDefaultTrue();
        }
        return Result.getDefaultFalse();
    }

    /**
     * 删除企业类型
     * @param id
     * @return
     */
    @DeleteMapping(value = "/delete/{id}")
    @Log(operateContent = "删除公司列表",operateModule = "用户中心")
    public  Result delete(@PathVariable("id") Integer id){
        if(id == null ){
            return new Result().Faild("id不可为空");
        }
        //判断此类型是否已被企业用户使用
        int count = sysUserService.count(new QueryWrapper<SysUser>().eq("enterprise_type", id));
        if(count > 0){
            return Result.faild(ErrorCodeUtil.EN_IS_USE_NOT_DELETE);
            //return new Result().Faild("");
        }

        boolean flag = enterpriseTypeService.removeById(id);
        if(flag){
            return Result.getDefaultTrue();
        }
        return Result.getDefaultFalse();
    }

}
