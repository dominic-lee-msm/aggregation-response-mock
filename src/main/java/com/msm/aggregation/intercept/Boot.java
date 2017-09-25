package com.msm.aggregation.intercept;

import com.google.common.collect.ImmutableMap;
import com.msm.aggregation.intercept.config.ConfigurationBuilder;
import com.msm.aggregation.intercept.config.ConfigurationRegistry;
import com.msm.aggregation.intercept.config.InMemoryConfigurationRegistry;
import com.msm.aggregation.intercept.config.MongoConfigurationRegistry;
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@SpringBootApplication
public class Boot {

    public static final int PROXY_PORT_NUMBER = 12300;

    public static void main(String[] args) throws Exception {
        final ConfigurableApplicationContext ctx = SpringApplication.run(Boot.class, args);

        DefaultHttpProxyServer.bootstrap().withPort(PROXY_PORT_NUMBER)
                .withFiltersSource(ctx.getBean(HttpsHandlingFiltersSource.class))
                .withManInTheMiddle(ImpersonatingMitmManager.builder().trustAllServers(true).build())
                .start();
    }

    @Bean
    public HttpsHandlingFiltersSource filtersSource() {
        final ApplicationMode mode = ApplicationMode.findForIdentifier(getMode(yamlConfiguration()))
                .orElseThrow(() -> new IllegalStateException("No application mode found"));
        return new HttpsHandlingFiltersSource(createRegistryForMode(mode));
    }

    @Bean
    public Map<String, Object> yamlConfiguration() {
        try {
            return YamlReader.readYaml("config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ImmutableMap.of();
    }

    @Bean
    public String profile() {
        return getMongoProfile(yamlConfiguration());
    }

    @Bean
    public MongoDbConnector mongoConnector() {
        final Map<String, String> parameters = Optional.ofNullable(yamlConfiguration())
                .map(m -> (Map) m.get("mongo-profiles"))
                .map(m -> (Map<String, String>) m.get(profile()))
                .orElse(Collections.emptyMap());
        return new MongoDbConnector(parameters.get("hosts"),
                parameters.get("dbName"),
                parameters.get("user"),
                parameters.get("password"),
                "Y".equals(parameters.get("authRequired")),
                Integer.valueOf(parameters.get("port")));
    }

    private ConfigurationRegistry createRegistryForMode(final ApplicationMode mode) {
        if(mode == ApplicationMode.MONGO) {
            final MongoDbConnector connector = mongoConnector();
            return new MongoConfigurationRegistry(connector, new ConfigurationBuilder());
        } else if(mode == ApplicationMode.IN_MEMORY) {
            return new InMemoryConfigurationRegistry("config.yml", new ConfigurationBuilder());
        }
        return null; // Integration test mode
    }

    private static String getMongoProfile(final Map<String, Object> yaml) {
        return Optional.ofNullable(yaml)
                .map(m -> (String) m.get("mongo-profile"))
                .orElseThrow(() -> new IllegalStateException("No mongo-profile set"));
    }

    private static String getMode(final Map<String, Object> yaml){
        return Optional.ofNullable(yaml)
                .map(m -> (Map<String, Object>) m.get("application"))
                .map(m -> (String) m.get("mode"))
                .orElseThrow(() -> new IllegalStateException("No application mode set"));
    }

    private enum ApplicationMode {
        MONGO("mongo"),
        IN_MEMORY("in-memory"),
        INTEGRATION_TEST("integration-test");

        private String identifier;

        ApplicationMode(final String identifier) {
            this.identifier = identifier;
        }

        public static Optional<ApplicationMode> findForIdentifier(String identifier) {
            return Arrays.stream(values()).filter(f -> f.identifier.equals(identifier)).findFirst();
        }
    }



}
