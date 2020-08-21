package cn.meiot.filter;

import cn.meiot.config.RabbitConfig;
import cn.meiot.entity.Auth;
import cn.meiot.entity.bo.AuthUserBo;
import cn.meiot.entity.vo.Result;
import cn.meiot.utils.*;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * @author fengshaoyu
 * @title: AccessGatewayFilter
 * @projectName meiot
 * @description: 过滤
 * @date 2019-05-21 10:40
 */
@Slf4j
public class AccessGatewayFilter implements GlobalFilter {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;



    @Autowired
    private UriCheckUtil uriCheckUtil;

    @Value("${spring.cloud.gateway.discovery.locator.enabled}")
    private String value;

    @Value("${sys.uri}")
    private String uri;

    @Value("${sys.ws}")
    private String ws;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("请求接口:{}   请求方式:{}",request.getPath(),request.getMethod());
        if(request.getMethod().equals("OPTIONS")){
            return chain.filter(exchange);
        }
        //log.info("请求接口："+request.getURI()+"请求IP："+request.getRemoteAddress().getAddress());
        String authentication = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String userAgent = request.getHeaders().getFirst(ConstantUtil.DEVICE);
        String  device = UserAgentUtils.getDeviceName(userAgent);
        log.info("设备：{}===========>{}",userAgent,device);
        if("pc".equals(device)){
            device = "pc";
        }else{
            device = "phone";
        }
        log.info("转换后的设备：{},token:{}",device,authentication);
        String projectId = request.getHeaders().getFirst(ConstantsUtil.PROJECT);
       // log.info("项目id:{}",projectId);
        String url = request.getPath().value();
        ServerHttpRequest.Builder builder = request.mutate();
        if(url.indexOf(uri) >= 0  || url.indexOf(ws) >=0){
            return chain.filter(exchange);
        }

        Auth auth = Auth.builder().authentication(authentication).device(device).projectId(projectId).uri(url).build();
        //校验token是否有效
        Result result = auth(auth);
        if(!result.isResult()){
           return unauthorized(exchange,result);
        }

        //设置请求头  讲用户id设置到请求头中
        ServerHttpRequest host = exchange.getRequest().mutate().headers(httpHeaders -> {
            //删除头部的userId字段，防止有人直接再请求头部作假
            httpHeaders.remove("userId");
            //将登录用的id放到请求头中
            httpHeaders.add(ConstantsUtil.USER_ID,result.getData().toString());
        }).build();
        exchange = exchange.mutate().request(host).build();
        log.info("请求用户：{}，请求接口：{}，请求设备：{}",result.getData(),request.getPath(),device);
        return chain.filter(exchange);
    }


    /**
     *  网关拒绝返回401
     * @param serverWebExchange
     * @return
     */
    private Mono<Void> unauthorized(ServerWebExchange serverWebExchange,Result result) {
        serverWebExchange.getResponse().setStatusCode(HttpStatus.OK);

        ServerHttpResponse originalResponse = serverWebExchange.getResponse();
        originalResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return serverWebExchange.getResponse().writeWith(Flux.just(this.getBodyBuffer(serverWebExchange.getResponse(), result)));
       //return serverWebExchange.getResponse().writeWith(Flux.just(buffer));
    }

    /**
     * 封装返回值
     *
     * @param response
     * @param result
     * @return
     */
    private DataBuffer getBodyBuffer(ServerHttpResponse response, Result result) {
        return response.bufferFactory().wrap(JSONObject.toJSONBytes(result));
    }



    /**
     * 校验凭证
     * @param auth
     * @return
     */
    private Result auth(Auth auth){

        log.info("请求接口：{}",auth.getUri());
        Result result = Result.getDefaultFalse();
        if(StringUtils.isEmpty(auth.getAuthentication())){
            result.setCode("1");
           // result.setMsg("校验不能为空");
            return result;
        }
        //通过token获取userId
        String key = RedisConstantUtil.USER_TOKEN;
        String secretKey = (String )redisTemplate.opsForValue().get(key+auth.getAuthentication());
        //log.info("用户token：{}，用户密钥：{}",auth.getAuthentication(),secretKey);
        if(null == secretKey){
            log.info("登录过期，请重新登录！");
            result.setCode("1");
            result.setMsg("登录过期，请重新登录！");
           return result;
        }
        //解密 ，拿到用户id信息
        String userId = "";
        try {
            userId = EncryptUtil.decrypt(auth.getAuthentication(),secretKey );
            //log.info("用户id：{}",userId);
        }catch (Exception e){
            log.info("token非法");
            result.setMsg("token非法");
            return result;
        }
        log.info("用户id：{}",userId);
        //通过用户id获取token信息
        String user= (String) redisTemplate.opsForValue().get(key+auth.getDevice()+"_"+userId);
       // log.info("用户信息：{}",user);
        if(null == user){
            log.info("用户信息为空");
            result.setCode("1");
            result.setMsg("登录过期，请重新登录！");
            return result;
        }
        AuthUserBo authUserBo = new Gson().fromJson(user,AuthUserBo.class);
        if(!authUserBo.getToken().equals(auth.getAuthentication())){
            log.info("token匹配不上");
            result.setCode("1");
            //result.setMsg(ErrorCodeConstant.TOKEN_ERROR);
            result.setMsg("您的账号已在其他设备登录，请重新登录！");
            //删除当前token信息
            rabbitTemplate.convertAndSend(RabbitConfig.DEL_USER_TOKEN,key);
            return result;
        }
        //校验此url是否具备权限
        auth.setUserId(Long.valueOf(userId));
        auth.setType(authUserBo.getUser().getType());
        Boolean flag = uriCheckUtil.checkUri(auth);
        if(!flag){
            result.setCode("-2");
            //result.setMsg(ErrorCodeConstant.REQUEST_ILLEGALITY);
            result.setMsg("暂无权限");
            return result;
        }
        //刷新token的有效时间,利用消息队列刷新
        rabbitTemplate.convertAndSend("refreshTokenTime",auth);
        result  = Result.getDefaultTrue();
        result.setData(userId);
        return result;
    }

}
