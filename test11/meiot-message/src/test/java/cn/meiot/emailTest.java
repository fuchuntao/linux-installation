package cn.meiot;

import cn.meiot.entity.vo.EmailVo;
import cn.meiot.service.IMailService;
import cn.meiot.utils.ConstantUtil;
import cn.meiot.utils.EmailTool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * @Package cn.meiot
 * @Description:
 * @author: 武有
 * @date: 2019/11/21 18:13
 * @Copyright: www.spacecg.cn
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
//@ActiveProfiles("test")
public class emailTest {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private EmailTool emailTool;

    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private IMailService iMailService;

    @Value("${mail.platformLoginUrl}")
    private String platformLoginUrl;

    @Value("${mail.enterpriseLoginUrl}")
    private String enterpriseLoginUrl;




    @Test
    public void test01() throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("1411098792@qq.com");
            helper.setTo("1411098792@qq.com");
            helper.setSubject("测试邮箱");//主题
            helper.setText("测试正文");//正文
            mailSender.send(mimeMessage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test02() throws Exception{
        EmailVo emailVo=new EmailVo();
        emailVo.setPassword("123456");
        emailVo.setTo("1411098792@qq.com");
        emailVo.setAccountType(5);
        Map<String, Object> params = new HashMap<>();
        params.put("password", emailVo.getPassword());
        params.put("accountType",emailVo.getAccountType());
        emailTool.sendmail("德微电初始密码通知",params,emailVo.getTo());
    }

    @Test
    public void test03() throws Exception{

        Context context = new Context();
        Map<String,Object> valueMap=new HashMap<>();
        valueMap.put("tips",ConstantUtil.PLATFORM_PWD_TIP+"武有");
        valueMap.put("addr",ConstantUtil.PLATFORM_ADDR+"武有");
        valueMap.put("loginUrl",platformLoginUrl+"武有");
        valueMap.put("password","wuyou123");
        context.setVariables(valueMap);
        String content = this.templateEngine.process("mail", context);
//        helper.setText(content, true);

//        Document doc = Jsoup.parse(EmailTool.readHtmlToString("mail.html"));
//        Integer accountType = 2;
//        if( 1 == accountType){
//            doc.getElementById("tips").html(ConstantUtil.PLATFORM_PWD_TIP);
////            doc.getElementById("loginUrl").html(mailProperties.getPlatformLoginUrl());
//            doc.getElementById("addr").html(ConstantUtil.PLATFORM_ADDR);
//        }else{
//            doc.getElementById("tips").html(ConstantUtil.ENTERPRISE_PWD_TIP);
////            doc.getElementById("loginUrl").html(mailProperties.getEnterpriseLoginUrl());
//            doc.getElementById("addr").html(ConstantUtil.ENTERPRISE_ADDR);
//        }
//        doc.getElementById("password").html("<font color='#ff6600'>"+123456+" </font>");
//        String result = doc.toString();
        iMailService.sendHtmlMail("1411098792@qq.com","主题：你好html邮件",content);
    }


    @Test
    public void test04() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("password", "wuyou123");
        params.put("accountType",1);
        emailTool.sendmail("德微电初始密码",params,"1411098792@qq.com");
    }
}
