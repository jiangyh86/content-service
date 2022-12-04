package com.jyh.content.auth;

import com.jyh.content.utils.JwtOperator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author jiangyiheng
 * @date 2022-10-04 14:35
 */
@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AuthAspect {

    private final JwtOperator jwtOperator;

    @Around("@annotation(com.jyh.content.auth.CheckLogin)")
    public Object checkLogin(ProceedingJoinPoint point)throws Throwable{
        checkToken();
        return point.proceed();
    }
    @Around("@annotation(com.jyh.content.auth.CheckAuthorization)")
    public Object checkAuthorization(ProceedingJoinPoint point)throws Throwable{
        try {
            //验证token的合法
            this.checkToken();
            //验证用户角色是否匹配
            HttpServletRequest request = getHttpServletRequest();
            String role = (String)request.getAttribute("role");

            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            CheckAuthorization annotation = method.getAnnotation(CheckAuthorization.class);

            String value = annotation.value();

            if (!Objects.equals(role, value)){
                throw new SecurityException("用户无权访问");
            }
        } catch (RuntimeException e) {
            throw new SecurityException("用户无权访问");
        }
        return point.proceed();
    }


    private void checkToken(){
        try {
            //从header获取token
            HttpServletRequest request = getHttpServletRequest();
            String token = request.getHeader("x-token");
            log.info("token："+token);
            //检验token是否合法&是否过期，合法就立即放行
            Boolean bool = jwtOperator.validateToken(token);
            if (!bool){
                throw new SecurityException("Token 不合法");
            }
            //如果检验成功，把用户信息放大Attribute中去
            Claims claims = jwtOperator.getClaimsFromToken(token);
            request.setAttribute("id",claims.get("id"));
            request.setAttribute("nickname",claims.get("nickname"));
            request.setAttribute("role",claims.get("role"));
        } catch (RuntimeException e) {
            throw new SecurityException("Token不合法");
        }
    }


    private HttpServletRequest getHttpServletRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes=(ServletRequestAttributes) requestAttributes;
        assert attributes != null;
        return attributes.getRequest();
    }
}
