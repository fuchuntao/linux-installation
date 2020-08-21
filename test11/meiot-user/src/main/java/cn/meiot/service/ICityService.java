package cn.meiot.service;

import cn.meiot.entity.vo.CityVo;
import cn.meiot.entity.vo.Result;

public interface ICityService {

    /**
     * 根据用户id修改城市信息
     * @param userId
     * @param cityVo
     * @return
     */
    Result updateCityByUserId(Long userId, CityVo cityVo);
}
