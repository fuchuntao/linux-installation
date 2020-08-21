package cn.meiot.mapper;

import cn.meiot.entity.SysUser;
import cn.meiot.entity.bo.*;
import cn.meiot.entity.vo.ExportUserVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-07-29
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 查询账号里类型
     * @param userId
     * @return
     */
    Integer selectTypeById(@Param("userId") Long userId);

    /**
     * 通过昵称查询用户id
     * @param nikName
     * @return
     */
    Long selectIdByNikName(@Param("nikName") String nikName);

    /**
     * 互殴去所有用户id
     * @param type
     * @return
     */
    @Select("select id from sys_user where type = #{type} ")
    List<String> findAllUserId(@Param("type") Integer type);

    /**
     * 获取用户列表
     * @param sysUser
     * @return
     */
    List<PcUserInfo> getPersonList( @Param("sysUser") SysUser sysUser,@Param("currentPage") Integer currentPage,@Param("pageSize") Integer pageSize,@Param("keyword") String keyword);

    /**
     * 获取用户列表总数
     * @param sysUser
     * @return
     */
    Integer getPersonListCount(@Param("sysUser") SysUser sysUser,@Param("keyword") String keyword);

    /**
     * 获取平台所有账户总数
     * @param map
     * @return
     */
    Integer getAdminListCount(@Param("map") Map<String, Object> map);

    /**
     * sysUserMapper
     * @param map
     * @return
     */
    List<PlatUser> getAdminList(@Param("map") Map<String, Object> map);

    /**
     * 通过账号查询用户id
     * @param account
     * @return
     */
    @Select(" select id from  sys_user where user_name = #{account} and deleted = 0")
    Long selectIdByAccount(String account);

    @Select(" select id from sys_user where belong_id = #{mainUserId} and deleted = 0 ")
    List<Long> getSubUserIdByMainUserId(Long mainUserId);

    /**
     * 查询需要导出的内容
     * @return
     */
    List<ExportEnUserBo> getExportEnUser(ExportUserVo exportUserVo);

    /**
     * 获取需要导出的个人列表
     * @param exportUserVo
     * @return
     */
    List<ExportSingleUserBo> getExportsingleUser(ExportUserVo exportUserVo);

    /**
     * 通过企业id获取项目主账号id
     * @param enterpriseId
     * @return
     */
    @Select(" select id from sys_user where enterprise_id =#{enterpriseId} and belong_id = 0 limit 1")
    Long queryMainIdByEnterpriseId(Integer enterpriseId);

    /**
     * 禁用当前用户
     * @param userId
     * @return
     */
    @Update(" update  sys_user set status = 2 where id = #{userId}")
    Integer forbidden(Long userId);

    /**
     * 查询当前账号下的所有子账号
     * @param userId
     * @return
     */
    @Select(" select id from sys_user where belong_id = #{userId} ")
    List<Long> selectIdByBelongId(Long userId);

    /**
     * 通过用户id查询状态
     * @param userId
     * @return
     */
    @Select(" select status from  sys_user where id = #{userId}")
    Integer getStatusByUserId(Long userId);

    /**
     * 通过项目id获取用户id
     * @param projectId
     * @return
     */
    @Select(" SELECT id FROM sys_user WHERE enterprise_id = (SELECT enterprise_id FROM `project`  WHERE id = #{projectId}) ")
    List<Long> selectIdByProjectId(Integer projectId);

    /**
     * 获取用户数量
     * @return
     */
    @Select(" SELECT (SELECT COUNT(1) FROM sys_user WHERE TYPE = 2 AND deleted=0  AND is_admin = 1) AS companyUserSum,(SELECT COUNT(1) FROM sys_user WHERE TYPE = 5 AND deleted=0) AS userSum ")
    UserNumBo getUserNum();

    List<Long> getUserByType(@Param("type") Integer type);

    @Select(" SELECT nick_name FROM sys_user WHERE id = #{userId} ")
    String selectNiknameById(Long userId);
}
