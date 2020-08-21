package cn.meiot.service.pc;

import cn.meiot.entity.Enterprise;
import cn.meiot.entity.bo.EnterpriseBo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 企业信息 服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-18
 */
public interface IEnterpriseService extends IService<Enterprise> {

    /**
     * 查询企业列表
     * @return
     */
    List<EnterpriseBo> getList();
}
