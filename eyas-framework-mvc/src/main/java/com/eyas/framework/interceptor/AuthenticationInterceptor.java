package com.eyas.framework.interceptor;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.eyas.framework.JwtUtils;
import com.eyas.framework.annotation.WithOutToken;
import com.eyas.framework.impl.RedisServiceImpl;
import io.jsonwebtoken.Claims;
import org.springframework.data.annotation.Reference;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author Created by yixuan on 2019/7/11.
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Reference
    private RedisServiceImpl redisServiceImpl;


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        // 从 http 请求头中取出 token
        String token = httpServletRequest.getHeader("token");
        // 如果不是映射到方法直接通过
        if(!(object instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod=(HandlerMethod)object;
        Method method=handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(WithOutToken.class)) {
            WithOutToken withOutToken = method.getAnnotation(WithOutToken.class);
            if (withOutToken.required()) {
                return true;
            }
        }else{
        //检查有没有需要用户权限的注解
        // if (method.isAnnotationPresent(UserLoginToken.class)) {
        //     UserLoginToken userLoginToken = method.getAnnotation(UserLoginToken.class);
        //     if (userLoginToken.required()) {
                // 执行认证
                if (token == null) {
                    throw new RuntimeException("无token，请重新登录");
                }
                // 获取 token 中的 user id
                Claims claims;
                try {
                    claims = JwtUtils.parseJWT(token);
                } catch (JWTDecodeException j) {
                    throw new RuntimeException("401");
                }
                // 获取用户id
                String userId = claims.getId();
                String user = (String) redisServiceImpl.get(userId);
                if (user == null) {
                    throw new RuntimeException("用户不存在，请重新登录");
                }
                return true;
            // }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception {

    }
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    }
}
