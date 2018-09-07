package com.ijson.rest.proxy;

import com.google.common.base.Strings;

import lombok.Data;

@Data
public class BeanFactory {

    private static BeanFactory instance;

    private BeanFactory() {

    }

    private static RestServiceProxyFactory factory;

    private static String configName;

    private void init() {
        if (factory == null) {
            factory = new RestServiceProxyFactory();
        }
        if (Strings.isNullOrEmpty(configName)) {
            configName = "in-rest-proxy-config";
        }
        factory.setConfigName(configName);
        factory.init();
    }

    public static <T> T getBean(Class<T> clazz) {
        BeanFactory fa = new BeanFactory();
        fa.init();
        RestServiceProxyFactoryBean<T> bean = new RestServiceProxyFactoryBean<>();
        bean.setFactory(factory);
        bean.setType(clazz);
        return bean.getObject();
    }
}
