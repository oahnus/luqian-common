package com.github.oahnus.luqiancommon.config.cdn;

import com.github.oahnus.luqiancommon.dto.QiniuBatchResult;
import com.github.oahnus.luqiancommon.util.QiniuUtils;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    public void setProperties(QiniuProperties qiniuProperties) {
        this.accessKey = qiniuProperties.getAccessKey();
        this.secretKey = qiniuProperties.getSecretKey();
        this.urlPrefix = qiniuProperties.getUrlPrefix();
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

    public DefaultPutRet upload(String bucket, InputStream in, String key) throws IOException {
        byte[] bytes = IOUtils.toByteArray(in);
        return QiniuUtils.upload(bucket, bytes, key);
    }

    public DefaultPutRet upload(String bucket, byte[] bytes, String key) throws IOException {
        return QiniuUtils.upload(bucket, bytes, key);
    }

    /**
     * 批量删除文件
     * @param bucket 仓库
     * @param keyList 文件名list
     * @return qiniu result dto
     */
    public QiniuBatchResult deleteBatch(String bucket, List<String> keyList) {
        return QiniuUtils.deleteBatch(bucket, keyList);
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
        return QiniuUtils.buildAccessSign(urlRoot, fileKey, expireTimestamp);
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
