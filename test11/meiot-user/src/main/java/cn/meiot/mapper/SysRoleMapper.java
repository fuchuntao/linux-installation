package cn.meiot.mapper;

import cn.meiot.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-08-02
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 通过id查询此角色的类型
     * @param id
     * @return
     */
    Integer selectTypeById(@Param("id") Integer id);

    /**
     * 通过用户id查询角色名称
     * @param id
     * @return
     */
    @Select(" SELECT id,name FROM `sys_role` WHERE id = (SELECT sys_role_id FROM `sys_user_role` WHERE sys_user_id = #{id} LIMIT 1) ")
    SysRole queryNameById(Long id);


    List<String> queryNamesById(@Param("list") List<Integer> id);

    /**
     * 查询当前角色是否是超级管理员
     * @param roleId
     * @return
     */
    @Select(" select is_super_admin from sys_role where id = #{roleId} ")
    Integer queryIsAdminById(Integer roleId);

    /**
     * 通过用户id查询角色名称
     * @param id
     * @return
     */
    @Select(" SELECT NAME FROM `sys_role` WHERE id = (SELECT sys_role_id FROM `sys_user_role` WHERE sys_user_id = #{id} LIMIT 1  )  ")
    String queryNameByUserId(Long id);
}
