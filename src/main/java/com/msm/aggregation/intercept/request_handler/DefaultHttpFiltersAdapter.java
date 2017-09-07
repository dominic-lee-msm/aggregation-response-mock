package com.msm.aggregation.intercept.request_handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.littleshoot.proxy.HttpFiltersAdapter;

public class DefaultHttpFiltersAdapter extends HttpFiltersAdapter {

    public DefaultHttpFiltersAdapter(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        super(originalRequest, ctx);
    }

    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
        final DefaultFullHttpResponse response = new DefaultFullHttpResponse(originalRequest.getProtocolVersion(), HttpResponseStatus.OK);
        HttpHeaders.setKeepAlive(response, false);
        return response;
    }
}
