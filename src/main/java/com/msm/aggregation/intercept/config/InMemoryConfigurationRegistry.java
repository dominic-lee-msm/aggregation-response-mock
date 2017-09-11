package com.msm.aggregation.intercept.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.msm.aggregation.intercept.YamlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class InMemoryConfigurationRegistry implements ConfigurationRegistry {

    private final LoadingCache<String, Optional<Configuration>> cache;

    public InMemoryConfigurationRegistry(final String yamlName, final ConfigurationBuilder configurationBuilder) {
        this.cache = CacheBuilder.newBuilder().build(CacheLoader.from(f -> {
            try {
                return getYamlConfig(YamlReader.readYaml(yamlName), f).flatMap(configurationBuilder::buildConfiguration);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Optional.empty();
        }));
    }

    @Override
    public Optional<Configuration> findConfiguration(String url) {
        try {
            return cache.get(url);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void refresh() {
        cache.invalidateAll();
    }

    private static Optional<Map<String, Object>> getYamlConfig(Map<String, Object> map, final String url) {
        final List<Map<String, Object>> allConfigs = Optional.ofNullable(map)
                .map(m -> (Map) m.get("in-memory"))
                .map(m -> (List<Map<String, Object>>) m.get("intercept-configurations"))
                .orElse(Collections.emptyList());
        return allConfigs.stream().filter(config -> config.get("target").equals(url)).findFirst();
    }

}
