package com.ijson.rest.proxy.config;

import com.google.gson.reflect.TypeToken;
import com.ijson.rest.proxy.model.ServiceConfig;
import com.ijson.rest.proxy.util.JsonUtil;
import com.ijson.rest.proxy.util.UrlUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cuiyongxu on 20/05/2017.
 */
@Data
@Slf4j
public class ServiceConfigManager {
    private String configName;
    private static volatile Map<String, RestServiceConfig> serviceConfigMaps = new HashMap<>();
    private ILoadConfigHook loadConfigHook;
    private static volatile ServiceGrayConfig serviceGrayConfig = new ServiceGrayConfig();

    public ServiceConfigManager(String configName, ILoadConfigHook loadConfigHook) {
        this.configName = configName;
        this.loadConfigHook = loadConfigHook;
        this.init();
    }

    public static ServiceConfig getServiceConfig(String serviceKey) {
        return serviceConfigMaps.get(serviceKey);
    }

    private void init() {
        try {
            String content = ConfigFactory.getConfigToString(configName);
            serviceConfigMaps = JsonUtil.fromJson(content, new TypeToken<Map<String, RestServiceConfig>>() {
            }.getType());
            initServiceKey(serviceConfigMaps);
            loadConfigHook.reload(serviceConfigMaps);
            log.info("log RestServiceProxyFactory {} ", configName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initServiceKey(Map<String, RestServiceConfig> serviceConfigMaps) {
        if (MapUtils.isNotEmpty(serviceConfigMaps)) {
            serviceConfigMaps.forEach((key, serviceConfig) -> {
                //设置url
                serviceConfig.setAddress(UrlUtil.getServiceUrl(serviceConfig.getAddress()));
                serviceConfig.setServiceKey(key);
                if (MapUtils.isNotEmpty(serviceConfig.getGrayServices())) {
                    serviceConfig.getGrayServices().forEach((serviceKey, grayServiceConfig) -> {
                        grayServiceConfig.setServiceKey(serviceConfig.getGrayServiceKey(
                                key, serviceKey
                        ));
//                      设置url
                        grayServiceConfig.setAddress(UrlUtil.getServiceUrl(grayServiceConfig.getAddress()));
                    });
                }
            });
        }
    }

    public interface ILoadConfigHook {
        void reload(Map<String, RestServiceConfig> configMap);
    }
}
