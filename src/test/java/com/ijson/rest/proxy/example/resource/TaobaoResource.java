package com.ijson.rest.proxy.example.resource;

import com.ijson.rest.proxy.annotation.*;
import com.ijson.rest.proxy.example.model.GetIP;
import com.ijson.rest.proxy.example.model.PostIP;

import java.util.Map;

/**
 * Created by cuiyongxu on 17/7/15.
 */
@RestResource(value = "Taobao", desc = "淘宝开发平台", codec = "com.ijson.rest.proxy.example.codec.TaobaoRestCodeC", contentType = "application/json")
public interface TaobaoResource {

    @Deprecated
    @GET(value = "/service/getIpInfo.php?ip={ip}", contentType = "application/json")
    GetIP.Result getIPInfo(@HeaderMap Map<String, String> header, @PathParams Map<String, String> params);

    @POST(value = "/outGetIpInfo", contentType = "application/json")
    PostIP.Result getIPPostInfo(@HeaderMap Map<String, String> header, @QueryParamsMap Map<String, String> params);
}
