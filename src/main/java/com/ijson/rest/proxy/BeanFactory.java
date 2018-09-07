package com.ijson.rest.proxy;

import com.google.common.base.Strings;

import lombok.Data;

@Data
public class BeanFactory {

    private static BeanFactory instance;

    private BeanFactory() {

    }

    private static synchronized BeanFactory getInstance() {
        if (instance == null) {
            instance = new BeanFactory();
        }
        return instance;
    }

    private static RestServiceProxyFactory factory;

    private static String configName;

    private synchronized void init() {
        if (factory == null) {
            factory = new RestServiceProxyFactory();
            if (Strings.isNullOrEmpty(configName)) {
                configName = "in-rest-proxy-config";
            }
            factory.setConfigName(configName);
            factory.init();
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        BeanFactory.getInstance().init();
        RestServiceProxyFactoryBean<T> bean = new RestServiceProxyFactoryBean<>();
        bean.setFactory(factory);
        bean.setType(clazz);
        return bean.getObject();
    }
}
