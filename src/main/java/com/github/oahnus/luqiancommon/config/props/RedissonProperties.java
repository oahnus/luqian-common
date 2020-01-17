package com.github.oahnus.luqiancommon.config.props;

import lombok.Data;

/**
 * Created by oahnus on 2019/8/30
 * 14:15.
 */
@Data
//@ConfigurationProperties(prefix = "luqian.redisson", ignoreUnknownFields = true)
public class RedissonProperties {
    private String address; //连接地址

    private int database = 0;

    /**
     * 等待节点回复命令的时间。该时间从命令发送成功时开始计时
     */
    private int timeout = 3000;
    private int connectTimeout = 10000;

    private String password;

    private RedissonPoolProperties pool;
}
