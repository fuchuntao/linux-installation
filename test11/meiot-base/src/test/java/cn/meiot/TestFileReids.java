package cn.meiot;

import cn.meiot.entity.WhiteList;
import cn.meiot.entity.Wss;
import cn.meiot.entity.vo.FirmwareFileVo;
import cn.meiot.entity.vo.Result;
import cn.meiot.entity.vo.UpgradeVo;
import cn.meiot.entity.vo.UploadVo;
import cn.meiot.mapper.EquipmentUserMapper;
import cn.meiot.mapper.WhiteListMapper;
import cn.meiot.service.IEquipmentUserService;
import cn.meiot.service.IFirmwareService;
import cn.meiot.utils.CRC;
import cn.meiot.utils.QueueConstantUtil;
import cn.meiot.utils.RedisComm;
import cn.meiot.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * @Package cn.meiot
 * @Description:
 * @author: 武有
 * @date: 2019/11/20 10:57
 * @Copyright: www.spacecg.cn
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings("all")
@Slf4j
public class TestFileReids {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisTemplate<String,String> redisTemplate2;

    @Autowired
    private IFirmwareService firmwareService;
    @Autowired
    private IEquipmentUserService equipmentUserService;

    @Autowired
    private EquipmentUserMapper equipmentUserMapper;

    @Autowired
    private WhiteListMapper whiteListMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void test01() throws Exception{
        File file = new File("C:\\Users\\Administrator\\Desktop\\moudle_commflash1.bin");
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.skip(0);
        byte fileBytes[] = new byte[500];
        int read = fileInputStream.read(fileBytes);
        String crc = JSON.toJSONString(new UploadVo(read, CRC.getCRC16(fileBytes,read),file.length(),500,0L));
         redisTemplate.opsForValue().set(RedisComm.READ_BYTE+"1.0.0"+"_"+0+"_"+500+"_"+"a",read);
         redisTemplate.opsForValue().set("1.0.0"+"_"+0+"_"+500+"_"+"a",fileBytes);
         redisTemplate.opsForValue().set(RedisComm.FILE_LENGH+"1.0.0"+"_"+0+"_"+500+"_"+"a",file.length());
        System.out.println("=========");
        Integer readNumber= (Integer) redisTemplate.opsForValue().get(RedisComm.READ_BYTE+"1.0.0"+"_"+0+"_"+500+"_"+"a");
        System.out.println("readNumber:"+readNumber);
        byte[] bytes= (byte[]) redisTemplate.opsForValue().get("1.0.0"+"_"+0+"_"+500+"_"+"a");
        System.out.println("redis前："+CRC.getCRC16(fileBytes,read));
        System.out.println("redis后："+CRC.getCRC16(bytes,readNumber));

    }

    @Test
    public void getFirmwareUrl(){
        FirmwareFileVo firmwareUrl = this.firmwareService.getFirmwareUrl("1.0.0", 1);
        log.info("{}",firmwareUrl);
    }

//    @Test
//    public void getUpgradeAndDevice(){
//        RedisUtil.setRedisTemplate(redisTemplate2);
//        Result<Object> upgradeAndDevice = equipmentUserService.getUpgradeAndDevice(10000047L, 0);
//        List<UpgradeVo> upgradeListRedis = RedisUtil.getUpgradeListRedis(10000047L, 0);
//        log.info("===============================================>:{}",upgradeAndDevice);
//    }

    @Test
    public void  equipmentUserMapper(){
        List<WhiteList> whiteLists = whiteListMapper.selectList(new QueryWrapper<WhiteList>().lambda().eq(WhiteList::getFirmwareId,130));
        this.equipmentUserMapper.selectDeviceVersionVo(10000007L, 0,whiteLists);
        System.out.println("");
    }

    @Test
    public void testWss(){
//        10000003
        RedisUtil.setRedisTemplate(redisTemplate2);
        Long userId=10000003L;
        rabbitTemplate.convertAndSend(QueueConstantUtil.WSS_CMD_21, QueueConstantUtil.WSS_KEY, JSONObject.toJSONString(new Wss(1, RedisUtil.getToken(userId), "{\"currentLength\":0,\"length\":0,\"serialNumber\":\"M2201911190002\",\"status\":0}")));

    }
}
