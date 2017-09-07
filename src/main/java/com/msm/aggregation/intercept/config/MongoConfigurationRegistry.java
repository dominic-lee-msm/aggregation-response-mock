package com.msm.aggregation.intercept.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.mongodb.DBObject;
import com.msm.aggregation.intercept.MongoDbConnector;
import org.jongo.RawResultHandler;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class MongoConfigurationRegistry implements ConfigurationRegistry {

    private final LoadingCache<String, Configuration> cache;

    public MongoConfigurationRegistry(final MongoDbConnector mongoConnector, final ConfigurationBuilder builder) {
        this.cache = CacheBuilder.newBuilder().build(CacheLoader.from(url -> {
            final Optional<DBObject> configurationForUrl = ImmutableList.copyOf(loadConfigurations(url, mongoConnector)).stream().findFirst();
            return configurationForUrl.flatMap(m -> builder.buildConfigurations(Collections.singleton((Map<String,Object>)m.toMap())).stream().findFirst())
                    .orElse(null);
        }));
    }

    @Override
    public Optional<Configuration> findConfiguration(String url) {
        try {
            return Optional.ofNullable(cache.get(url));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void refresh() {
        cache.invalidateAll();
    }

    private static Iterator<DBObject> loadConfigurations(final String url, final MongoDbConnector mongoConnector) {
        final String queryUrl = removeTrailingSlash(url);
        return mongoConnector.getCollection("configuration").find("{target: '" + queryUrl + "'}")
                .map(new RawResultHandler<>()).iterator();
    }

    private static String removeTrailingSlash(final String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }
}
