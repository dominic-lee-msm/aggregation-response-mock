package com.msm.aggregation.intercept.response_loader;

public class DefaultResponseLoader implements ResponseLoader {

    private final String response;

    public DefaultResponseLoader(final String response) {
        this.response = response;
    }

    @Override
    public String loadResponse() {
        return response;
    }
}
