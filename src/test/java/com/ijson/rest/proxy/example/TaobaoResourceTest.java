package com.ijson.rest.proxy.example;

import com.google.common.collect.Maps;
import com.ijson.rest.BaseTest;
import com.ijson.rest.proxy.example.model.GetIP;
import com.ijson.rest.proxy.example.model.PostIP;
import com.ijson.rest.proxy.example.resource.TaobaoResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by cuiyongxu on 17/7/15.
 */

public class TaobaoResourceTest extends BaseTest {

    @Autowired
    private TaobaoResource taobaoResource;


    @Test()
    public void getIPInfo() {
        Map<String, String> header = Maps.newHashMap();
        header.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");

        Map<String, String> params = Maps.newHashMap();
        params.put("ip", "113.46.165.72");

        GetIP.Result result = taobaoResource.getIPInfo(header, params);
        System.out.println(result);
    }

    @Test()
    public void getIPPostInfo() {
        Map<String, String> header = Maps.newHashMap();
        header.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");

        Map<String, String> params = Maps.newHashMap();
        params.put("ip", "113.46.165.72");
        params.put("accessKey", "alibaba-inc");

        PostIP.Result result = taobaoResource.getIPPostInfo(header, params);
        System.out.println(result);
    }
}
