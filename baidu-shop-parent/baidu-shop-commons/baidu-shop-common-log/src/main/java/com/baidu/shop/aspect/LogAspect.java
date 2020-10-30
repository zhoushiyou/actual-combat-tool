package com.baidu.shop.aspect;

import com.baidu.shop.annotation.Log;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.LogEntity;
import com.baidu.shop.mapper.MapperLog;
import com.baidu.shop.util.IPUtils;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName LogAspect
 * @Description: TODO
 * @Author zsy
 * @Date 2020/10/29
 * @Version V1.0
 **/
@Aspect
@Component
@Slf4j
public class LogAspect {

    @Resource
    private MapperLog mapperLog;

    @Autowired
    private JwtConfig jwtConfig;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Pointcut("@annotation(com.baidu.shop.annotation.Log)")
    public void operLogPoinCut() {
    }

    @AfterReturning(returning  /**
     * 记录操作日志
     * @param joinPoint 方法的执行点
     * @param result  方法返回值
     * @throws Throwable
     */ = "result", value = "operLogPoinCut()")
    public void saveOperLog(JoinPoint joinPoint, Object result) throws Throwable {

        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);

        try {
            //将返回值转换成map集合
            LogEntity logEntity = new LogEntity();
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            //获取切入点所在的方法
            Method method = signature.getMethod();
            //获取操作
            Log annotation = method.getAnnotation(Log.class);
            if (annotation != null){
                logEntity.setModel(annotation.operModul());
                logEntity.setType(annotation.operType());
                logEntity.setDescription(annotation.operDesc());
            }
            //操作时间
            logEntity.setOperationDate(Timestamp.valueOf(sdf.format(new Date())));
            String token= CookieUtils.getCookieValue(request, "MRSHOP_TOKEN");
            //操作用户
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            logEntity.setUserId(Long.valueOf(userInfo.getId()));
            logEntity.setUserCode(userInfo.getUsername());
            //操作IP
            logEntity.setIp(IPUtils.getIpAddr(request));
            //保存日志
            log.debug(logEntity + "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
