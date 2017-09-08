package com.msm.aggregation.intercept;

import com.msm.aggregation.intercept.config.ConfigurationBuilder;
import com.msm.aggregation.intercept.config.ConfigurationRegistry;
import com.msm.aggregation.intercept.config.InMemoryConfigurationRegistry;
import com.msm.aggregation.intercept.config.MongoConfigurationRegistry;
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class Boot {

    private static final int PORT_NUMBER = 12300;

    public static void main(String[] args) throws Exception {
        final Map<String, Object> yaml = YamlReader.readYaml("config.yml");
        final ApplicationMode mode = ApplicationMode.findForIdentifier(getMode(yaml))
                .orElseThrow(() -> new IllegalStateException("No application mode found"));

        DefaultHttpProxyServer.bootstrap().withPort(PORT_NUMBER)
                .withFiltersSource(new HttpsHandlingFiltersSource(createRegistryForMode(mode, yaml)))
                .withManInTheMiddle(ImpersonatingMitmManager.builder().trustAllServers(true).build())
                .start();
    }

    private static ConfigurationRegistry createRegistryForMode(final ApplicationMode mode, final Map<String, Object> yaml) throws Exception {
        if(mode == ApplicationMode.MONGO) {
            final String mongoProfile = getMongoProfile(yaml);
            final MongoDbConnector connector = mongoConnector(yaml, mongoProfile);
            return new MongoConfigurationRegistry(connector, new ConfigurationBuilder());
        } else if(mode == ApplicationMode.IN_MEMORY) {
            return new InMemoryConfigurationRegistry("config.yml", new ConfigurationBuilder());
        }
        return null; // Integration test mode
    }

    private static MongoDbConnector mongoConnector(final Map<String, Object> yaml, final String profile) throws Exception {
        final Map<String, String> parameters = Optional.ofNullable(yaml)
                .map(m -> (Map) m.get("mongo-profiles"))
                .map(m -> (Map<String, String>) m.get(profile))
                .orElse(Collections.emptyMap());
        return new MongoDbConnector(parameters.get("hosts"),
                parameters.get("dbName"),
                parameters.get("user"),
                parameters.get("password"),
                "Y".equals(parameters.get("authRequired")),
                Integer.valueOf(parameters.get("port")));
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
