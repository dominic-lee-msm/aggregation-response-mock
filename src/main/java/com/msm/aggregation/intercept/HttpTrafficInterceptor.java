package com.msm.aggregation.intercept;

import com.msm.aggregation.intercept.config.Configuration;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpTrafficInterceptor extends HttpFiltersAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(HttpTrafficInterceptor.class);
    private final Configuration configuration;

    public HttpTrafficInterceptor(final HttpRequest originalRequest, final Configuration configuration) {
        super(originalRequest);
        this.configuration = configuration;
    }

    @Override
    public HttpObject serverToProxyResponse(HttpObject httpObject) {
        LOG.info("serverToProxyResponse called for [{}]", configuration.getUrl());
        return configuration.getResponseModifier().modifyResponse(httpObject);
    }

}
