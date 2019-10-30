package com.github.oahnus.luqiancommon.config.props;

import lombok.Data;

/**
 * Created by oahnus on 2019/8/30
 * 14:15.
 */
@Data
public class RedissonPoolProperties {
    private int minIdle = 5;  /* 最小连接数 */
    private int poolSize = 50; /* 连接池连接数 */
}
