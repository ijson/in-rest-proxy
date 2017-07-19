package com.ijson.rest.proxy.exception;

import lombok.Data;

/**
 * Created by cuiyongxu on 26/12/2016.
 */
@Data
public class RestProxyInvokeException extends RestProxyRuntimeException {

    public RestProxyInvokeException(String message) {
        super(RestProxyExceptionCode.REST_PROXY_INVOKE_ROEMOT_SERVICE, message);
    }

    public RestProxyInvokeException(String message, Throwable cause) {
        super(RestProxyExceptionCode.REST_PROXY_INVOKE_ROEMOT_SERVICE, message, cause);
    }
    public RestProxyInvokeException(Throwable cause) {
        super(RestProxyExceptionCode.REST_PROXY_INVOKE_ROEMOT_SERVICE, null,cause);
    }
}
