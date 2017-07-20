package com.ijson.rest.proxy.example.resource;

import com.ijson.rest.proxy.annotation.Body;
import com.ijson.rest.proxy.annotation.POST;
import com.ijson.rest.proxy.annotation.RestResource;
import com.ijson.rest.proxy.annotation.XmlParams;
import com.ijson.rest.proxy.example.model.Unifiedorder;

/**
 * Created by cuiyongxu on 17/7/16.
 */
@RestResource(value = "Weixin", desc = "微信支付平台", codec = "com.ijson.rest.proxy.example.codec.WeixinRestCodeC", contentType = "application/json")
public interface WeixinResource {


    @POST(value = "/pay/unifiedorder", desc = "统一下单", contentType = "application/json")
    Unifiedorder.Result unifiedorder(@Body Unifiedorder.Arg arg);
}
