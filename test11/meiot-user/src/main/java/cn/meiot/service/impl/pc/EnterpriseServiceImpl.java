package cn.meiot.service.impl.pc;

import cn.meiot.entity.Enterprise;
import cn.meiot.entity.bo.EnterpriseBo;
import cn.meiot.mapper.EnterpriseMapper;
import cn.meiot.service.pc.IEnterpriseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 企业信息 服务实现类
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-18
 */
@Service
public class EnterpriseServiceImpl extends ServiceImpl<EnterpriseMapper, Enterprise> implements IEnterpriseService {

    @Autowired
    private EnterpriseMapper enterprise_name;

    @Override
    public List<EnterpriseBo> getList() {
        return enterprise_name.getList();
    }
}
