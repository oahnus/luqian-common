package com.github.oahnus.luqiancommon.config;

import com.github.oahnus.luqiancommon.config.cdn.QiNiuClient;
import com.github.oahnus.luqiancommon.config.cdn.QiniuProperties;
import com.github.oahnus.luqiancommon.config.condition.QiniuCondition;
import com.github.oahnus.luqiancommon.config.props.LuqianProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Created by oahnus on 2019/9/20
 * 17:36.
 */
@Configuration
@EnableConfigurationProperties(LuqianProperties.class)
public class CDNConfiguration {
    @Autowired
    LuqianProperties properties;

    @Bean
    @Conditional(QiniuCondition.class)
    public QiNiuClient qiNiuClient() {
        QiNiuClient qiNiuClient = new QiNiuClient();
        QiniuProperties qiniuProperties = properties.getQiniu();
        qiNiuClient.setProperties(qiniuProperties);
        return qiNiuClient;
    }
}
