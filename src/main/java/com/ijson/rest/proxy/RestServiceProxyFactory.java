package com.ijson.rest.proxy;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.reflect.Reflection;
import com.ijson.rest.proxy.annotation.RestResource;
import com.ijson.rest.proxy.codec.AbstractRestCodeC;
import com.ijson.rest.proxy.config.ServiceConfigManager;
import com.ijson.rest.proxy.exception.RestProxyConfigException;
import com.ijson.rest.proxy.exception.RestProxyInvokeException;
import com.ijson.rest.proxy.model.ServiceConfig;
import com.ijson.rest.proxy.util.JsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by cuiyongxu on 26/12/2016.
 */
@Slf4j
@Data
public class RestServiceProxyFactory {

    private Map<String, AbstractRestCodeC> serviceCodeCMaps = new HashMap<>();
    private final static RestClient restClient = new RestClient();

    private ServiceConfigManager configManager;

    private String configName;

    public RestServiceProxyFactory() {

    }

    public void init() {
        configManager = new ServiceConfigManager(configName, (serviceConfigMaps) -> serviceConfigMaps.forEach((key, value) -> {
            restClient.createHttpClientForService(value);
            Map<String, ServiceConfig> grayServiceConfigs = value.getGrayServices();
            if (MapUtils.isNotEmpty(grayServiceConfigs)) {
                grayServiceConfigs.forEach((grayKey, grayService) ->
                        restClient.createHttpClientForService(grayService));
            }
        }));
    }


    public <T> T newRestServiceProxy(Class<T> clazz) {
        return Reflection.newProxy(clazz, (Object proxy, Method method, Object[] args) -> {
//            if (method.isDefault()) {
//                MethodHandle methodHandler = getMethodHandler(method);
//                return methodHandler.bindTo(proxy).invokeWithArguments(args);
//            }
            RestResource restResource = method.getDeclaringClass().getAnnotation(RestResource.class);
            String serviceKey = restResource.value();
            InvokeParams invokeParams = InvokeParams.getInstance(serviceKey, method, args);
            AbstractRestCodeC codec = getCodeC(invokeParams.getCodec());

            long start = System.currentTimeMillis();
            Object ret = null;
            Throwable throwable = null;
            try {
                ret = restClient.invoke(serviceKey, invokeParams, codec);
            } catch (Throwable e) {
                throwable = e;
            } finally {

                if (throwable == null || ret != null) {
                    log.debug("url:{},headerParams:{},arg:{},pathParams:{},queryParams:{},result:{}",
                            invokeParams.getServiceUrl(), invokeParams.getHeaders(),
                            invokeParams.getBody(), invokeParams.getPathParams(), invokeParams.getQueryParams(), ret);
                } else {
                    log.error("access [{}({})] ,headers:{},arg:{},result:{}",
                            invokeParams.getServiceName(), invokeParams.getServiceUrl(),
                            invokeParams.getHeaders(),
                            JsonUtil.toJson(invokeParams.getBody()),
                            JsonUtil.toJson(ret),
                            throwable);
                    throw throwable;
                }
            }
            return ret;
        });
    }

    Map<String, MethodHandle> methodHandlers = Maps.newConcurrentMap();

    private MethodHandle getMethodHandler(Method method)
            throws NoSuchMethodException, IllegalAccessException, InstantiationException, java.lang.reflect.InvocationTargetException {

        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                .getDeclaredConstructor(Class.class, int.class);
        constructor.setAccessible(true);

        Class<?> declaringClass = method.getDeclaringClass();
        int allModes = (MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE);
        return constructor.newInstance(declaringClass, allModes)
                .unreflectSpecial(method, declaringClass);
    }

    private AbstractRestCodeC getCodeC(String codec) {
        if (Strings.isNullOrEmpty(codec)) {
            throw new RestProxyInvokeException("codec init error ,please check  config ");
        }
        AbstractRestCodeC rst = serviceCodeCMaps.computeIfAbsent(codec, key -> {
            try {
                log.info("init Codec :{}", codec);
                return (AbstractRestCodeC) Class.forName(codec).newInstance();
            } catch (Exception e) {
                throw new RestProxyConfigException("codec init error:" + codec);
            }
        });
        return rst;
    }


}
