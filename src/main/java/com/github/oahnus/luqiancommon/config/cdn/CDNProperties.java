package com.github.oahnus.luqiancommon.config.cdn;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by oahnus on 2019/9/20
 * 13:18.
 */
@Data
@ConfigurationProperties(prefix = "cdn.qiniu", ignoreInvalidFields = true)
public class CDNProperties {
    private String accessKey;
    private String secretKey;
}
