package com.ijson.rest.proxy;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ijson.rest.proxy.codec.AbstractRestCodeC;
import com.ijson.rest.proxy.exception.RestProxyInvokeException;
import com.ijson.rest.proxy.model.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.*;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by cuiyongxu on 26/12/2016.
 */
@Slf4j
public class RestClient {


    protected static final int DEFAULT_MAX_CONNECTION = 512;
    protected static final int DEFAULT_MAX_PER_ROUTE_CONNECTION = 50;

    protected static final int DEFAULT_SOCKET_TIMEOUT = 5000;
    protected static final int DEFAULT_CONNECTION_TIMEOUT = 2000;

    private CloseableHttpClient defaultHttpClient;

    private Map<String, CloseableHttpClient> httpClientMap = Maps.newConcurrentMap();

    public RestClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(DEFAULT_MAX_CONNECTION);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE_CONNECTION);

        SocketConfig.Builder sb = SocketConfig.custom();
        sb.setSoKeepAlive(true);
        sb.setTcpNoDelay(true);
        connectionManager.setDefaultSocketConfig(sb.build());

        HttpClientBuilder hb = HttpClientBuilder.create();
        hb.setConnectionManager(connectionManager);

        RequestConfig.Builder rb = RequestConfig.custom();
        rb.setSocketTimeout(DEFAULT_SOCKET_TIMEOUT);
        rb.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);

        hb.setDefaultRequestConfig(rb.build());

        defaultHttpClient = hb.build();
    }

    public void createHttpClientForService(ServiceConfig serviceConfig) {
        String serviceKey = serviceConfig.getServiceKey();
        log.info("create http client:{},\t{},\t{}",serviceConfig.getServiceName(),serviceKey,serviceConfig.getAddress());
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(50);
        connectionManager.setDefaultMaxPerRoute(50);

        SocketConfig.Builder sb = SocketConfig.custom();
        sb.setSoKeepAlive(true);
        sb.setTcpNoDelay(true);
        connectionManager.setDefaultSocketConfig(sb.build());

        HttpClientBuilder hb = HttpClientBuilder.create();
        hb.setConnectionManager(connectionManager);

        RequestConfig.Builder rb = RequestConfig.custom();
        rb.setSocketTimeout(serviceConfig.getSocketTimeOut() == 0 ? DEFAULT_SOCKET_TIMEOUT
                : serviceConfig.getSocketTimeOut());
        rb.setConnectTimeout(serviceConfig.getConnectionTimeOut() == 0 ? DEFAULT_CONNECTION_TIMEOUT
                : serviceConfig.getConnectionTimeOut());

        hb.setDefaultRequestConfig(rb.build());

        try {
            CloseableHttpClient oldClient = httpClientMap.get(serviceKey);
            if (oldClient != null) {
                oldClient.close();
            }
        } catch (Exception e) {
            log.error("Close httpclient error,serviceKey:{}", serviceKey, e);
        }

        httpClientMap.put(serviceKey, hb.build());
    }

    public <R> R invoke(String serviceName, InvokeParams invokeParams, AbstractRestCodeC codeC) throws IOException {
        DefaultRestResponseHandler<R> handler = new DefaultRestResponseHandler<>(invokeParams.getServiceUrl(), invokeParams.getResultClazz(), codeC);

            CloseableHttpClient serviceClient = httpClientMap.get(serviceName);
            if (serviceClient == null) {
                serviceClient = defaultHttpClient;
            }
            RequestBuilder requestBuilder = HTTPRequestBuilderFactory.create(invokeParams, codeC);
            try (CloseableHttpResponse response = serviceClient.execute(requestBuilder.build())) {
                R rst = handler.handleResponse(response);
                if (rst == null) {
                    throw new RestProxyInvokeException("返回结果为空");
                }
                return rst;
            }
    }


    public static final class DefaultRestResponseHandler<T> implements ResponseHandler<T> {

        private Class<T> clazz;
        private AbstractRestCodeC codeC;
        private String uri;

        public DefaultRestResponseHandler(String uri, Class<T> clazz, AbstractRestCodeC codeC) {
            this.uri = uri;
            this.clazz = clazz;
            this.codeC = codeC;
        }

        @Override
        public T handleResponse(HttpResponse response) throws IOException {

            int statusCode = response.getStatusLine().getStatusCode();
            Map<String, List<String>> headers = Maps.newHashMap();
            Header[] tempHeaders = response.getAllHeaders();
            for (int i = 0; i < tempHeaders.length; i++) {
                Header header = tempHeaders[i];
                if (CollectionUtils.isEmpty(headers.get(header.getName()))) {
                    headers.put(header.getName().toLowerCase(), Lists.newArrayList(header.getValue()));
                } else {
                    headers.get(header.getName().toLowerCase()).add(header.getValue());
                }
            }
            HttpEntity in = response.getEntity();
            return codeC.decodeResult(statusCode, headers, in != null ? EntityUtils.toByteArray(in) : null, clazz);
    }
}

}
