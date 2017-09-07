package com.msm.aggregation.intercept.request_handler.impl;

import com.msm.aggregation.intercept.request_handler.ShortCircuitRequestHandler;
import com.msm.aggregation.intercept.response_loader.ResponseLoader;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.nio.charset.Charset;

public class DefaultShortCircuitRequestHandler implements ShortCircuitRequestHandler {

    private ResponseLoader responseLoader;

    public DefaultShortCircuitRequestHandler(final ResponseLoader responseLoader) {
        this.responseLoader = responseLoader;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        return new DefaultFullHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(responseLoader.loadResponse().getBytes(Charset.defaultCharset())));
    }
}
