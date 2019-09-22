package com.github.oahnus.luqiancommon.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Created by oahnus on 2019/9/21
 * 23:37.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
    String key();
    long expire() default 3600;
    TimeUnit unit() default TimeUnit.SECONDS;
}
