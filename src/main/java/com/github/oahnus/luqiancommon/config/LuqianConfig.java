package com.github.oahnus.luqiancommon.config;

import com.github.oahnus.luqiancommon.config.condition.RedissonCondition;
import com.github.oahnus.luqiancommon.config.props.LuqianProperties;
import com.github.oahnus.luqiancommon.config.props.RedissonProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.MsgPackJacksonCodec;
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
@Order(1)
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
        RedissonProperties redissonProperties = properties.getRedisson();
        String node = redissonProperties.getAddress();
        node = node.startsWith("redis://") ? node : "redis://" + node;
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(node)
                .setTimeout(redissonProperties.getTimeout());
        if (redissonProperties.getPool() != null) {
            serverConfig.setConnectionMinimumIdleSize(redissonProperties.getPool().getMinIdle());
        }

        if (!StringUtils.isEmpty(redissonProperties.getPassword())) {
            serverConfig.setPassword(redissonProperties.getPassword());
        }
        config.setCodec(MsgPackJacksonCodec.INSTANCE);
        return Redisson.create(config);
    }
}
