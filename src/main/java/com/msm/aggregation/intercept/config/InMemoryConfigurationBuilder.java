package com.msm.aggregation.intercept.config;

import com.msm.aggregation.intercept.Boot;
import com.msm.aggregation.intercept.modifier.response.Configurations;
import com.msm.aggregation.intercept.modifier.response.ResponseModifier;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryConfigurationBuilder {

    private static final List<String> CONFIG_TYPE_NAMES = Configurations.listNames();

    public static InMemoryConfigurationRegistry build(final List<Map<String, Object>> configurations) {
        final InMemoryConfigurationRegistry registry = new InMemoryConfigurationRegistry();
        buildConfigurations(configurations).forEach(registry::addConfiguration);
        return registry;
    }

    private static Collection<Configuration> buildConfigurations(final List<Map<String, Object>> configurations) {
        return configurations.stream()
                .map(InMemoryConfigurationBuilder::buildConfiguration)
                .collect(Collectors.toList());
    }

    private static Configuration buildConfiguration(final Map<String, Object> attributeMap) {
        return findObjectForName("target", attributeMap)
                .map(e -> (String) e)
                .map(name -> new Configuration(name, buildResponseModifier(attributeMap)))
                .orElseThrow(() -> new IllegalArgumentException("Could not create configuration, missing target"));
    }

    private static ResponseModifier buildResponseModifier(final Map<String, Object> attributeMap) {
        final String configTypeName = attributeMap.keySet().stream()
                .filter(CONFIG_TYPE_NAMES::contains).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not create configurations, missing type"));
        final Optional<ResponseModifier> wrapped = findObjectForName("wrapped", attributeMap)
                .map(e -> (Map<String, Object>) e)
                .map(InMemoryConfigurationBuilder::buildResponseModifier);
        final List<Object> params = attributeMap.entrySet().stream().filter(s -> !"target".equals(s.getKey())).map(Map.Entry::getValue).collect(Collectors.toList());
        wrapped.ifPresent(params::add);
        return Configurations.findConfigurationFor(configTypeName).map(config -> config.build(params)).orElse(null);
    }

    private static Optional<Object> findObjectForName(final String name, final Map<String, Object> attributeMap) {
        return attributeMap.entrySet().stream()
                .filter(e -> e.getKey().equals(name))
                .map(Map.Entry::getValue)
                .findFirst();
    }


}
