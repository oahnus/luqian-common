package com.github.oahnus.luqiancommon.lock.impl.zk;

import com.github.oahnus.luqiancommon.config.props.LuqianProperties;
import com.github.oahnus.luqiancommon.config.props.ZkProperties;
import com.github.oahnus.luqiancommon.enums.LockType;
import com.github.oahnus.luqiancommon.lock.DistributedLock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by oahnus on 2019/10/18
 * 11:26.
 */
public class ZkDistributedLock implements DistributedLock {
    private String applicationName;
    private CuratorFramework client;
    private Map<String, InterProcessLock> lockInstanceMap = new ConcurrentHashMap<>();
    private String lockBasePath = "";
    private static final long DEFAULT_WAIT_TIME = 5000; // 5000 ms

    @Override
    public boolean lock(String key) {
        if (client == null) {
            return false;
        }
        String wrapKey = wrapKey(key);
        InterProcessLock lock = new InterProcessSemaphoreMutex(client, wrapKey);
        try {
            lock.acquire();
            lockInstanceMap.put(wrapKey, lock);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean lock(String key, int timeout, TimeUnit unit) {
        return lock(key);
    }

    @Override
    public boolean tryLock(String key) {
        if (client == null) {
            return false;
        }
        String wrapKey = wrapKey(key);
        InterProcessLock lock = new InterProcessSemaphoreMutex(client, wrapKey);
        try {
            lock.acquire(DEFAULT_WAIT_TIME, TimeUnit.MICROSECONDS);
            lockInstanceMap.put(wrapKey, lock);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean tryLock(String key, int waittime, int timeout, TimeUnit unit) {
        if (client == null) {
            return false;
        }
        String wrapKey = wrapKey(key);
        InterProcessLock lock = new InterProcessSemaphoreMutex(client, wrapKey);
        try {
            // acquire(time, unit)  time是等待时间
            lock.acquire(waittime, unit);
            lockInstanceMap.put(wrapKey, lock);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean unlock(String key) {
        if (client == null) {
            return false;
        }
        try {
            String wrapKey = wrapKey(key);
            InterProcessLock lock = lockInstanceMap.get(wrapKey);

            if (lock != null && lock.isAcquiredInThisProcess()) {
                lock.release();
                lockInstanceMap.remove(wrapKey);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public LockType lockType() {
        return LockType.ZooKeeper;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        String applicationName = context.getEnvironment()
                .getProperty("spring.application.name");
        if (StringUtils.isEmpty(applicationName)) {
            applicationName = "SpringBootApplication";
        }
        this.applicationName = applicationName;

        LuqianProperties luqianProperties = context.getBean(LuqianProperties.class);
        Boolean enable = luqianProperties.getEnable();
        if (!enable) {
            return;
        }
        ZkProperties zkProperties = luqianProperties.getZookeeper();
        if (zkProperties == null || StringUtils.isEmpty(zkProperties.getConnectStr())) {
            return;
        }

        ZkDistributedLockFactory.init(zkProperties);
        this.client = ZkDistributedLockFactory.getClient();
        this.lockBasePath = ZkDistributedLockFactory.getLockBasePath();
    }

    private String wrapKey(String key) {
        return lockBasePath + "/" + key;
    }
}
