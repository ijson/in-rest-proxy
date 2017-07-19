package com.ijson.rest.proxy.exception;

/**
 * Created by cuiyongxu on 17/7/17.
 */
public class RestProxyINFieldException extends RestProxyRuntimeException {
    public RestProxyINFieldException(int code) {
        super(code);
    }

    public RestProxyINFieldException(int code, String message) {
        super(code, message);
    }

    public RestProxyINFieldException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
