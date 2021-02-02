package com.gaga.gateway.tokenFilter;

import com.alibaba.fastjson.JSONObject;
import com.gaga.gateway.utils.Json;
import com.gaga.gateway.utils.RedisUtils;
import com.gaga.gateway.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

/**
 * 网关层token全局过滤器
 * @Author fuGaga
 * @Date 2021/1/5 17:00
 * @Version 1.0
 */
@Component
public class TokenFilter implements GlobalFilter, Ordered {

    @Value("${filter.url}")
    private String filterUrl;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 获取当前请求路径
        String path = request.getURI().getPath().substring(request.getURI().getPath().lastIndexOf("/"));
        HttpMethod httpMethod = request.getMethod();
        //判断是否为忽略路径...
        if(filterUrl.contains(path)){
            return chain.filter(exchange);
        }
        String userToken = request.getHeaders().getFirst("userToken");
        if(null==userToken){
            DataBuffer dataBuffer = setResponseDataBuffer(response,"访问失败,token不存在！");
            return response.writeWith(Mono.just(dataBuffer));
        }
        String resultJson = (String) RedisUtils.getValue(userToken);
        if(null == resultJson){
            DataBuffer dataBuffer = setResponseDataBuffer(response,"访问失败,token未授权！");
            return response.writeWith(Mono.just(dataBuffer));
        }

        return chain.filter(exchange);
    }

    private DataBuffer setResponseDataBuffer(ServerHttpResponse response,String msg){
        Json json = new Json();
        json.setSuccess(false);
        json.setMsg(msg);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        byte[] bytes = JSONObject.toJSONBytes(json);
        DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);
        return dataBuffer;
    }

    @Override
    public int getOrder() {
        return -999;
    }
}
