package cn.meiot.service.pc;

import cn.meiot.entity.bo.ExportEnUserBo;
import cn.meiot.entity.bo.ExportSingleUserBo;
import cn.meiot.entity.vo.EnterpriseUserVo;
import cn.meiot.entity.vo.ExportUserVo;
import cn.meiot.entity.vo.Result;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface PcUserService {
    /**
     * 保存企业用户
     * @param enterpriseUserVo
     * @return
     */
    Result addEnUser(EnterpriseUserVo enterpriseUserVo,Long userId);

    /**
     * 修改企业用户信息
     * @param enterpriseUserVo
     * @return
     */
    Result updateEnUser(EnterpriseUserVo enterpriseUserVo);

    /**
     * 导出
     * @return
     */
    List<ExportEnUserBo> getExportEnUser(ExportUserVo exportUserVo);

    /**
     * 删除账户
     * @param userId
     * @param ids
     * @return
     */
    Result deleteUser(Long userId, List<Long> ids);

    void exprot(List<ExportEnUserBo> list, String fileName, HttpServletResponse response);

    /**
     * 获取需要导出的个人列表
     * @param exportUserVo
     * @return
     */
    List<ExportSingleUserBo> getExportsingleUser(ExportUserVo exportUserVo);

    /**
     * 禁用当前用户
     * @param userId
     * @return
     */
    Result forbidden(Long userId);

    /**
     * 启用账号
     * @param userId
     * @return
     */
    Result enable(Long userId);

    /**
     * 重置密码
     * @param adminId 管理员id
     * @param userId 重置密码的对象id
     * @return
     */
    Result resetPwd(Long adminId, Long userId);


    List queryElectrician(Integer projectId,String permissionCode);
}
