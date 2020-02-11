package com.hqz.hzuoj.annotations;



import java.lang.annotation.*;

/**
 * 管理员登陆检测
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminLoginCheck {
}
