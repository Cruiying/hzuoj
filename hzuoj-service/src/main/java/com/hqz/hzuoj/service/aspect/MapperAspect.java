package com.hqz.hzuoj.service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @Author: HQZ
 * @CreateTime: 2020/5/17 17:21
 * @Description: TODO
 */
@Component
@Slf4j
@Aspect
public class MapperAspect {
    @Pointcut("execution(public * com.hqz.hzuoj.mapper.*.*.*(..))")
    private void mapper() {

    }

    @Around("mapper()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        long begin = System.nanoTime();
        Object obj = pjp.proceed();
        long end = System.nanoTime();
        log.info("调用Mapper:"+pjp+"方法：耗时："+(end - begin) / 1000000+"毫秒");
        return obj;
    }
}
