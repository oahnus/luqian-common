package com.github.oahnus.luqiancommon.dto;

import com.github.oahnus.luqiancommon.enums.web.RespCode;
import lombok.Data;

/**
 * Created by oahnus on 2019/9/20
 * 16:36.
 */
@Data
public class RespData<T> {
    private RespCode code;
    private String msg;
    private T data;

    public RespData() {
        this.code = RespCode.SUCCESS;
        this.msg = "success";
    }

    public RespData<T> data(T data) {
        this.data = data;
        return this;
    }

    public RespData error(RespCode code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }
}
