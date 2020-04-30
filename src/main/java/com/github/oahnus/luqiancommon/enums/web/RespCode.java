package com.github.oahnus.luqiancommon.enums.web;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by oahnus on 2019/9/20
 * 16:37.
 */
public enum RespCode {
    SUCCESS(0),
    NO_AUTH(60401),
    FORBIDDEN(60403),
    DATA_NOT_FOUND(60404),

    CLIENT_ERROR(60410),
    SERVICE_ERROR(60411),
    INNER_SERVER_ERROR(60500)
    ;
    private int code;

    RespCode(int code) {
        this.code = code;
    }

    public static RespCode getRespCode(int code) {
        for (RespCode type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }

    @JsonValue
    public int getCode() {
        return this.code;
    }
}
