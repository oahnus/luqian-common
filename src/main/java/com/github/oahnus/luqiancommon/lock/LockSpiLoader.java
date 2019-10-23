package com.github.oahnus.luqiancommon.lock;

import com.github.oahnus.luqiancommon.enums.LockType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by oahnus on 2019/9/26
 * 10:45.
 */
@Slf4j
public class LockSpiLoader {
    private static Map<LockType, DistributedLock> instancesMap = new ConcurrentHashMap<>();

    static {
        log.debug("Init Lock Spi");
        ServiceLoader<DistributedLock> locks = ServiceLoader.load(DistributedLock.class);
        locks.forEach(lock -> {
            instancesMap.putIfAbsent(lock.lockType(), lock);
        });
    }

    public static DistributedLock getLockInstance(LockType lockType) {
        return instancesMap.get(lockType);
    }

    public static void setApplicationContext(ApplicationContext context) {
        log.debug("Inject Spring App Context");
        for (Map.Entry<LockType, DistributedLock> entry : instancesMap.entrySet()) {
            entry.getValue().setApplicationContext(context);
        }
    }
}
