package com.ijson.rest.proxy.exception;

import lombok.Data;

/**
 * Created by cuiyongxu on 23/03/2017.
 */
@Data
public class RestProxyRuntimeException extends RuntimeException {
    private int code;

    public RestProxyRuntimeException(int code) {
        this.code = code;
    }

    public RestProxyRuntimeException( int code,String message) {
        super(message);
        this.code = code;
    }

    public RestProxyRuntimeException(int code,String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
