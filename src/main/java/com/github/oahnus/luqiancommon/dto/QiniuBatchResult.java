package com.github.oahnus.luqiancommon.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oahnus on 2019/9/24
 * 10:54.
 */
@Data
public class QiniuBatchResult {
    private boolean isAllSuccess;
    private boolean isError;
    private String errorMsg;
    private List<String> successKeyList;
    private List<String> errorKeyList;

    public QiniuBatchResult() {
        successKeyList = new ArrayList<>();
        errorKeyList = new ArrayList<>();
    }

    public QiniuBatchResult error(String errorMsg) {
        this.isError = true;
        this.isAllSuccess = false;
        this.errorMsg = errorMsg;
        return this;
    }

    public void addSuccessKey(String key) {
        this.successKeyList.add(key);
    }

    public void addErrorKey(String key) {
        this.errorKeyList.add(key);
    }

    public int successCount() {
        return this.successKeyList.size();
    }

    public int errorCount() {
        return this.errorKeyList.size();
    }
}
