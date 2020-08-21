package cn.meiot.utils;

import cn.meiot.entity.vo.SmsVo;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendSmsUtil {

    @Value("${sms.uri}")
    private  static  String uri;

    public  static Boolean send(SmsVo smsVo) throws Exception{
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(true); // 设置该连接是可以输出的
        connection.setRequestMethod("GET"); // 设置请求方式
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
        String line = null;
        StringBuilder result = new StringBuilder();
        while ((line = br.readLine()) != null) { // 读取数据
            result.append(line + "\n");
        }
        connection.disconnect();

        return true;
    }
}
