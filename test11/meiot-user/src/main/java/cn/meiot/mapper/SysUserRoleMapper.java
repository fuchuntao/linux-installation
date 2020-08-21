package cn.meiot.mapper;

import cn.meiot.entity.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-02
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 查看此角色是否已被用户使用
     * @param map
     * @return
     */
    Integer selectCountByMap(@Param("map") Map<String, Integer> map);

    /**
     * 根据用户id获取角色列表
     * @param userId
     * @return
     */
    List<Integer> getRoleList(@Param("userId") Long userId);

    /**
     * 通过用户id查询所拥有的所有角色
     * @param userId
     * @return
     */
    @Select(" select sys_role_id from sys_user_role where sys_user_id = #{userId} ")
    List<Integer> selectRoleByUserId(@Param("userId") Long userId);

    /**
     * 通过角色查询用户信息
     * @param roleIds
     * @return
     */
    List<Long> selectUserIdByRoles(@Param("list") List<Integer> roleIds);

    /**
     * 通过角色id查询用户id
     * @param roleId
     * @return
     */
    @Select(" select sys_user_id from sys_user_role where sys_role_id = #{roleId}  ")
   List<Long> selectUserIdByRoleId(Integer roleId);
}
