package com.ijson.rest.proxy.example.model;

import lombok.Data;

/**
 * Created by cuiyongxu on 17/7/15.
 */
@Data
public class BaseResult {
    protected int code;//错误码0 || 200是正常的
    protected String return_code;
    protected String return_msg;

}
