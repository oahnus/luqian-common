package com.github.oahnus.luqiancommon.lock;

import com.github.oahnus.luqiancommon.enums.LockType;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Created by oahnus on 2019/9/26
 * 10:45.
 */
public class LockSpiLoader {
    private static Map<LockType, DistributedLock> instancesMap = new HashMap<>();

    static {
       ServiceLoader<DistributedLock> locks = ServiceLoader.load(DistributedLock.class);
       locks.forEach(lock -> {
           instancesMap.put(lock.lockType(), lock);
       });
   }

    public static DistributedLock getLockInstance(LockType lockType) {
        return instancesMap.get(lockType);
    }
}
