package com.github.oahnus.luqiancommon.annotations;

import com.github.oahnus.luqiancommon.enums.ApiLockStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by oahnus on 2020-06-10
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiLock {
    ApiLockStrategy strategy() default ApiLockStrategy.FORBIDDEN;

    // Max Wait Time
    int maxWait() default 5000;
}
