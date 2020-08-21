package cn.meiot.mapper;

import cn.meiot.entity.RoleProject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-26
 */
@Mapper
public interface RoleProjectMapper extends BaseMapper<RoleProject> {

    /**
     * 通过角色id查询项目id
     * @param roleIds
     * @return
     */
    List<Integer> selectprojectIdsByRoles(@Param("list") List<Integer> roleIds);
}
