package com.ijson.rest.proxy.config;

import com.google.common.base.Strings;
import com.ijson.rest.proxy.model.ServiceConfig;
import lombok.Data;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

@Data
public class RestServiceConfig extends ServiceConfig{
    /**
     * key gray
     * value config
     */
    private Map<String,ServiceConfig> grayServices;

    public String getGrayServiceKey(String serviceKey, String grayServiceKey){
        return serviceKey+"_"+grayServiceKey;
    }

    public ServiceConfig getGrayServiceConfig(String grayService){
        if(Strings.isNullOrEmpty(grayService)|| MapUtils.isEmpty(grayServices)){
            return this;
        }
        ServiceConfig rst = grayServices.get(grayService);
        return rst;
    }
}
