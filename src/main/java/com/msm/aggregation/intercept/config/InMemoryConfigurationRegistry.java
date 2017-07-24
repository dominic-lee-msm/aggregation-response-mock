package com.msm.aggregation.intercept.config;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryConfigurationRegistry implements ConfigurationRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(InMemoryConfigurationRegistry.class);
    private final List<Configuration> configurations;

    public InMemoryConfigurationRegistry() {
        this.configurations = new ArrayList<>();
    }

    @Override
    public List<Configuration> getAllConfigurations() {
        return ImmutableList.copyOf(configurations);
    }

    @Override
    public Optional<Configuration> findConfiguration(String url) {
        return configurations.stream().filter(c -> c.getUrl().equals(url)).findFirst();
    }

    @Override
    public void addConfiguration(Configuration configuration) {
        LOG.info("Adding configuration [{}] to [{}]", configuration.getUrl(), this.toString());
        configurations.add(configuration);
    }

    @Override
    public void removeConfiguration(Configuration configuration) {
        LOG.info("Removing configuration [{}] from [{}]", configuration.getUrl(), this.toString());
        configurations.remove(configuration);
    }
}
