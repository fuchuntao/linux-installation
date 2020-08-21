package cn.meiot.service.api;

import cn.meiot.entity.bo.UserNumBo;

import java.util.List;
import java.util.Map;

public interface ApiService {
    /**
     * 根据项目id查询创建时间
     * @param projectId
     * @return
     */
    Long getProjectDateByProjectId(Integer projectId);

    /**
     * 通过项目id查询项目名称
     * @param projectId
     * @return
     */
    String queryProjectNameById(Integer projectId);

    /**
     * 根据项目di查询企业名称和项目名称
     * @param projectId
     * @return
     */
    Map<String, String> queryProNameByProjectId(Integer projectId);

    /**
     * 后去用户数量
     * @return
     */
    UserNumBo getUserNum();

    /**
     * 通过类型查询用户id列表
     * @param type
     * @return
     */
    List<Long> getUserIdByType(Integer type);

    /**
     * 获取用户openid
     * @param userId
     * @return
     */
    String getOpenid(Long userId);

    /**
     * 通过用户id获取角色id
     * @param userId
     * @return
     */
    Integer getTypeByUserId(Long userId);

    /**
     * 通过用户id获取角色id
     * @param userId
     * @return
     */
    List<Integer> getRoleIdByUserId(Long userId);

    /**
     * 通过用户id和权限唯一标识校验当前用户是否有权限
     * @param userId
     * @param permission
     * @return
     */
    boolean checkPermission(Long userId, String permission,Integer projectId);

    /**
     * 通过用户id查询用户昵称
     * @param userId
     * @return
     */
    String getNiknameByUserId(Long userId);

    /**
     * 通过用户id查询用户角色名称
     * @param userId
     * @return
     */
    List<String> getRoleNameByUserId(Long userId);

    /**
     * 通过权限唯一标识和项目id查询用户id
     * @param permission
     * @param projectId
     * @return
     */
    List<Long> listUserIdByPermission(String permission, Integer projectId);
}
