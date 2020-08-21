package cn.meiot.task;

import cn.meiot.entity.Bulletin;
import cn.meiot.entity.enums.PushEnums;
import cn.meiot.service.IBulletinService;
import cn.meiot.service.Push;
import cn.meiot.utils.CommonUtil;
import cn.meiot.utils.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Package cn.meiot.task
 * @Description:
 * @author: 武有
 * @date: 2019/12/19 11:03
 * @Copyright: www.spacecg.cn
 */
@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
@Slf4j
public class BulletinPushTask {
    @Resource(name = "bulletinServiceImpl")
    private IBulletinService bulletinService;
    @Autowired
    private RedisTemplate redisTemplate;


    @Scheduled(cron = "0/5 * * * * ?")
    private void configureTasks() {
        /*1.查询公告 推送时间小于当前时间 且状态为待推送 返回List集合
         * 2.采用不同的实现类来做不同推送目标的实现类*/

        try{
            Boolean push1 = redisTemplate.opsForValue().setIfAbsent("push", "1", 5L, TimeUnit.MINUTES);
            if (push1) {
                pushBulletin();
            }else {
                return;
            }
            redisTemplate.delete("push");
        }catch (Exception e){
            redisTemplate.delete("push");
        }
    }

    @Scheduled(cron = "0/5 * * * * ?")
    private void updateExpired() {

        bulletinService.updateExpired();

    }


    private void pushBulletin() {
        try {
            List<Bulletin> list = bulletinService.list(new QueryWrapper<Bulletin>().lambda().eq(Bulletin::getStatus, 0).lt(Bulletin::getPushTime, CommonUtil.getDate()));
            if (null != list && list.size()>0) {
                for (Bulletin b : list) {
                    Push push = SpringUtil.getBean(PushEnums.getValueByName(b.getPushAims()));
                    push.push(b);
                    b.setStatus(1);
                    bulletinService.updateById(b);
                }
            }
        } catch (Exception e) {
            log.info("" + e);
        }
    }
}
