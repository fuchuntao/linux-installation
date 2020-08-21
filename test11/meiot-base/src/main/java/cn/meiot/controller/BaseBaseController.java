package cn.meiot.controller;

import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.exception.MyTokenExcption;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.RedisConstantUtil;
import cn.meiot.utils.RedisUtil;
import cn.meiot.utils.UserAgentUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * @Package cn.meiot.controller
 * @Description:
 * @author: 武有
 * @date: 2019/11/30 17:34
 * @Copyright: www.spacecg.cn
 */
@Slf4j
@Controller
@SuppressWarnings("all")
public class BaseBaseController extends BaseController{



    /**
     * 获取项目id(前端传值)
     *
     * @return
     */

    @Override
    public Integer getProjectId() {
        String project = ConstantsUtil.PROJECT;
        String projectId = request.getHeader(project);
        String  device = UserAgentUtils.getDeviceName(request.getHeader("User-Agent"));
        if("pc".equals(device)){
            device = "pc";
        }else{
            device = "phone";
        }
        log.info("获取到的项目id:{}",projectId);
        if (StringUtils.isEmpty(projectId)) {
            throw  new MyTokenExcption("未获取到项目id","未获取到项目id");
        }
        AuthUserBo authUserBo = RedisUtil.getUserInfo(getUserId());
        if(null == authUserBo){
            throw  new MyTokenExcption("未获取到用户信息-plus","未获取到用户信息-plus");
        }
//        AuthUserBo authUserBo = new Gson().fromJson(object.toString(),AuthUserBo.class);
        List<Integer> projectIds = authUserBo.getProjectIds();
        if(null == projectIds || projectIds.size() == 0){
            throw  new MyTokenExcption("当前用户没有该项目,请尝试重新登录","当前用户没有项目,请尝试重新登录");
        }
        Integer id = Integer.valueOf(projectId);
        if(projectIds.contains(id))
            return id;
        log.info("当前用户操作他人项目");
        throw  new MyTokenExcption("当前用户没有该项目,请尝试重新登录","当前用户没有项目,请尝试重新登录");

    }


    public String getDevic(){
        String  device = UserAgentUtils.getDeviceName(request.getHeader("User-Agent"));
        if("pc".equals(device)){
            device = "pc";
        }else{
            device = "phone";
        }
        return device;
    }

}
