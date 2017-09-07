package com.msm.aggregation.intercept.request_handler;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.HttpFiltersAdapter;

public class ShortCircuitHttpFiltersAdapter extends HttpFiltersAdapter {

    private final ShortCircuitRequestHandler requestHandler;

    public ShortCircuitHttpFiltersAdapter(final HttpRequest originalRequest,
            final ShortCircuitRequestHandler requestHandler) {
        super(originalRequest);
        this.requestHandler = requestHandler;
    }

    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
        if(requestHandler != null) {
            final HttpResponse response = requestHandler.handleRequest(originalRequest);
            HttpHeaders.setKeepAlive(response, false);
            return response;
        }
        return super.clientToProxyRequest(httpObject);
    }

}
