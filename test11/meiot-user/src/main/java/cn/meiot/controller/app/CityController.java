package cn.meiot.controller.app;

import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.vo.CityVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.ICityService;
import cn.meiot.utils.ErrorCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/city/")
@Slf4j
public class CityController extends BaseController {

    @Autowired
    private ICityService cityService;



    @PostMapping(value = "/editCity")
    @Log(operateContent = "修改/保存城市信息",operateModule = "用户中心")
    public Result editCity(@RequestBody CityVo cityVo){
        Result result = Result.getDefaultFalse();
        if(null == cityVo){
            log.info("参数不可为空");
            result.setMsg(ErrorCodeUtil.PARMA_NOT_BE_NULL);
            return result;
        }

        if(StringUtils.isEmpty(cityVo.getProvince())){
            log.info("省份不可为空");
            result.setMsg(ErrorCodeUtil.PROVINCE_NOT_BE_NULL);
            return result;
        }
        if(StringUtils.isEmpty(cityVo.getCity())){
            log.info("城市不可为空");
            result.setMsg(ErrorCodeUtil.CITY_NOT_BE_NULL);
            return result;
        }
        if(StringUtils.isEmpty(cityVo.getDistrict())){
            log.info("区域不可为空");
            result.setMsg(ErrorCodeUtil.DISTRICT_NOT_BE_NULL);
            return result;
        }
        return cityService.updateCityByUserId(getUserId(),cityVo);

    }
}
