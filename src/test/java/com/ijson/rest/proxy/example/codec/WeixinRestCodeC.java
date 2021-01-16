package com.ijson.rest.proxy.example.codec;

import com.ijson.rest.proxy.codec.AbstractRestCodeC;
import com.ijson.rest.proxy.example.model.BaseResult;
import com.ijson.rest.proxy.exception.RestProxyBusinessException;
import com.ijson.rest.proxy.util.JsonUtil;
import com.ijson.rest.proxy.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by cuiyongxu on 17/7/16.
 */
@Slf4j
public class WeixinRestCodeC extends AbstractRestCodeC {

    @Override
    public <T> byte[] encodeArg(T obj) {
        String str = XmlUtil.toXml(obj);
        log.info("Msg:{}", str);
        return str.getBytes();
    }

    @Override
    public <T> T decodeResult(int statusCode, Map<String, List<String>> headers, byte[] bytes, Class<T> clazz) {
        T ret = null;
        try {
            String xml = new String(bytes, "UTF-8");
            log.info("ReturnMsg : {}", xml);
            ret = XmlUtil.toBean(xml, clazz);
        } catch (UnsupportedEncodingException e) {
            log.error("decodeResult", e);
        }
        validateResult(ret);
        return ret;
    }

    private void validateResult(Object ret) {
        if (BaseResult.class.isInstance(ret)) {
            BaseResult result = (BaseResult) ret;
            if ("FAIL".equals(result.getReturn_code())) {
                throw new RestProxyBusinessException(result.getCode(), result.getReturn_msg());
            }
        }
    }

}
