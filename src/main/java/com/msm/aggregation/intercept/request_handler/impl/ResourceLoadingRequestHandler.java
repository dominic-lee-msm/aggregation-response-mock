package com.msm.aggregation.intercept.request_handler.impl;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.msm.aggregation.intercept.request_handler.ShortCircuitRequestHandler;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

public class ResourceLoadingRequestHandler implements ShortCircuitRequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceLoadingRequestHandler.class);
    private final String resourceName;

    public ResourceLoadingRequestHandler(final String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        try {
            final String body = Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
            return new DefaultFullHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(body.getBytes(Charset.defaultCharset())));
        } catch (Exception e) {
            LOG.error("Failed to load resource [{}]", resourceName);
        }
        return null;
    }

}
