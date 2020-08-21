package cn.meiot;

import cn.meiot.entity.vo.TaskInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MeiotTaskApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void contextLoads() {

        TaskInfo taskInfo = new TaskInfo();

        taskInfo.setJobName("hahahahha");
        taskInfo.setCronExpression("6555");

        redisTemplate.opsForHash().put("test","userId-1",taskInfo);

        Object test = redisTemplate.opsForHash().get("test", "userId-1");
        System.out.println("返回结果："+test);
    }

}
