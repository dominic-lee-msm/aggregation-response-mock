package com.msm.aggregation.intercept.config;

import java.util.List;
import java.util.Optional;

public interface ConfigurationRegistry {

    public Optional<Configuration> findConfiguration(final String url);

    public void refresh();

}
