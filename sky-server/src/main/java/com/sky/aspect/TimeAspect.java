package com.sky.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

//@Component  // 加入bean容器中
@Aspect  // AOP类
@Slf4j
//@Order(1)
public class TimeAspect {

    @Pointcut("execution(* com.sky.service.*.*(..))")
    private void pc() {
    }

    @Around("pc()")  // 切入点表达式
    public Object recordTime(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录开始时间
        long begin = System.currentTimeMillis();

        String className = joinPoint.getTarget().getClass().getName();
        log.info("目标对象的类名：{}", className);

        String methodName = joinPoint.getSignature().getName();
        log.info("目标方法的方法名：{}", methodName);

        Object[] args = joinPoint.getArgs();
        log.info("目标方法运行时传入的参数：{}", Arrays.toString(args));

        // 运行原始方法
        Object result = joinPoint.proceed();

        log.info("目标方法运行的返回值：{}", result);

        // 记录结束时间，计算方法执行时间
        long end = System.currentTimeMillis();

        // joinPoint.getSignature()获取当前方法的签名
        log.info(joinPoint.getSignature() + "方法执行耗时：{}ms", end - begin);
        return result;
    }
}
