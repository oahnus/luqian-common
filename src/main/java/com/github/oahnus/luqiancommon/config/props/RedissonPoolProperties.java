package com.github.oahnus.luqiancommon.config.props;

import lombok.Data;

/**
 * Created by oahnus on 2019/8/30
 * 14:15.
 */
@Data
public class RedissonPoolProperties {
    private int maxIdle; /**连接池中的最大空闲连接**/

    private int minIdle = 5;  /**最小连接数**/

    private int maxActive;/**连接池最大连接数**/

    private int maxWait; /* 连接池最大阻塞等待时间 */

}
