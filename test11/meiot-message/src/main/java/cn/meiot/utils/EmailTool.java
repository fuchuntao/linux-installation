package cn.meiot.utils;

import cn.meiot.service.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class EmailTool {


    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private IMailService iMailService;

    @Value("${mail.platformLoginUrl}")
    private String platformLoginUrl;

    @Value("${mail.enterpriseLoginUrl}")
    private String enterpriseLoginUrl;

    public void sendmail(String subject, Map<String, Object> params, String to) throws Exception {
        Context context = new Context();
        Map<String,Object> valueMap=new HashMap<>();
        Integer accountType = (Integer)params.get("accountType");
        valueMap.put("password",params.get("password"));
        if( 1 == accountType){
            valueMap.put("tips",ConstantUtil.PLATFORM_PWD_TIP);
            valueMap.put("loginUrl",platformLoginUrl);
            valueMap.put("addr",ConstantUtil.PLATFORM_ADDR);
        }else{
            valueMap.put("tips",ConstantUtil.ENTERPRISE_PWD_TIP);
            valueMap.put("loginUrl",enterpriseLoginUrl);
            valueMap.put("addr",ConstantUtil.ENTERPRISE_ADDR);
        }
        context.setVariables(valueMap);
        String content = this.templateEngine.process("mail", context);
        iMailService.sendHtmlMail(to,subject,content);
    }

    /**
     * 读取html文件为String
     * @param htmlFileName
     * @return
     * @throws Exception
     */
    @Deprecated
    public static String readHtmlToString(String htmlFileName) throws Exception{
        InputStream is = null;
        Reader reader = null;
        try {
            is = EmailTool.class.getClassLoader().getResourceAsStream(htmlFileName);
            if (is ==  null) {
                throw new Exception("未找到模板文件");
            }
            reader = new InputStreamReader(is, "UTF-8");
            StringBuilder sb = new StringBuilder();
            int bufferSize = 1024;
            char[] buffer = new char[bufferSize];
            int length = 0;
            while ((length = reader.read(buffer, 0, bufferSize)) != -1){
                sb.append(buffer, 0, length);
            }
            return sb.toString();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.error("关闭io流异常", e);
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch ( IOException e) {
                log.error("关闭io流异常", e);
            }
        }
    }


}

