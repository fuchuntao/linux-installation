package cn.meiot.mq;

import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.utils.EncryptUtil;
import cn.meiot.utils.RedisConstantUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * token信息
 */
@Component
@Slf4j
public class TokenReceive {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 删除没有的token
     */
    @RabbitListener(queues = RedisConstantUtil.DEL_USER_TOKEN)
    public void deleteUserToen(){

        String  key = RedisConstantUtil.USER_TOKEN+"*";
//        //获取所有的key
        Set allTokens = redisTemplate.keys(key);
        //所有的value
        List<String> list = redisTemplate.opsForValue().multiGet(allTokens);
        Gson gson = new Gson();
        Map<String,Long> tokenUserId = new HashMap<String, Long>();
        for(String s:list){
            try{
                AuthUserBo authUserBo = gson.fromJson(s, AuthUserBo.class);
                tokenUserId.put(RedisConstantUtil.USER_TOKEN+authUserBo.getToken(),authUserBo.getUser().getId());
            }catch(Exception e){

            }
        }
        if(null == tokenUserId || tokenUserId.size() == 0){
            redisTemplate.delete(allTokens);
        }
        Iterator iterator = allTokens.iterator();
        while (iterator.hasNext()){
            String token = (String) iterator.next();
            if(token.split("_").length > 3){
                iterator.remove();
                continue;
            }
            if(tokenUserId.containsKey(token)){
                iterator.remove();
            }
        }
        redisTemplate.delete(allTokens);
        log.info("清除完完成");



//        String  key = RedisConstantUtil.USER_TOKEN+"*";
//        //获取所有的key
//        Set keys = redisTemplate.keys(key);
//        System.out.println("keys："+keys);
//        //所有的value
//        List<String> list = redisTemplate.opsForValue().multiGet(keys);
//        Gson gson = new Gson();
//        //所有的token+用户id
//        List<String>  tokens = new ArrayList<String>();
//
//        Map<String,Long> tokenUserId = new HashMap<String, Long>();
//
//        if(null == list || list.size() == 0){
//            return ;
//        }
//        for(String s:list){
//            try{
//                AuthUserBo authUserBo = gson.fromJson(s, AuthUserBo.class);
//                tokens.add(authUserBo.getToken());
//                tokens.add(authUserBo.getUser().getId().toString());
//                tokenUserId.put(s,authUserBo.getUser().getId());
//            }catch(Exception e){
//
//            }
//        }
//        Iterator iterator = keys.iterator();
//        while (iterator.hasNext()){
//            String userToken = (String) iterator.next();
//            //TODO  数组下标最开始为3 修改为2
//            String[] split = userToken.split("_");
//
//            String token = "";
//            if(split.length > 3){
//                token =  split[3];
//            }else{
//                token =  split[2];
//            }
//            if(tokens.contains(token)){
//                iterator.remove();
//            }
//        }
//        log.info("需要删除的无用token：{}",keys);
//        redisTemplate.delete(keys);
//        log.info("删除多余token信息结束");
    }
}
