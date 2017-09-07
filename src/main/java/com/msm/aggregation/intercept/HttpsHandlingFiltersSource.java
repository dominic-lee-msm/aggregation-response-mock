package com.msm.aggregation.intercept;

import com.google.common.collect.ImmutableList;
import com.msm.aggregation.intercept.config.Configuration;
import com.msm.aggregation.intercept.config.ConfigurationRegistry;
import com.msm.aggregation.intercept.request_handler.DefaultHttpFiltersAdapter;
import com.msm.aggregation.intercept.request_handler.ShortCircuitHttpFiltersAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.AttributeKey;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.netty.handler.codec.http.HttpMethod.CONNECT;

public class HttpsHandlingFiltersSource extends HttpFiltersSourceAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(HttpsHandlingFiltersSource.class);
    private static final int REQUEST_RESPONSE_BYTES_SIZE = 10 * 1024 * 1024;
    private static final AttributeKey<String> CONNECTED_URL = AttributeKey.valueOf("connected_url");
    private static final List<String> LOCALHOST_ALIASES = ImmutableList.of("127.0.0.1", "localhost");
    private ConfigurationRegistry configurationRegistry;

    public HttpsHandlingFiltersSource(final ConfigurationRegistry configurationRegistry) {
        this.configurationRegistry = configurationRegistry;
    }

    @Override
    public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext clientCtx) {
        final String uri = originalRequest.getUri();
        if(LOCALHOST_ALIASES.contains(getHostName(originalRequest))) {
            if(uri.contains("refresh-configurations")){
                configurationRegistry.refresh();
            }
            return new DefaultHttpFiltersAdapter(originalRequest, clientCtx);
        }

        if (originalRequest.getMethod() == CONNECT) {
            return handleHttpConnect(originalRequest, clientCtx, uri);
        }

        final String connectedUrl = clientCtx.channel().attr(CONNECTED_URL).get();
        final String url = connectedUrl == null ? uri : connectedUrl + uri;
        final HttpFilters filters = configurationRegistry.findConfiguration(url)
                .filter(Configuration::isEnabled)
                .map(Configuration::getRequestHandler)
                .map(handler -> (HttpFilters) new ShortCircuitHttpFiltersAdapter(originalRequest, handler))
                .orElseGet(() -> new HttpFiltersAdapter(originalRequest));

        LOG.info("Filtering request for [{}] using filters [{}]", url, filters);

        return filters;
    }

    private HttpFilters handleHttpConnect(HttpRequest originalRequest, ChannelHandlerContext clientCtx, String uri) {
        LOG.info("Handling https connect");
        if (clientCtx != null) {
            final String prefix = "https://" + uri.replaceFirst(":443$", "");
            clientCtx.channel().attr(CONNECTED_URL).set(prefix);
        }
        return new HttpFiltersAdapter(originalRequest, clientCtx);
    }

    @Override
    public int getMaximumResponseBufferSizeInBytes() {
        return REQUEST_RESPONSE_BYTES_SIZE;
    }

    @Override
    public int getMaximumRequestBufferSizeInBytes() {
        return REQUEST_RESPONSE_BYTES_SIZE;
    }

    private static String getHostName(HttpRequest originalRequest) {
        return HttpHeaders.getHost(originalRequest).split(":")[0];
    }

}
