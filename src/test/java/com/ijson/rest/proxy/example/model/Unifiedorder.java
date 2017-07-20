package com.ijson.rest.proxy.example.model;

import com.ijson.rest.proxy.annotation.CDATA;
import com.ijson.rest.proxy.annotation.INField;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Data;

/**
 * Created by cuiyongxu on 17/7/16.
 */
public interface Unifiedorder {

    @Data
    class BaseArg{
        @INField(name = "appid", required = true)
        private String appid;
        private String mch_id;
    }

    @Data
    @XStreamAlias("xml")
    class Arg extends BaseArg{
        private String device_info;
        private String nonce_str;
        private String sign;
        private String sign_type;
        private String body;
        @CDATA()
        private String detail;
        private String attach;
        private String out_trade_no;
        private String fee_type;
        private Integer total_fee;
        @CDATA
        @INField(name = "spbill_create_ip",required = true)
        private String spbill_create_ip;
        private String time_start;
        private String time_expire;
        private String goods_tag;
        private String notify_url;
        private String trade_type;
        private String product_id;
        private String limit_pay;
        private String openid;
        private SceneInfo scene_info;

    }

    @Data
    class SceneInfo {
        private String id;
        private String name;
        private String area_code;
        private String address;
    }

    @Data
    @XStreamAlias("xml")
    class Result extends BaseResult {
        private String appid;
        private String mch_id;
        private String device_info;
        private String nonce_str;
        private String sign;
        private String result_code;
        private String err_code;
        private String err_code_des;
        private String trade_type;
        private String prepay_id;
        private String code_url;
    }

}
