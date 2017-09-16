package com.ijson.rest.proxy.config;

import com.google.gson.reflect.TypeToken;
import com.ijson.config.ConfigFactory;
import com.ijson.rest.proxy.model.ServiceConfig;
import com.ijson.rest.proxy.util.JsonUtil;
import com.ijson.rest.proxy.util.UrlUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cuiyongxu on 20/05/2017.
 */
@Data
@Slf4j
public class ServiceConfigManager {
    private String configName;
    private static volatile Map<String, ServiceConfig> serviceConfigMaps = new HashMap<>();
    private ILoadConfigHook loadConfigHook;

    public ServiceConfigManager(String configName, ILoadConfigHook loadConfigHook) {
        this.configName = configName;
        this.loadConfigHook = loadConfigHook;
        this.init();
    }

    public static ServiceConfig getServiceConfig(String serviceKey) {
        return serviceConfigMaps.get(serviceKey);
    }

    private void init() {
         ConfigFactory.getConfig(configName,(config)->{
             String content = new String(config.getContent());
             serviceConfigMaps = JsonUtil.fromJson(content, new TypeToken<Map<String, ServiceConfig>>() {
             }.getType());
             initServiceKey(serviceConfigMaps);
             loadConfigHook.reload(serviceConfigMaps);
             log.info("log RestServiceProxyFactory {} ", configName);
        });

    }

    private void initServiceKey(Map<String, ServiceConfig> serviceConfigMaps) {
        if (MapUtils.isNotEmpty(serviceConfigMaps)) {
            serviceConfigMaps.forEach((key, serviceConfig) -> {
                //设置url
                serviceConfig.setAddress(UrlUtil.getServiceUrl(serviceConfig.getAddress()));
                serviceConfig.setServiceKey(key);
            });
        }
    }

    public interface ILoadConfigHook {
        void reload(Map<String, ServiceConfig> configMap);
    }
}
