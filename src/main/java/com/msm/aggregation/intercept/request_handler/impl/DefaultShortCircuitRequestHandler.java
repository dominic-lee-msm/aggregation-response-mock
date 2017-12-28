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
    private final Long timeoutInMilliseconds;

    public DefaultShortCircuitRequestHandler(final ResponseLoader responseLoader, Long timeoutInMilliseconds) {
        this.responseLoader = responseLoader;
        this.timeoutInMilliseconds = timeoutInMilliseconds;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        try {
            Thread.sleep(timeoutInMilliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new DefaultFullHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(responseLoader.loadResponse().getBytes(Charset.defaultCharset())));
    }
}
