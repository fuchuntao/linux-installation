package cn.meiot.service;

import cn.meiot.entity.Config;
import cn.meiot.entity.bo.ConfigUserBo;
import cn.meiot.entity.vo.ConfigVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yaomaoyang
 * @since 2020-02-28
 */
public interface IConfigService extends IService<Config> {

    /**
     * 根据key查找value
     * @param cKey
     * @return
     */
    String getConfigValueByKey(String cKey);

    /**
     * 通过id修改信息
     * @param configVo
     * @return
     */
    Integer updateConfigById(ConfigVo configVo);

    /**
     * 通过类型获取配置项
     * @param i
     * @return
     */
    List<ConfigUserBo> getListByType(int i);

    /**
     * 通过key获取value
     * @param sysParamRichTextUrl
     * @return
     */
    String getValueByKey(String sysParamRichTextUrl);
}
