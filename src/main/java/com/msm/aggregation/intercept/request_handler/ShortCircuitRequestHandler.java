package com.msm.aggregation.intercept.request_handler;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public interface ShortCircuitRequestHandler {

    public HttpResponse handleRequest(final HttpRequest request);

}
