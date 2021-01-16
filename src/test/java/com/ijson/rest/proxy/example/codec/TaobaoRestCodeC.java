package com.ijson.rest.proxy.example.codec;

import com.alibaba.fastjson.JSONObject;
import com.ijson.rest.proxy.codec.AbstractRestCodeC;
import com.ijson.rest.proxy.example.model.BaseResult;
import com.ijson.rest.proxy.exception.RestProxyBusinessException;
import com.ijson.rest.proxy.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by cuiyongxu on 17/3/6.
 */
@Slf4j
public class TaobaoRestCodeC extends AbstractRestCodeC {
    @Override
    public <T> byte[] encodeArg(T obj) {
        return JsonUtil.toJson(obj).getBytes();
    }

    @Override
    public <T> T decodeResult(int statusCode, Map<String, List<String>> headers, byte[] bytes, Class<T> clazz) {
        super.decodeResult(statusCode, headers, bytes, clazz);
        T ret = null;
        try {
            String json = new String(bytes, "UTF-8");
            log.info("ReturnMsg : {}", json);
            if (isJson(json)) {
                ret = JsonUtil.fromJson(json, clazz);
            }
        } catch (UnsupportedEncodingException e) {
            log.error("decodeResult", e);
        }
        validateResult(ret);
        return ret;
    }

    public boolean isJson(String jsonString) {
        try {
            JSONObject.parse(jsonString);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void validateResult(Object ret) {
        if (BaseResult.class.isInstance(ret)) {
            BaseResult result = (BaseResult) ret;
            if (!(result.getCode() == 0)) {
                throw new RestProxyBusinessException(result.getCode(), "获取数据异常");
            }
        }
    }
}
