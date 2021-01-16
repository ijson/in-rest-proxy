package com.ijson.rest.proxy.example;

import com.ijson.rest.BaseTest;
import com.ijson.rest.proxy.example.model.Unifiedorder;
import com.ijson.rest.proxy.example.resource.WeixinResource;
import com.ijson.rest.proxy.util.MD5;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by cuiyongxu on 17/7/16.
 */
public class WeixinResourceTest extends BaseTest {

    @Autowired
    private WeixinResource weixinResource;

    @Test
    public void unifiedorder() {
        Unifiedorder.Arg arg = new Unifiedorder.Arg();
        arg.setAppid("wx2421b1c4370ec43b");
        arg.setAttach("支付测试");
        arg.setBody("JSAPI支付测试");
        arg.setMch_id("10000100");
        arg.setDetail("{ \"goods_detail\":[ { \"goods_id\":\"iphone6s_16G\", \"wxpay_goods_id\":\"1001\", \"goods_name\":\"iPhone6s 16G\", \"quantity\":1, \"price\":528800, \"goods_category\":\"123456\", \"body\":\"苹果手机\" }, { \"goods_id\":\"iphone6s_32G\", \"wxpay_goods_id\":\"1002\", \"goods_name\":\"iPhone6s 32G\", \"quantity\":1, \"price\":608800, \"goods_category\":\"123789\", \"body\":\"苹果手机\" } ] }");
        arg.setNonce_str(UUID.randomUUID().toString().replace("-",""));
        arg.setOpenid("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o");
        arg.setOut_trade_no(UUID.randomUUID().toString().replace("-",""));
        arg.setTotal_fee(1);
        arg.setSpbill_create_ip("123.12.12.123");
        arg.setNotify_url("http://www.weixin.qq.com/wxpay/pay.php");
        arg.setTrade_type("JSAPI");
        arg.setSign(getSign(buildParamMap(arg)));
        Unifiedorder.Result result = weixinResource.unifiedorder(arg);

        //System.out.println(result);

    }


    private SortedMap<String, Object> buildParamMap(Unifiedorder.Arg data) {
        SortedMap<String, Object> paramters = new TreeMap<String, Object>();
        Field[] fields = data.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if (null != field.get(data)) {
                    paramters.put(field.getName().toLowerCase(), field.get(data).toString());
                }
            }
        } catch (Exception e) {
            System.out.print("构建签名map错误: ");
            e.printStackTrace();
        }

        return paramters;
    }

    public static String getSign(SortedMap<String, Object> map) {
        StringBuffer buffer = new StringBuffer();
        Set<Map.Entry<String, Object>> set = map.entrySet();
        Iterator<Map.Entry<String, Object>> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String k = entry.getKey();
            Object v = entry.getValue();
            // 参数中sign、key不参与签名加密
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                buffer.append(k + "=" + v + "&");
            }
        }
        //微信支付API秘钥
        //TODO
        buffer.append("key=" + "2RoetPJsPfftEwMJqIZJf1I0dXJybW90yetGLJoe9PJ");
        String sign = MD5.mD5Encode(buffer.toString()).toUpperCase();
        return sign;
    }
}
