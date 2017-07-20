package com.ijson.rest.proxy.example.resource;

import com.ijson.rest.proxy.annotation.GET;
import com.ijson.rest.proxy.annotation.HeaderMap;
import com.ijson.rest.proxy.annotation.PathParams;
import com.ijson.rest.proxy.annotation.RestResource;
import com.ijson.rest.proxy.example.model.GetIP;

import java.util.Map;

/**
 * Created by cuiyongxu on 17/7/15.
 */
@RestResource(value = "Taobao", desc = "淘宝开发平台", codec = "com.ijson.rest.proxy.example.codec.TaobaoRestCodeC", contentType = "application/json")
public interface TaobaoResource {

    @GET(value = "/service/getIpInfo.php?ip={ip}", contentType = "application/json")
    GetIP.Result getIPInfo(@HeaderMap Map<String, String> header, @PathParams Map<String, String> params);
}
