package com.github.oahnus.luqiancommon.config.cdn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by oahnus on 2019/9/20
 * 17:36.
 */
@Configuration
@EnableConfigurationProperties(CDNProperties.class)
public class QiNiuAutoConfiguration {
    @Autowired
    CDNProperties cdnProperties;

    @Bean
    public QiNiuClient qiNiuClient() {
        QiNiuClient qiNiuClient = new QiNiuClient();
        qiNiuClient.setProperties(cdnProperties);
        return qiNiuClient;
    }
}
