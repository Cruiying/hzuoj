package com.hqz.hzuoj.user.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @Author: HQZ
 * @CreateTime: 2020/5/17 16:04
 * @Description: TODO
 */
@Aspect
@Component
@Slf4j
public class ControllerAspect {
    @Pointcut("execution(public * com.hqz.hzuoj.user.controller.*.*(..))")
    private void controller() {

    }
    @Around("controller()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        long begin = System.nanoTime();
        Object obj = pjp.proceed();
        long end = System.nanoTime();
        log.info("调用controller:"+pjp+"方法：耗时："+(end - begin) / 1000000+"毫秒");
        return obj;
    }

}
