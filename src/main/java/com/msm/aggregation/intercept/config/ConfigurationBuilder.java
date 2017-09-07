package com.msm.aggregation.intercept.config;

import com.google.common.collect.ImmutableList;
import com.msm.aggregation.intercept.MongoDbConnector;
import com.msm.aggregation.intercept.request_handler.ShortCircuitRequestHandler;
import com.msm.aggregation.intercept.request_handler.impl.DefaultShortCircuitRequestHandler;
import com.msm.aggregation.intercept.request_handler.impl.TimeDelayRequestHandlerDecorator;
import com.msm.aggregation.intercept.response_loader.FileResponseLoader;
import com.msm.aggregation.intercept.response_loader.MongoResponseLoader;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConfigurationBuilder {

    private static final List<String> RESPONSE_LOADER_TYPE_NAMES = ImmutableList.of("filename", "responseId", "timeout");
    private MongoDbConnector mongoConnector;

    public ConfigurationBuilder(final MongoDbConnector mongoConnector) {
        this.mongoConnector = mongoConnector;
    }

    public Collection<Configuration> buildConfigurations(final Collection<Map<String, Object>> configurations) {
        return configurations.stream()
                .map(this::buildConfiguration)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private Optional<Configuration> buildConfiguration(final Map<String, Object> attributeMap) {
        final boolean enabled = findObjectForName("enabled", attributeMap)
                .map(String.class::cast)
                .filter("Y"::equalsIgnoreCase).isPresent();
        return findObjectForName("target", attributeMap)
                .map(name -> new Configuration((String) name, enabled, buildRequestHandler(attributeMap)));
    }

    private ShortCircuitRequestHandler buildRequestHandler(final Map<String, Object> attributeMap) {
        final String configTypeName = attributeMap.keySet().stream()
                .filter(RESPONSE_LOADER_TYPE_NAMES::contains).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not create configurations, missing type"));

        final Optional<ShortCircuitRequestHandler> wrapped = findObjectForName("wrapped", attributeMap)
                .map(e -> (Map<String, Object>) e)
                .map(this::buildRequestHandler);

        final Map<String, Object> paramMap = attributeMap.entrySet().stream()
                .filter(s -> !"target".equals(s.getKey()))
                .filter(s -> !"wrapped".equals(s.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        wrapped.ifPresent(w -> paramMap.put("wrapped", w));

        return buildRequestHandler(configTypeName, paramMap).orElse(null);
    }

    private Optional<ShortCircuitRequestHandler> buildRequestHandler(final String configTypeName, final Map<String, Object> paramMap) {
        if(configTypeName.equals("filename")) {
            return Optional.of(new DefaultShortCircuitRequestHandler(new FileResponseLoader((String) paramMap.get("filename"))));
        } else if(configTypeName.equals("responseId") && mongoConnector != null) {
            return Optional.of(new DefaultShortCircuitRequestHandler(new MongoResponseLoader((String) paramMap.get("responseId"), mongoConnector)));
        } else if(configTypeName.equals("timeout")) {
            return Optional.of(new TimeDelayRequestHandlerDecorator(Long.valueOf((String) paramMap.get("timeout")), (ShortCircuitRequestHandler)  paramMap.get("wrapped")));
        }
        return Optional.empty();
    }

    private static Optional<Object> findObjectForName(final String name, final Map<String, Object> attributeMap) {
        return attributeMap.entrySet().stream()
                .filter(e -> e.getKey().equals(name))
                .map(Map.Entry::getValue)
                .findFirst();
    }

}
