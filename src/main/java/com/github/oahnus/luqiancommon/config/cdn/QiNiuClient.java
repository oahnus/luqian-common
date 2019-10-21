package com.github.oahnus.luqiancommon.config.cdn;

import com.github.oahnus.luqiancommon.dto.QiniuBatchResult;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.model.BatchStatus;
import com.qiniu.util.Auth;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by oahnus on 2019/9/20
 * 17:39.
 * TODO 超时是在get时做的检查，CACHE_MAP可能会越来越大，需要定时做清理
 */
@Slf4j
public class QiNiuClient {
    private String accessKey;
    private String secretKey;

    private static Map<String, TokenEntity> TOKEN_CACHE = new ConcurrentHashMap<>();
    private static final long TOKEN_EXPIRE = 60 * 60 * 1000;

    private static final long CLEAN_INTERVAL = 1; // 1 min

    static {
        Thread cleaner = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MINUTES.sleep(CLEAN_INTERVAL);
                    cleanCacheMap();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "TokenCacheMapCleaner");
        cleaner.start();
    }

    public void setProperties(QiniuProperties qiniuProperties) {
        this.accessKey = qiniuProperties.getAccessKey();
        this.secretKey = qiniuProperties.getSecretKey();
    }

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

    public QiniuBatchResult deleteBatch(String bucket, List<String> keyList) {
        if (keyList == null || keyList.isEmpty()) {
            return new QiniuBatchResult().error("Qiniu Key List Cannot Empty");
        }
        if (keyList.size() > 1000) {
            return new QiniuBatchResult().error("Qiniu Key List Size Cannot More Than 1000");
        }

        Auth auth = Auth.create(accessKey, secretKey);
        Configuration cfg = new Configuration(Region.region0());
        BucketManager bucketManager = new BucketManager(auth, cfg);
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

    private String wrapCacheKey(String bucket, String key) {
        if (key == null) {
            return bucket;
        }
        return bucket + ":" + key;
    }



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
