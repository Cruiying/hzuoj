package com.hqz.hzuoj.annotations;



import java.lang.annotation.*;

/**
 * 管理员登陆检测
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginWeb {
}
