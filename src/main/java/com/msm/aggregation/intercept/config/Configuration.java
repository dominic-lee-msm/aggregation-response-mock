package com.msm.aggregation.intercept.config;

import com.msm.aggregation.intercept.modifier.response.ResponseModifier;

public class Configuration {

    private final String url;

    private final ResponseModifier responseModifier;


    public Configuration(final String url, final ResponseModifier responseModifier) {
        this.url = url;
        this.responseModifier = responseModifier;
    }

    public String getUrl() {
        return url;
    }

    public ResponseModifier getResponseModifier() {
        return responseModifier;
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
}
