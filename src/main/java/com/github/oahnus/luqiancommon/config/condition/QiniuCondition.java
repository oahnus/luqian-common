package com.github.oahnus.luqiancommon.config.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * Created by oahnus on 2019/10/7
 * 19:43.
 */
public class QiniuCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment env = context.getEnvironment();
        String accessKey = env.getProperty("luqian.qiniu.accessKey");
        String secretKey = env.getProperty("luqian.qiniu.secretKey");
        return !StringUtils.isEmpty(accessKey) && !StringUtils.isEmpty(secretKey);
    }
}
