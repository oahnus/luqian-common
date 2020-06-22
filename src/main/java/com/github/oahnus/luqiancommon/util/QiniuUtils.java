package com.github.oahnus.luqiancommon.util;

import com.alibaba.fastjson.JSON;
import com.github.oahnus.luqiancommon.config.cdn.QiniuProperties;
import com.github.oahnus.luqiancommon.dto.QiniuBatchResult;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.BatchStatus;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;
import com.qiniu.util.UrlSafeBase64;
import org.apache.commons.io.IOUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by oahnus on 2020-06-18
 */
public class QiniuUtils {
    private static final long DEFAULT_EXPIRE = 60 * 60 * 1000;

    private static String accessKey;
    private static String secretKey;
    private static String urlPrefix;

    private static Configuration cfg = new Configuration(Region.region0());

    private static UploadManager uploadManager = new UploadManager(cfg);
    private static BucketManager bucketManager;
    private static SecretKeySpec secretKeySpec;

    public static void init(QiniuProperties props) {
        if (props != null) {
            accessKey = props.getAccessKey();
            secretKey = props.getSecretKey();
            urlPrefix = props.getUrlPrefix();

            if (StringUtils.isNullOrEmpty(accessKey) || StringUtils.isNullOrEmpty(secretKey)) {
                throw new RuntimeException("QiniuUtil Init Failed. Caused By accessKey or secretKey is Empty");
            }
            byte[] sk = StringUtils.utf8Bytes(secretKey);
            secretKeySpec = new SecretKeySpec(sk, "HmacSHA1");

            Auth auth = Auth.create(accessKey, secretKey);
            bucketManager = new BucketManager(auth, cfg);
        }
    }

    public static String urlPrefix() {
        if (StringUtils.isNullOrEmpty(urlPrefix)) {
            return "";
        }
        return checkUrlSeparator(urlPrefix);
    }

    /**
     * 获取bucket上传token
     * @param bucket 文件仓库
     * @return token
     */
    public static String fetchUploadToken(String bucket) {
        return fetchResourceSecretToken(bucket, null);
    }

    public static String fetchResourceSecretToken(String bucket, String key) {
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.uploadToken(bucket, key);
    }

    public static DefaultPutRet upload(String bucket, InputStream in, String key) throws IOException {
        byte[] bytes = IOUtils.toByteArray(in);
        return upload(bucket, bytes, key);
    }
    public static DefaultPutRet upload(String bucket, byte[] bytes, String key) throws IOException {
        Auth auth = Auth.create(accessKey, secretKey);
        String token = auth.uploadToken(bucket, key);
        Response response = uploadManager.put(bytes, key, token);
        if (response.statusCode != 200) {
            throw new RuntimeException("Request Failed");
        }
        return JSON.parseObject(response.bodyString(), DefaultPutRet.class);
    }

    /**
     * 批量删除文件
     * @param bucket 仓库
     * @param keyList 文件名list
     * @return qiniu result dto
     */
    public static QiniuBatchResult deleteBatch(String bucket, List<String> keyList) {
        if (keyList == null || keyList.isEmpty()) {
            return new QiniuBatchResult().error("Qiniu Key List Cannot Empty");
        }
        if (keyList.size() > 1000) {
            return new QiniuBatchResult().error("Qiniu Key List Size Cannot More Than 1000");
        }

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
    public static String buildAccessSign(String urlRoot, String fileKey, Long expireTimestamp) {
        String downloadUrl = checkUrlSeparator(urlRoot) + fileKey + "?e=" + expireTimestamp;
        String token = sign(downloadUrl);
        return downloadUrl + "&token=" + token;
    }

    public static String buildAccessSign(String fileKey, Long expireTimestamp) {
        return buildAccessSign(urlPrefix, fileKey, expireTimestamp);
    }

    public static String buildAccessSign(String fileKey) {
        long expireTimestamp = System.currentTimeMillis() + DEFAULT_EXPIRE;
        return buildAccessSign(urlPrefix, fileKey, expireTimestamp);
    }

    private static String checkUrlSeparator(String urlPrefix) {
        if (!urlPrefix.endsWith("/")) {
            urlPrefix += "/";
        }
        return urlPrefix;
    }

    private static String sign(String url) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKeySpec);
            String encode = UrlSafeBase64.encodeToString(mac.doFinal(url.getBytes(StandardCharsets.UTF_8)));
            return accessKey + ":" + encode;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return "";
        }
    }

    private static String wrapCacheKey(String bucket, String key) {
        if (key == null) {
            return bucket;
        }
        return bucket + ":" + key;
    }
}
