package com.msm.aggregation.intercept;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.HttpFiltersAdapter;

import java.net.InetSocketAddress;

public class DebugHttpFiltersAdapter extends HttpFiltersAdapter {

    public DebugHttpFiltersAdapter(HttpRequest originalRequest, ChannelHandlerContext ctx) {
        super(originalRequest, ctx);
    }

    @Override
    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
        System.out.println("clientToProxyRequest " + httpObject);
        return super.clientToProxyRequest(httpObject);
    }

    @Override
    public HttpResponse proxyToServerRequest(HttpObject httpObject) {
        System.out.println("proxyToServerRequest " + httpObject);
        return super.proxyToServerRequest(httpObject);
    }

    @Override
    public void proxyToServerRequestSending() {
        System.out.println("proxyToServerRequestSending");
        super.proxyToServerRequestSending();
    }

    @Override
    public void proxyToServerRequestSent() {
        System.out.println("proxyToServerRequestSent");
        super.proxyToServerRequestSent();
    }

    @Override
    public HttpObject serverToProxyResponse(HttpObject httpObject) {
        System.out.println("serverToProxyResponse " + httpObject);
        return super.serverToProxyResponse(httpObject);
    }

    @Override
    public void serverToProxyResponseTimedOut() {
        System.out.println("serverToProxyResponseTimedOut");
        super.serverToProxyResponseTimedOut();
    }

    @Override
    public void serverToProxyResponseReceiving() {
        System.out.println("serverToProxyResponseReceiving");
        super.serverToProxyResponseReceiving();
    }

    @Override
    public void serverToProxyResponseReceived() {
        System.out.println("serverToProxyResponseReceived");
        super.serverToProxyResponseReceived();
    }

    @Override
    public HttpObject proxyToClientResponse(HttpObject httpObject) {
        System.out.println("proxyToClientResponse " + httpObject);
        return super.proxyToClientResponse(httpObject);
    }

    @Override
    public void proxyToServerConnectionQueued() {
        System.out.println("proxyToServerConnectionQueued");
        super.proxyToServerConnectionQueued();
    }

    @Override
    public InetSocketAddress proxyToServerResolutionStarted(String resolvingServerHostAndPort) {
        System.out.println("proxyToServerConnectionStarted " + resolvingServerHostAndPort);
        return super.proxyToServerResolutionStarted(resolvingServerHostAndPort);
    }

    @Override
    public void proxyToServerResolutionFailed(String hostAndPort) {
        System.out.println("proxyToServerResolutionFailed" + hostAndPort);
        super.proxyToServerResolutionFailed(hostAndPort);
    }

    @Override
    public void proxyToServerResolutionSucceeded(String serverHostAndPort, InetSocketAddress resolvedRemoteAddress) {
        System.out.println("proxyToServerResolutionSucceeded " + serverHostAndPort + " " + resolvedRemoteAddress);
        super.proxyToServerResolutionSucceeded(serverHostAndPort, resolvedRemoteAddress);
    }

    @Override
    public void proxyToServerConnectionStarted() {
        System.out.println("proxyToServerConnectionStarted");
        super.proxyToServerConnectionStarted();
    }

    @Override
    public void proxyToServerConnectionSSLHandshakeStarted() {
        System.out.println("proxyToServerConnectionSSLHandshakeStarted");
        super.proxyToServerConnectionSSLHandshakeStarted();
    }

    @Override
    public void proxyToServerConnectionFailed() {
        System.out.println("proxyToServerConnectionFailed");
        super.proxyToServerConnectionFailed();
    }

    @Override
    public void proxyToServerConnectionSucceeded(ChannelHandlerContext serverCtx) {
        System.out.println("proxyToServerConnectionSucceeded " + serverCtx);
        super.proxyToServerConnectionSucceeded(serverCtx);
    }
}
