package com.ijson.rest.proxy.config;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.internal.LinkedTreeMap;
import com.ijson.rest.proxy.util.JsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by cuiyongxu on 20/05/2017.
 * {
 * serviceName:{
 * grayName:[grayEa,...]
 * },
 * ...
 * }
 * 如果一个企业在一个服务的灰度中，多次出现，使用最后次。如果没有存在灰度中，就返回主服务配置
 */
@Data
@Slf4j
public class ServiceGrayConfig {
    private String configName;

    private volatile Map<String, LinkedTreeMap<String, List<String>>> grayConfig = Maps.newHashMap();

    public void init() {
        if (configName != null) {
            try {
                loadConfig(ConfigFactory.getConfigToString(configName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void loadConfig(String config) {
        byte[] content = config.getBytes();
        try {
            grayConfig = JsonUtil.fromJson(new String(content, "utf-8"), new TypeToken<Map<String, Map<String, List>>>() {
            }.getType());
            log.info("grayConfig:{}", JsonUtil.toPrettyJson(grayConfig));
        } catch (UnsupportedEncodingException e) {
            log.error(configName, e);
            grayConfig = Maps.newHashMap();
        }
    }

    public ServiceGrayConfig setConfigName(String configName) {
        this.configName = configName;
        return this;
    }

    public String getGrayName(String serviceName, String ea) {
        if (grayConfig == null) {
            return null;
        }

        Map<String, List<String>> serviceGrayConfig = grayConfig.get(serviceName);
        List<String> rst = Lists.newArrayList();
        if (MapUtils.isNotEmpty(serviceGrayConfig)) {
            serviceGrayConfig.forEach((grayServiceName, grayEAs) -> {
                if (grayEAs.contains("*") || grayEAs.contains(ea)) {
                    rst.add(grayServiceName);
                }
            });
        }
        if (CollectionUtils.isNotEmpty(rst)) {
            return rst.get(rst.size() - 1);
        }
        return null;
    }

}
