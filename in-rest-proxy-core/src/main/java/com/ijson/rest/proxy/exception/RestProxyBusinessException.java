package com.ijson.rest.proxy.exception;

/**
 * Created by cuiyongxu on 23/03/2017.
 */
public class RestProxyBusinessException extends RestProxyRuntimeException {
    public RestProxyBusinessException(int code) {
        super(code);
    }

    public RestProxyBusinessException(int code, String message) {
        super(code, message);
    }

    public RestProxyBusinessException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
