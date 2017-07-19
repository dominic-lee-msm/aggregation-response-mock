package com.msm.aggregation.intercept.config;

import java.util.List;
import java.util.Optional;

public interface ConfigurationRegistry {

    public List<Configuration> getAllConfigurations();

    public Optional<Configuration> findConfiguration(final String name);

    public void addConfiguration(final Configuration configuration);

    public void removeConfiguration(final Configuration configuration);

}
