package cn.meiot.utils;

import cn.meiot.controller.BaseBaseController;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.bo.UserInfoBo;
import cn.meiot.entity.vo.ProgressBarVo;
import cn.meiot.entity.vo.UpgradeVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Package cn.meiot.utils
 * @Description:
 * @author: 武有
 * @date: 2019/11/27 15:32
 * @Copyright: www.spacecg.cn
 */
public class RedisUtil extends BaseBaseController {
    public static final String UPGRADE = "Upgrade_";
    public static final String UPGRADE_FLAG = "Upgrade_Flag_";



    /**
     * 通过设备好获取项目ID
     */
    public static final String UPGRADE_PROJECT_ID = "UpgradeProjectId_";

    /**
     * 通过设备好获取用户ID
     */
    public static final String UPGRADE_USER_ID = "UpgradeUserId_";

    public static final String UPGRADE_PROGRESS_BAR = "UpgradeProgressBar_";

    public static final String FILE_SIZE = "file_size";


    public static BaseBaseController baseBaseController;




    /**
     * 规定一次升级时间
     */
    public static final int TIME = 60 * 24;

    private static RedisTemplate<String, String> redisTemplate;

    public static AuthUserBo getUserInfo(Long userId) {
        String userInfoJsonString = redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN+"pc"+"_" + userId);
        if (StringUtils.isEmpty(userInfoJsonString)){
            userInfoJsonString=redisTemplate.opsForValue().get(RedisConstantUtil.USER_TOKEN+"phone"+"_" + userId);
      }

        if (StringUtils.isEmpty(userInfoJsonString)) {
            return null;
        }
        Object o = JSONObject.parse(userInfoJsonString);
        AuthUserBo userInfo = JSONObject.parseObject((String) o, AuthUserBo.class);
        return userInfo;
    }

    public static void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        RedisUtil.redisTemplate = redisTemplate;
    }

    public static UserInfoBo getUser(Long userId) {
        AuthUserBo userInfo = RedisUtil.getUserInfo(userId);
        if (null == userInfo) {
            return null;
        }
        return userInfo.getUser();
    }

    public static void setUpgradeListRedis(Long userId, Integer projectId, List<UpgradeVo> deviceVersionVoList) {
        redisTemplate.opsForValue().set(UPGRADE + userId + "_" + projectId, JSONObject.toJSONString(deviceVersionVoList), TIME, TimeUnit.MINUTES);
    }

    public static List getUpgradeListRedis(Long userId, Integer projectId) {
        String s = redisTemplate.opsForValue().get(UPGRADE + userId + "_" + projectId);
        if (null == s) {
            return null;
        }
        List list = JsonUtils.toList(s, UpgradeVo.class);
        return list;
    }

//    public static void setDeviceUpgrade(String serialNumber, int status) {
//        redisTemplate.opsForValue().set(UPGRADE_STATUS + serialNumber, status + "", 10, TimeUnit.MINUTES);
//    }

    /**
     * 设置项目ID
     *
     * @param serialNumber
     * @param projectId
     */
    public static void setProjectIdBySerialNumber(String serialNumber, int projectId) {
        redisTemplate.opsForValue().set(UPGRADE_PROJECT_ID + serialNumber, projectId + "", 1, TimeUnit.MINUTES);
    }

    /**
     * 查询项目ID
     *
     * @param serialNumber
     * @return
     */
    public static Integer getProjectIdBySerialNumber(String serialNumber) {
        String s = redisTemplate.opsForValue().get(UPGRADE_PROJECT_ID + serialNumber);
        if (null == s) {
            return null;
        }
        return Integer.valueOf(redisTemplate.opsForValue().get(UPGRADE_PROJECT_ID + serialNumber));

    }

    /**
     * 设置用户ID
     *
     * @param serialNumber
     * @param userId
     */
    public static void setUserIdBySerialNumber(String serialNumber, Long userId) {
        redisTemplate.opsForValue().set(UPGRADE_USER_ID + serialNumber, userId + "", 1, TimeUnit.MINUTES);
    }

    /**
     * 查询用户id
     *
     * @param serialNumber
     * @return
     */
    public static Long getUserIdBySerialNumber(String serialNumber) {
        String s = redisTemplate.opsForValue().get(UPGRADE_USER_ID + serialNumber);
        if (null == s) {
            return null;
        }
        return Long.valueOf(s);

    }

    public static void setUploadVo(Long userId, Integer projectId, String serialNumber, ProgressBarVo uploadVo) {
        redisTemplate.opsForHash().put(UPGRADE_PROGRESS_BAR + userId + "_" + projectId, serialNumber, JSONObject.toJSONString(uploadVo));
        redisTemplate.expire(UPGRADE_PROGRESS_BAR + userId + "_" + projectId, TIME, TimeUnit.MINUTES);
    }

    public static ProgressBarVo getUploadVo(Long userId, Integer projectId, String serialNumber) {
        String o = (String) redisTemplate.opsForHash().get(UPGRADE_PROGRESS_BAR + userId + "_" + projectId, serialNumber);
        if (null == o) {
            return null;
        }
        return JSONObject.parseObject(o, ProgressBarVo.class);
    }

    public static List<ProgressBarVo> getUploadVos(Long userId, Integer projectId) {
//        UPGRADE_PROGRESS_BAR + userId + "_" + projectId
        List<Object> values = redisTemplate.opsForHash().values(UPGRADE_PROGRESS_BAR + userId + "_" + projectId);
        if (null == values) {
            return null;
        }
        List<ProgressBarVo> list = new ArrayList<>();
        for (Object o : values) {
            ProgressBarVo progressBarVo = JsonUtils.toBean(o.toString(), ProgressBarVo.class);
            list.add(progressBarVo);
        }
        if (list.size() <= 0) {
            return null;
        }
        return list;
    }

    public static Long getLength(String name) {
        String s = (String) redisTemplate.opsForHash().get(FILE_SIZE, name);
        if (null == s) {
            return null;
        }
        return Long.valueOf(s);
    }

    public static void cleanKey(String key) {
        redisTemplate.delete(key);
    }

    public static void cleanUploadVos(Long userId, Integer projectId) {
        cleanKey(UPGRADE_PROGRESS_BAR + userId + "_" + projectId);
    }

    public static void cleanUpgradeListRedis(Long userId, Integer projectId) {
        cleanKey(UPGRADE + userId + "_" + projectId);
    }

    public static String getToken(Long userId) {
        return RedisUtil.getUserInfo(userId).getToken();
    }

    public static synchronized void clean(Long userId, Integer projectId) {

        List<ProgressBarVo> uploadVos = RedisUtil.getUploadVos(userId, projectId);
        if (null == uploadVos || uploadVos.size() <= 0) {
            return;
        }
        int i = 0;
        for (ProgressBarVo vo : uploadVos) {
            if (vo.getStatus() == 2 || vo.getStatus() == 3) {
                i++;
            }
        }
        if (i == uploadVos.size()) {
            RedisUtil.cleanUploadVos(userId, projectId);
            RedisUtil.cleanUpgradeListRedis(userId, projectId);
        }
    }

    /**
     * 通过设备号设置唯一标识
     *
     * @param s
     * @param uuid
     */
    public static void setFlag(String s, String uuid) {
        redisTemplate.opsForValue().set(UPGRADE_FLAG + s, uuid, RabbitMqUtil.TIME + 2, TimeUnit.MINUTES);
    }

    /**
     * 通过设备号获取唯一标识
     *
     * @param snb
     * @return
     */
    public static String getFlag(String snb) {
        return redisTemplate.opsForValue().get(UPGRADE_FLAG + snb);
    }

    public static String getVersion() {
        return redisTemplate.opsForValue().get("test_version");
    }

    public static void setVersion(String version) {
        redisTemplate.opsForValue().set("test_version", version);
    }

    public static String getSizeByAddress(String address) {
        return (String) redisTemplate.opsForHash().get(RedisConstantUtil.FILE_SIZE, address);
    }

    public static BaseBaseController getBaseBaseController() {
        return baseBaseController;
    }

    public static void setBaseBaseController(BaseBaseController baseBaseController) {
        RedisUtil.baseBaseController = baseBaseController;
    }
}
