package cn.meiot.config;

import cn.meiot.config.intercepors.AppIntercepors;
import cn.meiot.config.intercepors.PcIntercepors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;

/**
 * @Package cn.meiot.config
 * @Description:
 * @author: 武有
 * @date: 2019/9/17 18:30
 * @Copyright: www.spacecg.cn
 */
@Configuration
public class WebConfigurer implements WebMvcConfigurer {
    @Autowired
    private PcIntercepors pcIntercepors;

    @Autowired
    private AppIntercepors appIntercepors;



    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(pcIntercepors).addPathPatterns("/AfterSale/**").excludePathPatterns();
        registry.addInterceptor(appIntercepors).addPathPatterns("/AppAfterSale/**").excludePathPatterns();
    }


}
