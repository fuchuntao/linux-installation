package cn.meiot.controller;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import cn.meiot.service.RedisService;
import cn.meiot.utils.CodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;


/**
 * 
 * @ClassName: ValidateCodeController
 * @Description: 验证码
 * @author Sheng sheng.haohao@autoflysoft.com
 * @Company dctp
 * @date 2017年8月13日 下午8:57:28
 *
 */
@RefreshScope
@RestController
@Slf4j
public class ValidateCodeController  {

    @Autowired
    private RedisService redisService;



	/**
	 * 
	 * @Title: getValidateCode
	 * @param:
	 * @Description: 生成验证码
	 * @return void
	 */
	@ResponseBody
    @RequestMapping(value = "/validatecode/nofilter/gen", method = RequestMethod.GET)
	public void getValidateCode(@RequestParam("randomData") String randomData, HttpServletResponse resp) {
        // 调用工具类生成的验证码和验证码图片
        Map<String, Object> codeMap = CodeUtil.generateCodeAndPic();
        log.info("验证码：{}",codeMap.get("code"));
        // 将四位数字的验证码保存到redis中。
        redisService.saveValue(randomData,codeMap.get("code").toString().toUpperCase());

        // 禁止图像缓存。
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", -1);

        resp.setContentType("image/jpeg");

        // 将图像输出到Servlet输出流中。
        ServletOutputStream sos;
        try {
            sos = resp.getOutputStream();
            ImageIO.write((RenderedImage) codeMap.get("codePic"), "jpeg", sos);
            sos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
