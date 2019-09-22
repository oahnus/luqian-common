package com.github.oahnus.luqiancommon.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.MsgPackJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Created by oahnus on 2019/8/30
 * 14:06.
 */
@Configuration
@EnableConfigurationProperties(RedissonProperties.class)
@ConditionalOnClass({Redisson.class})
public class RedissonAutoConfig {
    @Autowired
    RedissonProperties redissonProperties;

    @Bean
    RedissonClient redissonClient() {
        Config config = new Config();
        String node = redissonProperties.getAddress();
        node = node.startsWith("redis://") ? node : "redis://" + node;
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(node)
                .setTimeout(redissonProperties.getTimeout())
                .setConnectionMinimumIdleSize(redissonProperties.getPool().getMinIdle());
        if (!StringUtils.isEmpty(redissonProperties.getPassword())) {
            serverConfig.setPassword(redissonProperties.getPassword());
        }
        config.setCodec(MsgPackJacksonCodec.INSTANCE);
        return Redisson.create(config);
    }
}
