package cn.meiot.controller.api;

import cn.meiot.entity.vo.ImgConfigVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.service.ISysUserService;
import cn.meiot.service.api.MessageService;
import cn.meiot.utils.RedisConstantUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/msg")
@Slf4j
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Value("${img.map}")
    private String map;

    @Value("${img.servername}")
    private String serverName;

    @Value("${img.path}")
    private String path;

    @Value("${img.img}")
    private String img;

    @Value("${img.upgrade}")
    private String upgrade;


    @Value("${img.thumbnail}")
    private  String thum;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 获取所有用户的id
     * @return
     */
    @RequestMapping(value = "/findAllUserId",method = RequestMethod.GET)
    public Result findAllUserId(@RequestParam("type") Integer type){

        return messageService.findAllUserId(type);

    }

    @RequestMapping(value = "/getImgConfig",method = RequestMethod.GET)
    public ImgConfigVo getImgConfig(){
        ImgConfigVo imgConfigVo= new ImgConfigVo();
        imgConfigVo.setMap(map);
        imgConfigVo.setPath(path);
        imgConfigVo.setServername(serverName);
        imgConfigVo.setThumbnail(thum);
        imgConfigVo.setImg(img);
        imgConfigVo.setUpgrade(upgrade);
        String json = new Gson().toJson(imgConfigVo);
        redisTemplate.opsForValue().set(RedisConstantUtil.IMG_CONFIG,json);
        return imgConfigVo;
    }

    @RequestMapping(value = "/getAllUserIdByMainUser",method = RequestMethod.GET)
    public List<Long> getAllUserIdByMainUser(@RequestParam("mainUserId") Long mainUserId){
        if(null == mainUserId){
            log.info("查询的id为空");
            return null;
        }
        List<Long> list = messageService.getSubUserIdByMainUserId(mainUserId);
        log.info("主账户id：{}，子账户：{}",mainUserId,list);
        return list;
    }

    /**
     * 通过角色查询用户
     * @param map
     * @return
     */
    @PostMapping(value = "/getUserIdsByRoleId")
    public List<Long> getUserIdsByRoleId(@RequestBody Map map){
        List<Integer> roleIds = (List<Integer>) map.get("roleIds");
        if(null == roleIds || roleIds.size() == 0){
            log.info("传递的角色id为空");
            return null;
        }
        return messageService.getUserIdsByRoleId(roleIds);

    }
}
