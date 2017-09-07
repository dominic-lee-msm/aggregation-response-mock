package com.msm.aggregation.intercept.config;

import com.msm.aggregation.intercept.request_handler.ShortCircuitRequestHandler;

public class Configuration {

    private final String url;
    private final boolean enabled;
    private final ShortCircuitRequestHandler requestHandler;

    public Configuration(final String url, final boolean enabled, final ShortCircuitRequestHandler requestHandler) {
        this.url = url;
        this.enabled = enabled;
        this.requestHandler = requestHandler;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Configuration that = (Configuration) o;

        return getUrl() != null ? getUrl().equals(that.getUrl()) : that.getUrl() == null;
    }

    @Override
    public int hashCode() {
        return getUrl() != null ? getUrl().hashCode() : 0;
    }

    public ShortCircuitRequestHandler getRequestHandler() {
        return requestHandler;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
