package com.github.oahnus.luqiancommon.config.cdn;

import com.github.oahnus.luqiancommon.dto.QiniuBatchResult;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.model.BatchStatus;
import com.qiniu.util.Auth;

import java.util.List;

/**
 * Created by oahnus on 2019/9/20
 * 17:39.
 */
public class QiNiuClient {
    private String accessKey;
    private String secretKey;

    public void setProperties(QiniuProperties qiniuProperties) {
        this.accessKey = qiniuProperties.getAccessKey();
        this.secretKey = qiniuProperties.getSecretKey();
    }

    public String fetchUploadToken(String bucket) {
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.uploadToken(bucket);
    }

    public String fetchResourceSecretToken(String bucket, String key) {
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.uploadToken(bucket, key);
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
}
