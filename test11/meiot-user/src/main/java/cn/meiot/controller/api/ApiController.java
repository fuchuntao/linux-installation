package cn.meiot.controller.api;


import cn.meiot.entity.bo.UserNumBo;
import cn.meiot.enums.AccountType;
import cn.meiot.service.api.ApiService;
import cn.meiot.service.pc.IProjectService;
import cn.meiot.utils.WxUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {



    private ApiService apiService;

    private IProjectService projectService;

    private WxUtil wxUtil;

    public ApiController(ApiService apiService,IProjectService projectService, WxUtil wxUtil){
        this.apiService = apiService;
        this.projectService = projectService;
        this.wxUtil = wxUtil;
    }

    /**
     * 通过项目id查询创建时间
     * @param projectId
     * @return
     */
    @RequestMapping(value = "/getProjectDateByProjectId",method = RequestMethod.GET)
   public Long getProjectDateByProjectId(@RequestParam("projectId") Integer projectId){
        log.info("查询的项目id：{}",projectId);
        if(null == projectId){
            log.info("需要查询的项目id为空");
            return null;
        }
        return apiService.getProjectDateByProjectId(projectId);
   }

    /**
     * 计算总项目数量
     * @return
     */
    @RequestMapping(value = "/queryProjectTotal",method = RequestMethod.GET)
    public Integer queryProjectTotal(){
        int count = projectService.count();
        log.info("项目总数量：{}",count);
        return  count;
    }

    /**
     * 通过项目id查询项目名称
     * @param projectId
     * @return
     */
    @RequestMapping(value = "/queryProjectNameById",method = RequestMethod.GET)
    public String  queryProjectNameById(@RequestParam("projectId") Integer projectId){
        log.info("通过项目id查询项目名称，查询的项目id：{}",projectId);
        if(null == projectId){
            log.info("需要查询的项目id为空");
            return null;
        }
        return apiService.queryProjectNameById(projectId);

    }

    /**
     * 通过项目id查询企业名和项目名
     * @param projectId
     * projectName 项目名
     * enterpriseName 企业名
     */
    @RequestMapping(value = "/queryProNameByProjectId",method = RequestMethod.GET)
    public Map<String,String> queryProNameByProjectId(@RequestParam("projectId") Integer projectId){

        return apiService.queryProNameByProjectId(projectId);
    }

    /**
     * 获取用户数量
     * @return
     */
    @RequestMapping(value = "/getUserNum",method = RequestMethod.GET)
    public UserNumBo getUserNum(){

        return apiService.getUserNum();
    }

    /**
     * 根据类型获取用户id列表
     * @param type 2:企业用户    5：个人用户     空：企业+个人
     * @return
     */
    @RequestMapping(value = "/getUserByType",method = RequestMethod.GET)
    public List<Long> getUserByType(@RequestParam(value = "type",required = false) Integer type){
        if(type == null){
            type = 0;
        }else{
            if(!AccountType.ENTERPRISE.value().equals(type) && !AccountType.PERSONAGE.value().equals(type)){
                log.info("传入的类型不符合要求，类型：{}",type);
                return null;
            }
        }
        return apiService.getUserIdByType(type);
    }

    /**
     * 获取accessToken
     * @return
     */
    @RequestMapping(value = "/accessToken",method = RequestMethod.GET)
    public String getAccessToken(){
         return wxUtil.getAccessToken();
    }

    /**
     * 获取用户openid
     * @return
     */
    @RequestMapping(value = "/openid",method = RequestMethod.GET)
    public String getOpenid(@RequestParam(value = "userId",required = true) Long userId){
        return apiService.getOpenid(userId);
    }


    /**
     * 通过用户id获取用户类型
     * @param userId
     * @return
     */
    @RequestMapping(value = "/getTypeByUserId",method = RequestMethod.GET)
    public Integer getTypeByUserId(@RequestParam("userId") Long userId){

        return apiService.getTypeByUserId(userId);
    }

    /**
     * 通过用户id获取角色id
     * @param userId
     * @return
     */
    @RequestMapping(value = "/getRoleIdByUserId",method = RequestMethod.GET)
    public List<Integer> getRoleIdByUserId(@RequestParam("userId") Long userId){

        return apiService.getRoleIdByUserId(userId);
    }


    /**
     * 通过用户id和权限唯一标识校验用户是否存在此权限
     * @param userId  用户id
     * @param permission  权限唯一标识
     * @param projectId  项目id
     * @return
     */
    @RequestMapping(value = "/checkPermission",method = RequestMethod.GET)
    public boolean checkPermission(@RequestParam("userId") Long userId,@RequestParam("permission") String permission,
                                   @RequestParam("projectId") Integer projectId){

        return apiService.checkPermission(userId,permission,projectId);
    }


    /**
     * 通过用户id查询用户昵称
     * @param userId
     * @return
     */
    @RequestMapping(value = "/getNiknameByUserId",method = RequestMethod.GET)
    public String getNiknameByUserId(@RequestParam("userId") Long userId){

        return apiService.getNiknameByUserId(userId);
    }


    /**
     * 通过用户id查询用户角色名称
     * @param userId
     * @return
     */
    @RequestMapping(value = "/getRoleNameByUserId",method = RequestMethod.GET)
    public List<String> getRoleNameByUserId(@RequestParam("userId") Long userId){

        return apiService.getRoleNameByUserId(userId);
    }

    /**
     * 通过权限code和项目id查询拥有该权限的用户
     * @param userId
     * @return
     */
    @RequestMapping(value = "/queryUserIdByPermission",method = RequestMethod.GET)
    public List<Long> queryUserIdByPermission(@RequestParam("permission") String permission,@RequestParam("projectId") Integer projectId){
        if(StringUtils.isBlank(permission)){
            return null;
        }
        return apiService.listUserIdByPermission(permission,projectId);
    }


}
