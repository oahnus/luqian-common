package com.github.oahnus.luqiancommon.config.cdn;

import com.github.oahnus.luqiancommon.dto.QiniuBatchResult;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.model.BatchStatus;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;
import com.qiniu.util.UrlSafeBase64;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by oahnus on 2019/9/20
 * 17:39.
 */
@Slf4j
public class QiniuClient {
    private String accessKey;
    private String secretKey;
    private String urlPrefix = "";

    private static Map<String, TokenEntity> TOKEN_CACHE = new ConcurrentHashMap<>();
    private static final long TOKEN_EXPIRE = 60 * 60 * 1000; // 1h
    private static final long CLEAN_INTERVAL = 5 * 60; // 5 min

    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private static BucketManager bucketManager;
    private SecretKeySpec secretKeySpec;

    public void setProperties(QiniuProperties qiniuProperties) {
        this.accessKey = qiniuProperties.getAccessKey();
        this.secretKey = qiniuProperties.getSecretKey();
        this.urlPrefix = qiniuProperties.getUrlPrefix();

        byte[] sk = StringUtils.utf8Bytes(secretKey);
        secretKeySpec = new SecretKeySpec(sk, "HmacSHA1");
    }

    /**
     * 获取资源管理器
     * @return 资源管理器
     */
    public BucketManager getBucketManager() {
        if (bucketManager == null) {
            synchronized (this) {
                if (bucketManager == null) {
                    Auth auth = Auth.create(accessKey, secretKey);
                    Configuration cfg = new Configuration(Region.region0());
                    bucketManager = new BucketManager(auth, cfg);
                }
            }
        }
        return bucketManager;
    }

    public String getUrlPrefix() {
        if (StringUtils.isNullOrEmpty(this.urlPrefix)) {
            return this.urlPrefix;
        }
        return checkUrlSeparator(this.urlPrefix);
    }

    /**
     * 获取bucket上传token
     * @param bucket 文件仓库
     * @return token
     */
    public String fetchUploadToken(String bucket) {
        return this.fetchResourceSecretToken(bucket, null);
    }

    public String fetchResourceSecretToken(String bucket, String key) {
        String cacheKey = wrapCacheKey(bucket, key);
        TokenEntity tokenEntity = TOKEN_CACHE.get(cacheKey);
        if (tokenEntity != null) {
            long expire = tokenEntity.getExpire();
            long now = System.currentTimeMillis();
            if (now < expire) {
                return tokenEntity.getToken();
            } else {
                // 超时移除
                TOKEN_CACHE.remove(cacheKey);
            }
        }

        Auth auth = Auth.create(accessKey, secretKey);
        String token = auth.uploadToken(bucket, key);
        long expire = System.currentTimeMillis() + TOKEN_EXPIRE;
        TOKEN_CACHE.put(cacheKey, new TokenEntity(token, expire));
        return token;
    }

    /**
     * 批量删除文件
     * @param bucket 仓库
     * @param keyList 文件名list
     * @return qiniu result dto
     */
    public QiniuBatchResult deleteBatch(String bucket, List<String> keyList) {
        if (keyList == null || keyList.isEmpty()) {
            return new QiniuBatchResult().error("Qiniu Key List Cannot Empty");
        }
        if (keyList.size() > 1000) {
            return new QiniuBatchResult().error("Qiniu Key List Size Cannot More Than 1000");
        }

        BucketManager bucketManager = getBucketManager();

        QiniuBatchResult result = new QiniuBatchResult();
        try {
            //单次批量请求的文件数量不得超过1000
            String[] keyArr = keyList.toArray(new String[]{});
            BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
            batchOperations.addDeleteOp(bucket, keyArr);
            Response response = bucketManager.batch(batchOperations);
            BatchStatus[] batchStatusList = response.jsonToObject(BatchStatus[].class);

            for (int i = 0; i < keyArr.length; i++) {
                BatchStatus status = batchStatusList[i];
                String key = keyArr[i];
                if (status.code == 200) {
                    result.addSuccessKey(key);
                } else {
                    result.addErrorKey(key);
                }
            }
            return result;
        } catch (QiniuException e) {
            e.printStackTrace();
            return result.error(e.getMessage());
        }
    }

    /**
     * 获取私有空间文件访问token, 与fetchResourceSecretToken功能相同
     * 此方法为原生代码实现，用于手动获取token
     * token 会缓存, 已有缓存的token在未过期时，不会根据expireTimestamp更新过期时间
     * @param urlRoot 私用空间域名
     * @param fileKey 文件名
     * @param expireTimestamp 过期时间
     * @return 带有token的完整url [http://cndn.xxx.com/filename?e=expireTimestamp&token=xxx]
     */
    public String buildAccessSign(String urlRoot, String fileKey, Long expireTimestamp) {
        String key = wrapCacheKey(null, fileKey);
        TokenEntity entity = TOKEN_CACHE.get(key);
        String token ;
        String downloadUrl = checkUrlSeparator(urlRoot) + fileKey + "?e=" + expireTimestamp;
        if (entity == null) {
            token = sign(downloadUrl);
            entity = new TokenEntity(token, expireTimestamp);
            TOKEN_CACHE.put(key, entity);
        } else {
            token = entity.getToken();
        }
        return downloadUrl + "&token=" + token;
    }

    public String buildAccessSign(String fileKey, Long expireTimestamp) {
        return buildAccessSign(urlPrefix, fileKey, expireTimestamp);
    }

    public String buildAccessSign(String fileKey) {
        long expireTimestamp = System.currentTimeMillis() + TOKEN_EXPIRE;
        return buildAccessSign(urlPrefix, fileKey, expireTimestamp);
    }

    /**
     * 启动token清理器
     */
    public void startCleaner() {
        log.debug("[QiniuClient].static initializer - Init Cache Cleaner");
        executor.scheduleAtFixedRate(() -> {
            log.debug("[QiniuClient].Cleaner - Run Cleaner");
            cleanCacheMap();
        }, 5, CLEAN_INTERVAL, TimeUnit.SECONDS);
    }

    private String checkUrlSeparator(String urlPrefix) {
        if (!urlPrefix.endsWith("/")) {
            urlPrefix += "/";
        }
        return urlPrefix;
    }

    private String sign(String url) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKeySpec);
            String encode = UrlSafeBase64.encodeToString(mac.doFinal(url.getBytes(StandardCharsets.UTF_8)));
            return this.accessKey + ":" + encode;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return "";
        }
    }

    private String wrapCacheKey(String bucket, String key) {
        if (key == null) {
            return bucket;
        }
        return bucket + ":" + key;
    }

    /**
     * 清除过期token
     */
    private static void cleanCacheMap() {
        if (TOKEN_CACHE.isEmpty()) {
            return;
        }
        Set<Map.Entry<String, TokenEntity>> entrySet = TOKEN_CACHE.entrySet();
        for (Map.Entry<String, TokenEntity> entry : entrySet) {
            String key = entry.getKey();
            TokenEntity entity = entry.getValue();
            long expire = entity.getExpire();
            long now = System.currentTimeMillis();
            if (now >= expire) {
                TOKEN_CACHE.remove(key);
            }
        }
    }

    @Data
    private static class TokenEntity {
        private String token;
        private long expire;

        public TokenEntity() {}

        public TokenEntity(String token, long expire) {
            this.token = token;
            this.expire = expire;
        }
    }
}
