package com.msm.aggregation.intercept.modifier.response.impl;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.msm.aggregation.intercept.modifier.response.ResponseModifier;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class ResourceLoadingResponseModifier implements ResponseModifier {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceLoadingResponseModifier.class);
    private String resourceName;

    public ResourceLoadingResponseModifier(final String resourceName) {
        this.resourceName = requireNonNull(resourceName);
    }

    @Override
    public HttpObject modifyResponse(HttpObject actualResponse) {
        try {
            final FullHttpResponse fullResponse = getFullResponse(actualResponse);
            final String body = Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
            return new DefaultFullHttpResponse(fullResponse.getProtocolVersion(), fullResponse.getStatus(),
                    Unpooled.wrappedBuffer(body.getBytes(Charset.defaultCharset())));
        } catch (Exception e) {
            LOG.error("Failed to load resource [{}]", resourceName);
        }
        return null;
    }

    private FullHttpResponse getFullResponse(HttpObject actualResponse){
        return Optional.ofNullable(actualResponse).filter(e -> FullHttpResponse.class.isInstance(e))
                .map(e -> (FullHttpResponse) e)
                .orElseThrow(() -> new IllegalArgumentException("Could not handle response"));
    }

}
