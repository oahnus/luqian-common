package com.github.oahnus.luqiancommon.dto;

import com.github.oahnus.luqiancommon.enums.web.RespCode;
import lombok.Data;

/**
 * Created by oahnus on 2019/9/20
 * 16:36.
 */
@Data
public class RespData<T> {
    private int code;
    private String msg;
    private T data;

    public RespData() {
        this.code = RespCode.SUCCESS.getCode();
        this.msg = "success";
    }

    public static <T> RespData<T> success(T data) {
        RespData<T> respData = new RespData<>();
        respData.setData(data);
        return respData;
    }
    public static <T> RespData<T> success() {
        RespData<T> respData = new RespData<>();
        respData.setData(null);
        return respData;
    }

    public static RespData error(RespCode code, String msg) {
        RespData respData = new RespData();
        respData.setCode(code.getCode());
        respData.setMsg(msg);
        return respData;
    }

    public static RespData error(int code, String msg) {
        RespData respData = new RespData();
        respData.setCode(code);
        respData.setMsg(msg);
        return respData;
    }

    public RespData<T> data(T data) {
        this.data = data;
        return this;
    }
}
