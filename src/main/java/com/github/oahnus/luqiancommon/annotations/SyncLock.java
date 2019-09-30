package com.github.oahnus.luqiancommon.annotations;

import com.github.oahnus.luqiancommon.enums.LockType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Created by oahnus on 2019/9/25
 * 22:29.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SyncLock {
    String key();

    String prefix() default "";

    LockType lockType() default LockType.Redis;
    // 是否使用 尝试获取锁
    boolean tryLock() default false;
    // 获取锁的最大等待时间 只有tryLock为true时 生效  单位 秒
    int waitTime() default 60;
    // 超时时间， 超时后自动释放锁  单位 秒
    int timeout() default 30;
    // 时间单位
    TimeUnit unit() default TimeUnit.SECONDS;
}
