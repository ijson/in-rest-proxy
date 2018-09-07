package com.ijson.rest.proxy;

import lombok.Data;

/**
 * Created by cuiyongxu on 26/12/2016.
 */
@Data
public class RestServiceProxyFactoryBean<T> {
    private RestServiceProxyFactory factory;
    private Class<T> type;

    public T getObject(){
        return factory.newRestServiceProxy(type);
    }

    public Class<?> getObjectType() {
        return type;
    }

    public boolean isSingleton() {
        return true;
    }
}
