package com.msm.aggregation.intercept.request_handler.impl;

import com.msm.aggregation.intercept.request_handler.ShortCircuitRequestHandler;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeDelayRequestHandlerDecorator implements ShortCircuitRequestHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TimeDelayRequestHandlerDecorator.class);
    private Long delay;
    private ShortCircuitRequestHandler wrapped;

    public TimeDelayRequestHandlerDecorator(final Long delay, final ShortCircuitRequestHandler wrapped) {
        this.delay = delay;
        this.wrapped = wrapped;
    }

    @Override
    public HttpResponse handleRequest(HttpRequest request) {
        try {
            Thread.sleep(delay);
        } catch(Exception e) {
            LOG.error("Failed to sleep");
        }
        return wrapped.handleRequest(request);
    }
}
