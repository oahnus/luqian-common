package com.github.oahnus.luqiancommon.config.cdn;

import com.qiniu.util.Auth;

/**
 * Created by oahnus on 2019/9/20
 * 17:39.
 */
public class QiNiuClient {
    private String accessKey;
    private String secretKey;

    public void setProperties(CDNProperties cdnProperties) {
        this.accessKey = cdnProperties.getAccessKey();
        this.secretKey = cdnProperties.getSecretKey();
    }

    public String fetchUploadToken(String bucket) {
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.uploadToken(bucket);
    }

    public String secretToken(String bucket, String key) {
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.uploadToken(bucket, key);
    }
}
