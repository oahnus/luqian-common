package com.github.oahnus.luqiancommon.exceptions;

import com.github.oahnus.luqiancommon.enums.web.RespCode;

/**
 * Created by oahnus on 2020-07-11
 * 业务异常
 */
public class BizException extends RuntimeException {
    private RespCode respCode = RespCode.INNER_SERVER_ERROR;

    public BizException() {
        super();
    }

    public BizException(RespCode code, String message) {
        super(message);
        respCode = code;
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }
}
