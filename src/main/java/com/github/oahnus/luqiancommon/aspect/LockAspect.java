package com.github.oahnus.luqiancommon.aspect;

import com.github.oahnus.luqiancommon.annotations.SyncLock;
import com.github.oahnus.luqiancommon.enums.LockType;
import com.github.oahnus.luqiancommon.exceptions.SyncLockException;
import com.github.oahnus.luqiancommon.lock.DistributedLock;
import com.github.oahnus.luqiancommon.lock.LockSpiLoader;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Created by oahnus on 2019/9/25
 * 22:52.
 */
@Component
@Aspect
@Slf4j
public class LockAspect {
    @Autowired
    RedissonClient redissonClient;

    @Pointcut("@annotation(com.github.oahnus.luqiancommon.annotations.SyncLock)")
    public void pointCut() {}

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        SyncLock syncLock = method.getAnnotation(SyncLock.class);
        LockType lockType = syncLock.lockType();
        boolean tryLock = syncLock.tryLock();
        String key = syncLock.key();
        int timeout = syncLock.timeout();
        int waitTime = syncLock.waitTime();
        TimeUnit timeUnit = syncLock.unit();

        DistributedLock lockInstance = LockSpiLoader.getLockInstance(lockType);
        boolean res;
        if (tryLock) {
            res = lockInstance.tryLock(key, waitTime, timeout, timeUnit);
        } else {
            if (timeout == 0) {
                res = lockInstance.lock(key);
            } else {
                res = lockInstance.lock(key, timeout, timeUnit);
            }
        }

        if (!res) {
            // TODO 获取锁超时策略, 抛出异常还是重试
            throw new SyncLockException("Fetch Redis Lock Failed");
        }

        Object resultVal = null;
        try {
            resultVal = pjp.proceed();
            lockInstance.unlock(key);
        } catch (Throwable throwable) {
            lockInstance.unlock(key);
            throw throwable;
        }
        return resultVal;
    }
}
