package com.github.oahnus.luqiancommon.lock.impl.zk;

import com.github.oahnus.luqiancommon.config.props.ZkProperties;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;

/**
 * Created by oahnus on 2019/10/18
 * 13:40.
 */
public class ZkDistributedLockFactory {

    private static CuratorFramework client = null;

    public static CuratorFramework getClient() {
        return client;
    }

    public static void init(ZkProperties zkProperties) {
        if (client == null) {
            synchronized (ZkDistributedLockFactory.class) {
                if (client == null) {
                    doInit(zkProperties);
                }
            }
        }
    }

    private static void doInit(ZkProperties zkProperties) {
        client = CuratorFrameworkFactory
                .builder()
                .connectString(zkProperties.getConnectStr())
                .retryPolicy(new RetryOneTime(zkProperties.getRetryTime()))
                .build();
        client.start();
    }
}
