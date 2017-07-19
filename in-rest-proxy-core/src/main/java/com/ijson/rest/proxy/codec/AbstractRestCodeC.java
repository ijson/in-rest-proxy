package com.ijson.rest.proxy.codec;


import com.ijson.rest.proxy.exception.RestProxyRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by cuiyongxu on 04/01/2017.
 */
@Slf4j
public abstract class AbstractRestCodeC {
    public abstract <T> byte[] encodeArg(T obj);

    public <T> T decodeResult(int statusCode, Map<String, List<String>> headers, byte[] bytes, Class<T> clazz) {
        switch (statusCode) {
            case HttpStatus.SC_OK:
                return null;
            default:
                String msg = null;
                if (bytes != null) {
                    try {
                        msg = new String(bytes, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        log.error("decodeResult:{}", e);
                    }
                }
                throw new RestProxyRuntimeException(statusCode, msg);
        }
    }

}
