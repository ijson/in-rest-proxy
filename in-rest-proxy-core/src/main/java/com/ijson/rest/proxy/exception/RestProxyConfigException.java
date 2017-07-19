package com.ijson.rest.proxy.exception;

import lombok.Data;

/**
 * Created by cuiyongxu on 26/12/2016.
 */
@Data
public class RestProxyConfigException extends RestProxyRuntimeException {

    public RestProxyConfigException(String message) {
        super(RestProxyExceptionCode.REST_PROXY_CONFIG, message);
    }

    public RestProxyConfigException(String message, Throwable cause) {
        super(RestProxyExceptionCode.REST_PROXY_CONFIG, message, cause);
    }
}
