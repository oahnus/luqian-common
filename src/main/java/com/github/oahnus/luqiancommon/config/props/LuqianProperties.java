package com.github.oahnus.luqiancommon.config.props;

import com.github.oahnus.luqiancommon.config.cdn.QiniuProperties;
import com.github.oahnus.luqiancommon.util.QiniuUtils;
import com.qiniu.util.StringUtils;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by oahnus on 2019/10/7
 * 18:01.
 */
@Data
@ConfigurationProperties(prefix = "luqian", ignoreUnknownFields = true)
public class LuqianProperties implements InitializingBean {
    private Boolean inject;
    private RedissonProperties redisson;
    private QiniuProperties qiniu;
    private ZkProperties zookeeper;

    @Override
    public void afterPropertiesSet() {
        if (qiniu != null && !StringUtils.isNullOrEmpty(qiniu.getAccessKey()) && !StringUtils.isNullOrEmpty(qiniu.getSecretKey())) {
            QiniuUtils.init(qiniu);
        }
    }
}
