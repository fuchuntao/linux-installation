package cn.meiot.service;

import cn.meiot.entity.Type;
import cn.meiot.entity.vo.Result;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Package cn.meiot.service
 * @Description:
 * @author: 武有
 * @date: 2019/9/17 15:10
 * @Copyright: www.spacecg.cn
 */
public interface TypeService {
    /**
     * 获取typeList
     * @return
     */
    List<Type>  getTypeList();

    /**
     * 添加类型
     * @param name
     * @return
     */
    Result addType(String name);
}
