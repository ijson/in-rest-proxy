
package com.ijson.rest.proxy.model;

import lombok.Data;

@Data
public class ServiceConfig {

    private String serviceKey;

    private String serviceName;

    private String address;

    private int socketTimeOut;

    private int connectionTimeOut;

}
