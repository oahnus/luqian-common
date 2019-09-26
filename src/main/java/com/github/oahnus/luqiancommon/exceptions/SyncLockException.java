package com.github.oahnus.luqiancommon.exceptions;

/**
 * Created by oahnus on 2019/9/26
 * 10:56.
 */
public class SyncLockException extends RuntimeException {
    public SyncLockException() {
        super();
    }

    public SyncLockException(String message) {
        super(message);
    }
}
