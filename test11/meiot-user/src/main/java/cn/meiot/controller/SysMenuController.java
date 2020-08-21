package cn.meiot.controller;


import cn.meiot.aop.Log;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.ISysMenuService;
import cn.meiot.service.ISysUserService;
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
@RequestMapping("/sysmenu")
public class SysMenuController extends BaseController {

    @Autowired
    private ISysMenuService sysMenuService;

    @Autowired
    private ISysUserService sysUserService;



    /**
     * 查看本人下所拥有的模块
     * @return
     */
    @RequestMapping(value = "/platList",method = RequestMethod.GET)
    @Log(operateContent = "查看模块",operateModule = "用户中心")
    public Result platList(){
        //Integer userId = getUserId();
        Long userId = getUserId();
        return sysMenuService.getPlatList(userId,null);
    }

    /**
     * 查询企业账户所有用的菜单列表
     * @return
     */
    @RequestMapping(value = "/enterpriseList",method = RequestMethod.GET)
    public Result enterpriseList(){
        return sysMenuService.getPlatList(getUserId(),getProjectId());
    }


    /**
     * 获取指定类型的所有模块
     * @return
     */
    @GetMapping(value = "listByType")
    @Log(operateContent = "查看执行类型的模块列表")
    public Result listByType(@RequestParam("type") Integer type){
        Long userId = getUserId();
        return sysMenuService.listByType(userId,type,getProjectId());
    }

}
