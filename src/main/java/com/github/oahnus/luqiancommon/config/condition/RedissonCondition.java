package com.github.oahnus.luqiancommon.config.condition;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by oahnus on 2019/10/7
 * 18:05.
 */
@Slf4j
public class RedissonCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment env = context.getEnvironment();
        String address = env.getProperty("luqian.redisson.address");
        return address != null && !address.trim().equals("");
    }
}
