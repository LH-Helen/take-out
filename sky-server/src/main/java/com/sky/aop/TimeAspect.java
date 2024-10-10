package com.sky.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component  // 加入bean容器中
@Aspect  // AOP类
@Slf4j
//@Order(1)
public class TimeAspect {

    @Pointcut("execution(* com.sky.service.*.*(..))")
    private void pc(){}

    @Around("pc()")  // 切入点表达式
    public Object recordTime(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录开始时间
        long begin = System.currentTimeMillis();

        // 运行原始方法
        Object result = joinPoint.proceed();

        // 记录结束时间，计算方法执行时间
        long end = System.currentTimeMillis();

        // joinPoint.getSignature()获取当前方法的签名
        log.info(joinPoint.getSignature() + "方法执行耗时：{}ms", end-begin);
        return result;
    }
}
