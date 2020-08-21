package cn.meiot.mapper;

import cn.meiot.entity.Enterprise;
import cn.meiot.entity.bo.EnterpriseBo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 企业信息 Mapper 接口
 * </p>
 *
 * @author yaomaoyang
 * @since 2019-09-18
 */
@Mapper
public interface EnterpriseMapper extends BaseMapper<Enterprise> {

    /**
     * 获取企业列表
     * @return
     */
    List<EnterpriseBo> getList();
}
