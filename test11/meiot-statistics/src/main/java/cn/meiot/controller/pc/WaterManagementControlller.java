package cn.meiot.controller.pc;

import cn.meiot.aop.Log;
import cn.meiot.controller.BaseController;
import cn.meiot.entity.vo.*;
import cn.meiot.feign.DeviceFeign;
import cn.meiot.feign.UserFeign;
import cn.meiot.mq.AppStatisticsMq;
import cn.meiot.service.IWaterStatisticsService;
import cn.meiot.service.IWaterStatisticsYearsService;
import cn.meiot.utils.*;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: WaterManagementControlller
 * @Description: 水务管理模块
 * @author: 符纯涛
 * @date: 2020/2/2
 */
@RestController
@RequestMapping("/pc/WaterManagement")
@Slf4j
public class WaterManagementControlller extends BaseController {

    @Autowired
    private IWaterStatisticsService waterStatisticsService;

    @Autowired
    private AppStatisticsMq appStatisticsMq;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private DeviceFeign deviceFeign;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private WaterUtil waterUtil;

    @Autowired
    private IWaterStatisticsYearsService  waterStatisticsYearsService;





    /**
     *
     * @Title: queryWaterMeter
     * @Description: 查询列表(用水记录)
     * @param waterStatisticsDto
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "queryWaterMeter",method = RequestMethod.GET)
    @Log(operateContent = "查询抄表列表",operateModule = "统计服务")
    public Result queryWaterMeter(WaterStatisticsDto waterStatisticsDto) {
        Long userId = getUserId();
        Integer projectId = getProjectId();
        log.info("获取userId:{},获取项目projectId:{}", userId, projectId);
        waterStatisticsDto.setUserId(userId);
        waterStatisticsDto.setProjectId(projectId);
        return waterStatisticsService.queryWaterMeter(waterStatisticsDto);
    }



    /**
     *
     * @Title: refreshWaterMeter
     * @Description: 手动拉取刷新
     * @param
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "refreshWaterMeter",method = RequestMethod.GET)
    @Log(operateContent = "手动拉取刷新抄表列表",operateModule = "统计服务")
    public Result refreshWaterMeter() {
        //获取当前用户id
        Long userId = getUserId();
        //获取当前项目id
        Integer projectId = getProjectId();

//        //获取主用户id
        Long rtuserId = userFeign.getMainUserId(userId);

//        Long rtuserId = 10000124L;
//        Integer projectId = 24;
        //有水表编号查询缓存是否有这个项目 并且key的过期时间设置为30分钟
        RedisDateVo redisDateVo = RedisDateVo.builder().type(1).updateTime(System.currentTimeMillis()).build();

        //查询缓存是否有这个值
        Object waterRedis = redisTemplate.opsForValue().get(ConstantsUtil.REDIS_WATER_PROJECT + projectId);

        Gson gson = new Gson();
        if(waterRedis != null) {
            //查看值type == 1 直接返回正在拉取数据中请稍后再试
            RedisDateVo redisDate = gson.fromJson(waterRedis.toString(), RedisDateVo.class);
            Integer type = redisDate.getType();
            //拉取的时间戳
            Long updateTime = redisDate.getUpdateTime();
            if(type != null && type == 1 ) {
                return Result.success(StatisticsCodeUtil.WATER_PULL_DATA);

            } else if(type != null && type == 0) {
                SimpleDateFormat simpleDateFormat = ConstantsUtil.getSimpleDateFormat();
                String format = simpleDateFormat.format(new Date(updateTime));
                log.info("水表拉取数据的type:{}",type);
                return Result.success(StatisticsCodeUtil.SUCCESSFUL_CODE,
                        StatisticsCodeUtil.WATER_PULL_DATA_WAIT,format,
                        ConstantsUtil.REDIS_WATER_EXPIRE_TIME.toString());
            }
        }
        //设置redis的值
        redisTemplate.opsForValue().set(ConstantsUtil.REDIS_WATER_PROJECT + projectId,
                gson.toJson(redisDateVo),ConstantsUtil.REDIS_WATER_EXPIRE_TIME, TimeUnit.MINUTES);

        //获取项目id下的水表编号
        List<String> stringList = waterUtil.querySubBuildingWaterId(0L, projectId, rtuserId);
        log.info("获取项目id下的水表编号：{}", stringList);
        //如果没有水表编号则没有数据
        if(CollectionUtils.isEmpty(stringList)) {
            return Result.success(StatisticsCodeUtil.WATER_NUMBER_IS_NULL);
        }
        //如果有值则拉取数据发送队列消息
        Map map = new HashMap();
        map.put("meterIdList", stringList);
        map.put("projectId", projectId);
         //发送队列消息
        rabbitTemplate.convertAndSend(QueueConstantUtil.HAND_WATER_RECORD, map);
//        rabbitTemplate.convertAndSend(QueueConstantUtil.WATER_RECORD_TEST, map);
        return Result.success(StatisticsCodeUtil.WATER_DATA_REFRESH);
    }






    /**
     *
     * @Title: exportWaterMeter
     * @Description: 抄表统计excel导出
     * @param waterStatisticsDto
     * @return: void
     */
    @RequestMapping(value = "exportWaterMeter",method = RequestMethod.GET)
    @Log(operateContent = "导出抄表列表",operateModule = "统计服务")
    public Result exportWaterMeter(WaterStatisticsDto waterStatisticsDto, HttpServletResponse response) {
        Long userId = getUserId();
        Integer projectId = getProjectId();
//        Long userId = 10000121L;
//        Integer projectId = 23;
        log.info("获取用户id,获取项目id,userId:{}", userId, projectId);
        waterStatisticsDto.setUserId(userId);
        waterStatisticsDto.setProjectId(projectId);
        waterStatisticsDto.setSign(1);
        Result result = waterStatisticsService.queryWaterMeter(waterStatisticsDto);
        List<WaterStatisticsVo> data = (List<WaterStatisticsVo>) result.getData();
        List<WaterStatisticsExcelVo> waterStatisticsExcelVos = new ArrayList<>();
        for (WaterStatisticsVo waterStatistics: data) {
            WaterStatisticsExcelVo waterStatisticsExcelVo = new WaterStatisticsExcelVo();
            BeanUtils.copyProperties(waterStatistics, waterStatisticsExcelVo);
            waterStatisticsExcelVos.add(waterStatisticsExcelVo);
        }
        String fileName = "抄表列表";
        Integer tab = waterStatisticsDto.getTab();
        if(tab != null && tab == 1) {
            fileName = "用水记录";
        }
        log.info("需要导出的内容长度为：{}",data.size());
        //导出
        ExcelUtils.export(waterStatisticsExcelVos,fileName,response,WaterStatisticsExcelVo.class);
        return null;
    }


    /**
     *
     * @Title: queryWaterList
     * @Description: 用水趋势
     * @param
     * @return: cn.meiot.entity.vo.Result
     */
    @RequestMapping(value = "queryWaterList",method = RequestMethod.GET)
    @Log(operateContent = "用水趋势",operateModule = "统计服务")
    public Result queryWaterList(@RequestParam("startTime") Long startTime,
                                 @RequestParam("type") Integer type,
                                 @RequestParam(defaultValue = "0",value = "buildingId",required = false) Long buildingId) {
//        获取当前用户id
        Long userId = getUserId();
//        获取当前项目id
        Integer projectId = getProjectId();
//        获取主用户id
        Long rtuserId = userFeign.getMainUserId(userId);
//        Integer projectId = 24;
////
//        Long rtuserId = 10000124L;

        log.info("获取userId:{},获取项目projectId:{}", rtuserId, projectId);

        Result result = waterStatisticsYearsService.queryWaterMeterList(projectId, rtuserId, startTime, type,buildingId);
        return result;

    }

}
