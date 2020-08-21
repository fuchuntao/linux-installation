package cn.meiot.service.pc;

import cn.meiot.entity.ProjectType;
import cn.meiot.entity.bo.ProjectTypeBo;
import cn.meiot.entity.vo.PermissionVo;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-19
 */
public interface IProjectTypeService extends IService<ProjectType> {

    /**
     * 获取项目类型列表
     * @return
     */
    List<ProjectTypeBo> getList();

    /**
     * 同通过项目类型id查询权限信息
     * @param id
     * @return
     */
    @Deprecated
    Result typePermissionlist(Integer id);

    /**
     * 项目类型权限设置
     * @param permissionVo
     * @return
     */
    Result setPermission(PermissionVo permissionVo);


    /**
     * 根据项目类型id获取权限列表
     * @param id
     * @return
     */
    Result permissionList(Integer id);
}
