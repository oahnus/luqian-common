package com.github.oahnus.luqiancommon.config;

import com.github.oahnus.luqiancommon.config.condition.RedissonCondition;
import com.github.oahnus.luqiancommon.config.props.LuqianProperties;
import com.github.oahnus.luqiancommon.config.props.RedissonProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

/**
 * Created by oahnus on 2019/10/7
 * 17:59.
 */
@Configuration
@EnableConfigurationProperties(LuqianProperties.class)
@ConditionalOnProperty(prefix = "luqian", value = "enable", havingValue = "true")
public class LuqianConfig {
    @Autowired
    LuqianProperties properties;

    @Bean
    @Order(1)
    @ConditionalOnClass({Redisson.class})
    @Conditional(RedissonCondition.class)
    RedissonClient redissonClient() {
        Config config = new Config();
        RedissonProperties redissonProp = properties.getRedisson();

        String node = redissonProp.getAddress();
        node = node.startsWith("redis://") ? node : "redis://" + node;

        int database = redissonProp.getDatabase();

        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(node)
                .setDatabase(database)
                .setConnectTimeout(redissonProp.getConnectTimeout())
                .setTimeout(redissonProp.getTimeout());

        if (redissonProp.getPool() != null) {
            serverConfig.setConnectionMinimumIdleSize(redissonProp.getPool().getMinIdle());
            serverConfig.setConnectionPoolSize(redissonProp.getPool().getPoolSize());
        }
        if (!StringUtils.isEmpty(redissonProp.getPassword())) {
            serverConfig.setPassword(redissonProp.getPassword());
        }
        return Redisson.create(config);
    }
}
