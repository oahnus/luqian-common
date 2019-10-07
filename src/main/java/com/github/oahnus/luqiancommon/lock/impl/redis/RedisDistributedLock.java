package com.github.oahnus.luqiancommon.lock.impl.redis;

import com.github.oahnus.luqiancommon.enums.LockType;
import com.github.oahnus.luqiancommon.lock.DistributedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * Created by oahnus on 2019/9/26
 * 10:41.
 */
public class RedisDistributedLock implements DistributedLock {
    private RedissonClient redissonClient;

    public RedisDistributedLock() {

    }

    @Override
    public boolean lock(String key) {
        RLock lock = redissonClient.getLock(key);
        lock.lock();
        return true;
    }

    @Override
    public boolean lock(String key, int timeout, TimeUnit unit) {
        RLock lock = redissonClient.getLock(key);
        lock.lock(timeout, unit);
        return true;
    }

    @Override
    public boolean tryLock(String key) {
        RLock lock = redissonClient.getLock(key);
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(String key, int waittime, int timeout, TimeUnit unit) {
        RLock lock = redissonClient.getLock(key);
        try {
            return lock.tryLock(waittime, timeout, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean unlock(String key) {
        RLock lock = redissonClient.getLock(key);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
            return true;
        }
        return false;
    }

    @Override
    public LockType lockType() {
        return LockType.Redis;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        if(context.containsBean(RedissonClient.class.getName())) {
            this.redissonClient = context.getBean(RedissonClient.class);
        }
    }
}
