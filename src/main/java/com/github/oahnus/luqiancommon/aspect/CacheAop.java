package com.github.oahnus.luqiancommon.aspect;

import com.github.oahnus.luqiancommon.annotations.Cache;
import com.github.oahnus.luqiancommon.annotations.CacheClear;
import com.github.oahnus.luqiancommon.annotations.CachePut;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Created by oahnus on 2019/9/21
 * 23:37.
 */
@Component
@Aspect
public class CacheAop {
    @Autowired
    RedissonClient redissonClient;

    @Pointcut("@annotation(com.github.oahnus.luqiancommon.annotations.Cache)")
    public void cache() {}

    @Pointcut("@annotation(com.github.oahnus.luqiancommon.annotations.CacheClear)")
    public void clearCache() {}

    @Pointcut("@annotation(com.github.oahnus.luqiancommon.annotations.CachePut)")
    public void putCache() {}

    @Around("putCache()")
    public Object putCacheAround(final ProceedingJoinPoint pjp) throws Throwable {
        Object resultVal = pjp.proceed();
        CachePut cachePut = getMethodAnno(pjp, CachePut.class);

        String key = cachePut.key();
        long expire = cachePut.expire();
        TimeUnit unit = cachePut.unit();

        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(resultVal, expire, unit);
        return resultVal;
    }

    @Around("clearCache()")
    public Object clearCacheAround(ProceedingJoinPoint pjp) throws Throwable {
        Object resultVal = pjp.proceed();
        CacheClear cacheClear = getMethodAnno(pjp, CacheClear.class);

        String key = cacheClear.key();

        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.delete();
        return resultVal;
    }

    @Around("cache()")
    public Object around(final ProceedingJoinPoint pjp) throws Throwable {

        Cache cache = getMethodAnno(pjp, Cache.class);
        String key = cache.key();
        long expire = cache.expire();
        TimeUnit unit = cache.unit();

        RBucket<Object> bucket = redissonClient.getBucket(key);
        Object resultVal;

        resultVal = bucket.get();
        if (resultVal == null) {
            resultVal = pjp.proceed();
            bucket.set(resultVal);
            bucket.expire(expire, unit);
        }
        return resultVal;
    }

    private <T> T getMethodAnno(final ProceedingJoinPoint pjp, Class annoClass) {
        final MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();

        Object[] args = pjp.getArgs();
        // TODO
        try {
            method = pjp.getTarget().getClass().getDeclaredMethod(method.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return (T) method.getAnnotation(annoClass);
    }
}
