package cn.meiot.controller;


import cn.meiot.entity.Config;
import cn.meiot.entity.bo.ConfigUserBo;
import cn.meiot.entity.enums.ConfigKeyTypeEnum;
import cn.meiot.entity.enums.ConfigTypeEnum;
import cn.meiot.entity.vo.ConfigVo;
import cn.meiot.entity.vo.FileConfigVo;
import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.IConfigService;
import cn.meiot.utils.ConstantsUtil;
import cn.meiot.utils.ErrorCodeUtil;
import cn.meiot.utils.RedisConstantUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author yaomaoyang
 * @since 2020-02-28
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

    private RedisTemplate redisTemplate;

    /**
     * key值类型
     */
    private final static Map<Integer,String> KEY_TYPE = new HashMap<Integer, String>();

    /**
     *生效范围
     */
    private final static Map<Integer,String> EFFECT_SCOPE = new HashMap<Integer, String>();

    static {
        KEY_TYPE.put(1,"文本");
        KEY_TYPE.put(2,"图片");
        KEY_TYPE.put(3,"富文本");

        EFFECT_SCOPE.put(0,"所有");
        EFFECT_SCOPE.put(1,"系统");
        EFFECT_SCOPE.put(2,"用户");
    }

    private IConfigService configService;

    private ImgConfigVo imgConfigVo;

    ConfigController(IConfigService configService,ImgConfigVo imgConfigVo,RedisTemplate redisTemplate) {
        this.configService = configService;
        this.imgConfigVo = imgConfigVo;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping(value = "/getCondition")
    public Result getCondition(){
        Result result = Result.getDefaultTrue();
        Map<String,Map<Integer,String>> map = new HashMap<String, Map<Integer, String>>();
        map.put("keyType",KEY_TYPE);
        map.put("effect_scope",EFFECT_SCOPE);
        result.setData(map);
        return result;

    }



    /**
     * 系统配置列表
     *
     * @param current  当前页
     * @param pageSize 煤业展示多少行
     * @return
     */
    @GetMapping(value = "/list")
    public Result list(@RequestParam(value = "current", defaultValue = "1") Integer current,
                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Result result = Result.getDefaultTrue();
        Page<Config> page = new Page<Config>(current, pageSize);
        IPage<Config> iPage = configService.page(page, new QueryWrapper<Config>().lambda().orderByDesc(Config::getCreateTime));
        if (iPage.getTotal() > 0) {
            for (Config c: iPage.getRecords()){
                c.setKeyTypeStr(ConfigKeyTypeEnum.getContent(c.getKeyType()));
                c.setTypeStr(ConfigTypeEnum.getContent(c.getType()));
                if(c.getKeyType() == ConfigKeyTypeEnum.IMAGES.code()){
                    c.setFileUrl(FileConfigVo.getMPath(c.getValue()));
                }
            }

            result.setData(iPage);
        }
        return result;
    }


    /**
     * 修改系统配置文件
     *
     * @param configVo
     * @return
     */
    @PostMapping(value = "/update")
    public Result update(@RequestBody @Valid ConfigVo configVo) {
        if (null == configVo.getId()) {
            return Result.faild(ErrorCodeUtil.SELECT_UPDATE_CONTETNT_PLEASE);
        }
        if(!configVo.getKeyType().equals(ConfigKeyTypeEnum.RICH_TEXT.code())){
            if(configVo.getValue().length() > 300){
                return Result.faild(ErrorCodeUtil.CONTENT_EXCEED_LIMIT);
            }
        }
        //通过id查询信息
        Config byId = configService.getById(configVo.getId());
        if (byId == null) {
            return Result.faild(ErrorCodeUtil.UPDATE_FAILD_MAYBE_IS_DELETE);
        }
        Integer count = configService.updateConfigById(configVo);
        if(count == 1){
            Object obj = redisTemplate.opsForHash().get(RedisConstantUtil.ConfigItem.CONFIG_KEYS, byId.getCKey());
            if(null != obj){
                redisTemplate.opsForHash().delete(RedisConstantUtil.ConfigItem.CONFIG_KEYS, byId.getCKey());
            }
            return Result.getDefaultTrue();
        }
        return Result.faild(ErrorCodeUtil.UPDATE_FAILD_MAYBE_IS_DELETE);
    }

    /**
     * 面向用户的系统参数
     * @return
     */
    @RequestMapping(value = "/nofilter/uList",method = RequestMethod.GET)
    public Result showList(){
        List<ConfigUserBo> list = configService.getListByType(2);
        if(list != null && list.size() > 0){
            for (ConfigUserBo c: list){
                if(c.getKeyType() == ConfigKeyTypeEnum.IMAGES.code()){
                    c.setValue(FileConfigVo.getMPath(c.getValue()));
                }
                if(c.getKeyType() == ConfigKeyTypeEnum.RICH_TEXT.code()){
                    String value = configService.getValueByKey(ConstantsUtil.ConfigItem.SYS_PARAM_RICH_TEXT_URL);
                    c.setValue(value+"?key="+c.getCKey());
                }
            }
        }
        Result result = Result.getDefaultTrue();
        result.setData(list);
        return result;
    }


    @RequestMapping(value = "/nofilter/oneValue",method = RequestMethod.GET)
    public Result getValueByKey(@RequestParam("key") String key){
        Config config = configService.getOne(new QueryWrapper<Config>().lambda().eq(Config::getCKey, key).eq(Config::getType, 2).last("limit 1"));
        if(null == config){
            return Result.OK();
        }
        ConfigUserBo configUserBo = new ConfigUserBo();
        BeanUtils.copyProperties(config,configUserBo);
        return Result.OK(configUserBo);
    }

}
