package com.ijson.rest.proxy;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ijson.rest.proxy.annotation.*;
import com.ijson.rest.proxy.config.ServiceConfigManager;
import com.ijson.rest.proxy.exception.RestProxyConfigException;
import com.ijson.rest.proxy.exception.RestProxyINFieldException;
import com.ijson.rest.proxy.model.ServiceConfig;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cuiyongxu on 26/12/2016.
 */
@Data
class InvokeParams {
    /**
     * 请求的Url
     */
    private String serviceUrl;
    /**
     * 请求服务器地址
     */
    private String serviceIp;
    /**
     * 方法路径
     */
    private String methodPath;
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 服务关键字，与服务地址相关联
     */
    private String serviceKey;
    /**
     * 方法协议类型
     */
    private String methodType;
    private String codec;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> queryParams = new HashMap<>();
    private Map<String, String> pathParams = new HashMap<>();
    private Object body;
    private Class resultClazz;

    public static InvokeParams getInstance(String serviceKey, Method method, Object[] args) {
        RestResource restResource = method.getDeclaringClass().getAnnotation(RestResource.class);
        InvokeParams invokeParams = new InvokeParams();
        Object methodType;
        String methodContentType;
        String uri;
        String codec;
        if ((methodType = method.getAnnotation(POST.class)) != null) {
            POST post = (POST) methodType;
            invokeParams.methodType = "POST";
            uri = post.value();
            methodContentType = post.contentType();
            codec = post.codec();
        } else if ((methodType = method.getAnnotation(GET.class)) != null) {
            GET get = (GET) methodType;
            invokeParams.methodType = "GET";
            uri = get.value();
            methodContentType = get.contentType();
            codec = get.codec();
        } else if ((methodType = method.getAnnotation(PUT.class)) != null) {
            PUT put = (PUT) methodType;
            invokeParams.methodType = "PUT";
            uri = put.value();
            methodContentType = put.contentType();
            codec = put.codec();
        } else if ((methodType = method.getAnnotation(DELETE.class)) != null) {
            DELETE delete = (DELETE) methodType;
            invokeParams.methodType = "DELETE";
            uri = delete.value();
            methodContentType = delete.contentType();
            codec = delete.codec();
        } else {
            throw new RestProxyConfigException(method.getName() + " not have method type");
        }
        if (Strings.isNullOrEmpty(codec)) {
            codec = restResource.codec();
        }

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter p = parameters[i];
            Annotation[] annotations = p.getAnnotations();
            Annotation annotation;
            if (annotations.length == 0) {
                continue;
            } else {
                annotation = annotations[0];
            }

            if (annotation instanceof Body) {
                invokeParams.body = args[i];
                fieldSettings(invokeParams.body);
            } else if (annotation instanceof PathParams) {
                invokeParams.pathParams = args[i] != null ? (Map<String, String>) args[i] : null;
            } else if (annotation instanceof QueryParamsMap) {
                invokeParams.queryParams = args[i] != null ? (Map<String, String>) args[i] : null;
            } else if (annotation instanceof HeaderMap) {
                invokeParams.headers = args[i] != null ? (Map<String, String>) args[i] : new LinkedHashMap<>();
            }
        }


        ServiceConfig config = ServiceConfigManager.getServiceConfig(serviceKey);
        invokeParams.setServiceName(config.getServiceName());
        String serviceUrl = getResourceAddress(config, uri);
        invokeParams.resultClazz = method.getReturnType();
        invokeParams.setServiceUrl(serviceUrl);
        invokeParams.setMethodPath(uri);
        invokeParams.setServiceIp(config.getAddress());
        invokeParams.setCodec(codec);

        invokeParams.serviceUrl = getServiceUrl(serviceUrl, invokeParams.pathParams, invokeParams.queryParams);
        String globContentType = restResource.contentType();
        if (!Strings.isNullOrEmpty(globContentType)) {
            invokeParams.headers.put("Content-Type", globContentType);
        }
        if (!Strings.isNullOrEmpty(methodContentType)) {
            invokeParams.headers.put("Content-Type", methodContentType);
        }
        return invokeParams;
    }

    private static void fieldSettings(Object arg) {
        List<Field> fields = getDeclaredField(arg.getClass());
        fields.forEach(field -> {
            field.setAccessible(true);
            INField inField = field.getAnnotation(INField.class);
            if (inField != null) {
                boolean required = inField.required();
                Object value = null;
                try {
                    value = field.get(arg);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (required && value == null) {
                    throw new RestProxyINFieldException(-1, MessageFormat.format(inField.requiredMessage(), field.getName()));
                }
            }
        });
    }


    private static List<Field> getDeclaredField(Class<?> clazz) {
        List<Field> fieldList = Lists.newArrayList();
        while (clazz != null) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass(); //得到父类,然后赋给自己
        }
        return fieldList;
    }

    static Pattern p = Pattern.compile("(\\{[^{}]+\\})+");

    private static String getServiceUrl(String url, Map<String, String> pathParams, Map<String, String> queryParams) {
        String serviceUrl = url.replaceAll(" ", "");
        if (pathParams != null) {
            Matcher m = p.matcher(url);
            while (m.find()) {
                String pathPlaceHold = m.group();
                String pathPlaceHoldWithOutChar = pathPlaceHold.substring(1, pathPlaceHold.length() - 1);
                String value = pathParams.get(pathPlaceHoldWithOutChar);
                if (Strings.isNullOrEmpty(value)) {
                    throw new RestProxyConfigException(url + "参数" + pathPlaceHoldWithOutChar + "在参数列表中不存在");
                }
                serviceUrl = serviceUrl.replace(pathPlaceHold, value);
            }
        }
        if (queryParams != null) {
            serviceUrl += map2QueryParams(queryParams);
        }
        return serviceUrl;
    }

    private static String map2QueryParams(Map<String, String> map) {
        StringBuilder sb = new StringBuilder("?");
        map.forEach((k, v) -> {
            sb.append(k).append("=").append(v).append("&");
        });
        if (sb.length() > 1) {
            return sb.toString().substring(0, sb.length() - 1);
        } else {
            return "";
        }
    }

    private static String getResourceAddress(ServiceConfig config, String restUri) {
        String address = config.getAddress();
        if (address == null) {
            address = "";
        }
        String template = "%s";
        if (restUri.startsWith("/")) {
            template += "%s";
        } else {
            template += "/%s";
        }
        return String.format(template, address, restUri);
    }

}
