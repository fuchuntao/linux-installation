package cn.meiot.jg;

import cn.meiot.enums.JpushTypeEnum;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JPushClientExample {

    @Autowired
    private MyJPushClient myJPushClient;

    public void sendMsg(List<String> tagsList, String notificationTitle, String msgTitle, String msgContent,Integer msgType, Map<String,String> extras,Integer userType){

        try{
            if(null == extras){
                extras = new HashMap<String,String>();
            }
            myJPushClient.sendToTagsList(tagsList, notificationTitle,msgTitle,
                    msgContent, msgType,extras,userType);
        }catch (Exception e){
            log.error("推送消息失败，{}",e.getMessage());
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        List<String> tagsList = new ArrayList<String>();
        tagsList.add("2");
        String a = "{\"mainUserPhone\":\"151****1508\",\"subUserName\":\"null\",\"subUserPhone\":\"188****8888\",\"mainUserName\":\"骚逼凌智颖\",\"subUser\":\"3\",\"mainUser\":\"8\"}";
        Map<String,String> map = new HashMap<String,String>();
        map = new Gson().fromJson(a, Map.class);
        map.put("msgType","2");
        MyJPushClient myJPushClient = new MyJPushClient();
        //myJPushClient.sendToTagsList(tagsList,"通知栏标题","故障消息标题","故障消息内容", JpushTypeEnum.NOTIFICATION.value(),map);
    }

}
