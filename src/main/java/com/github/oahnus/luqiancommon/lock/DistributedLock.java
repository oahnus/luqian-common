package com.github.oahnus.luqiancommon.lock;

import com.github.oahnus.luqiancommon.enums.LockType;

import java.util.concurrent.TimeUnit;

/**
 * Created by oahnus on 2019/9/26
 * 10:39.
 */
public interface DistributedLock {
    boolean lock(String key);
    boolean lock(String key, int timeout, TimeUnit unit);
    boolean tryLock(String key);
    boolean tryLock(String key, int waittime, int timeout, TimeUnit unit);
    boolean unlock(String key);

    LockType lockType();
}
