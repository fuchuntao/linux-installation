package cn.meiot.service.impl;

import cn.meiot.entity.SysUser;
import cn.meiot.entity.UserProject;
import cn.meiot.mapper.ProjectMapper;
import cn.meiot.mapper.UserProjectMapper;
import cn.meiot.service.IUserProjectService;
import cn.meiot.service.pc.IProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-02
 */
@Service
public class UserProjectServiceImpl extends ServiceImpl<UserProjectMapper, UserProject> implements IUserProjectService {

    @Autowired
    private ProjectMapper projectMapper;



    @Override
    public List<Integer> getProjectIdByUser(SysUser user,List<Integer> list) {
        List<Integer> projectIds = null ;
        if(user.getIsAdmin() == 1){
            //获取所有项目
            projectIds = projectMapper.getProjectIdByenId(user.getEnterpriseId());
        }else if(null != list && list.size() > 0){
            projectIds = projectMapper.getProjectIdByRoleId(list);
        }
        return projectIds;
    }
}
