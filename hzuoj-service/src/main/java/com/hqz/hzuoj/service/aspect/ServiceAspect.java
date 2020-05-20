package com.hqz.hzuoj.service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;


/**
 * @Author: HQZ
 * @CreateTime: 2020/5/17 16:04
 * @Description: TODO
 */
@Aspect
@Component
@Slf4j
public class ServiceAspect {


    @Pointcut("execution(public * com.hqz.hzuoj.service.service.impl.*.*(..))")
    private void service() {

    }


    @Around("service()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        long begin = System.nanoTime();
        Object obj = pjp.proceed();
        long end = System.nanoTime();
        log.info("调用Service:"+pjp+"方法：耗时："+(end - begin) / 1000000+"毫秒");
        return obj;
    }

}
