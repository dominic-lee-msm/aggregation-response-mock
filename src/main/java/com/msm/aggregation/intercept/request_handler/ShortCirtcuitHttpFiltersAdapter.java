package com.msm.aggregation.intercept.request_handler;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.HttpFiltersAdapter;

public class ShortCirtcuitHttpFiltersAdapter extends HttpFiltersAdapter {

    private final ShortCircuitRequestHandler requestHandler;

    public ShortCirtcuitHttpFiltersAdapter(final HttpRequest originalRequest,
            final ShortCircuitRequestHandler requestHandler) {
        super(originalRequest);
        this.requestHandler = requestHandler;
    }

    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
        if(requestHandler != null) {
            return requestHandler.handleRequest(originalRequest);
        }
        return super.clientToProxyRequest(httpObject);
    }

}
