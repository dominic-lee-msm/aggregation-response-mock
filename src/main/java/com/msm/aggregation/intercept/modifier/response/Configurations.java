package com.msm.aggregation.intercept.modifier.response;

import com.msm.aggregation.intercept.modifier.response.impl.ResourceLoadingResponseModifier;
import com.msm.aggregation.intercept.modifier.response.impl.TimeDelayResponseModifierDecorator;

import java.util.Arrays;
import java.util.List;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Configurations {

    FILENAME("filename", params -> new ResourceLoadingResponseModifier((String)params.get(0))),
    TIMEOUT("timeout", params -> new TimeDelayResponseModifierDecorator((Long)params.get(0), (ResponseModifier)params.get(1)))
    ;

    private final String name;
    private final Function<List<Object>, ResponseModifier> builder;

    Configurations(final String name, final Function<List<Object>, ResponseModifier> builder) {
        this.name = name;
        this.builder = builder;
    }

    public ResponseModifier build(final List<Object> params) {
        return builder.apply(params);
    }

    public static Optional<Configurations> findConfigurationFor(final String name) {
        return Arrays.stream(Configurations.values()).filter(c -> c.name.equals(name)).findFirst();
    }

    public static List<String> listNames() {
        return Arrays.stream(Configurations.values()).map(e -> e.name).collect(Collectors.toList());
    }

}
