package com.hqz.hzuoj.annotations;


import java.lang.annotation.*;

/**
 *普通用户登录检查
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserLoginCheck {
}
