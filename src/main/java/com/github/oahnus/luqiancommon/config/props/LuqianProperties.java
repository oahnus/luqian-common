package com.github.oahnus.luqiancommon.config.props;

import com.github.oahnus.luqiancommon.config.cdn.QiniuProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by oahnus on 2019/10/7
 * 18:01.
 */
@Data
@ConfigurationProperties(prefix = "luqian", ignoreUnknownFields = true)
public class LuqianProperties {
    private Boolean enable;
    private RedissonProperties redisson;
    private QiniuProperties qiniu;
}
