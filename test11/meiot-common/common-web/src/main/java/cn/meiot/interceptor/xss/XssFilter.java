package cn.meiot.interceptor.xss;

import cn.meiot.controller.BaseController;
import cn.meiot.entity.vo.Result;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class XssFilter extends BaseController implements Filter{

    //是否过滤富文本内容
    private static boolean IS_INCLUDE_RICH_TEXT = true;
    public List<String> excludes = new ArrayList<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String method = "GET";
        String param = "";
        RequestFacade req = (RequestFacade) request;
//        log.info("请求接口:{}   请求方式:{}",req.getRequestURI(),req.getMethod());
        if (req.getRequestURI().contains("bulletin/add") || req.getRequestURI().contains("bulletin/updateBulletin")){
            filterChain.doFilter(request,response);
            return ;
        }
        XssAndSqlHttpServletRequestWrapper xssRequest = null;
        if (request instanceof HttpServletRequest) {
            method = ((HttpServletRequest) request).getMethod();
            xssRequest = new XssAndSqlHttpServletRequestWrapper((HttpServletRequest) request);
        }
        if ("POST".equalsIgnoreCase(method)) {
            param = this.getBodyString(xssRequest.getReader());

            String s = XssAndSqlHttpServletRequestWrapper.stripXSSAndSql(param);

            if(StringUtils.isNotBlank(param)){
                if(xssRequest.checkXSSAndSql(param)){
                    write(response);
                    return;
                }
            }
        }
        if (xssRequest.checkParameter()) {
            write(response);
            return;
        }
        filterChain.doFilter(xssRequest, response);
    }

    private void write( ServletResponse response) throws IOException {
        Result result = Result.getDefaultFalse();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        result.setMsg("request_not_allow");
        Gson gson =  new Gson();
        String  r = gson.toJson(result);
        log.info("返回结果："+r);
        out.write(r);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String isIncludeRichText = filterConfig.getInitParameter("isIncludeRichText");
        if (StringUtils.isNotBlank(isIncludeRichText)) {
            IS_INCLUDE_RICH_TEXT = BooleanUtils.toBoolean(isIncludeRichText);
        }
        String temp = filterConfig.getInitParameter("excludes");
        if (temp != null) {
            String[] url = temp.split(",");
            for (int i = 0; url != null && i < url.length; i++) {
                excludes.add(url[i]);
            }
        }
    }

    @Override
    public void destroy() {
    }


    // 获取request请求body中参数
    public static String getBodyString(BufferedReader br) {
        String inputLine;
        String str = "";
        try {
            while ((inputLine = br.readLine()) != null) {
                str += inputLine;
            }
            br.close();
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
        return str;

    }

}
