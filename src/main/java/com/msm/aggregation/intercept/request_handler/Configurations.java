package com.msm.aggregation.intercept.request_handler;

import com.msm.aggregation.intercept.request_handler.impl.ResourceLoadingRequestHandler;
import com.msm.aggregation.intercept.request_handler.impl.TimeDelayRequestHandlerDecorator;

import java.util.Arrays;
import java.util.List;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Configurations {

    FILENAME("filename", params -> new ResourceLoadingRequestHandler((String)params.get(0))),
    TIMEOUT("timeout", params -> new TimeDelayRequestHandlerDecorator(Long.valueOf((String)params.get(0)), (ShortCircuitRequestHandler) params.get(1)));

    private final String name;
    private final Function<List<Object>, ShortCircuitRequestHandler> builder;

    Configurations(final String name, final Function<List<Object>, ShortCircuitRequestHandler> builder) {
        this.name = name;
        this.builder = builder;
    }

    public ShortCircuitRequestHandler build(final List<Object> params) {
        return builder.apply(params);
    }

    public static Optional<Configurations> findConfigurationFor(final String name) {
        return Arrays.stream(Configurations.values()).filter(c -> c.name.equals(name)).findFirst();
    }

    public static List<String> listNames() {
        return Arrays.stream(Configurations.values()).map(e -> e.name).collect(Collectors.toList());
    }

}
