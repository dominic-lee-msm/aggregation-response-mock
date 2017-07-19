package com.msm.aggregation.intercept.config;

import com.msm.aggregation.intercept.modifier.ResponseModifier;

public class Configuration {

    private final String name;

    private final String url;

    private final ResponseModifier responseModifier;

    public Configuration(String name, String url, ResponseModifier responseModifier) {
        this.name = name;
        this.url = url;
        this.responseModifier = responseModifier;
    }

    public String getName() {
        return name;
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
        
        return getName() != null ? getName().equals(that.getName()) : that.getName() == null;
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }
}
