package cn.meiot.service;

import cn.meiot.entity.SysUser;
import cn.meiot.entity.UserProject;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-02
 */
public interface IUserProjectService extends IService<UserProject> {

    /**
     * 通过用户id查询项目id
     * @param user
     * @return
     */
    List<Integer> getProjectIdByUser(SysUser user,List<Integer> list);
}
