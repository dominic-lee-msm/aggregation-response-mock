package com.msm.aggregation.intercept;

import com.msm.aggregation.intercept.config.Configuration;
import com.msm.aggregation.intercept.config.ConfigurationRegistry;
import com.msm.aggregation.intercept.config.InMemoryConfigurationRegistry;
import com.msm.aggregation.intercept.modifier.response.ResponseModifier;
import com.msm.aggregation.intercept.modifier.response.impl.NonModifyingResponseModifier;
import com.msm.aggregation.intercept.modifier.response.impl.ResourceLoadingResponseModifier;
import com.msm.aggregation.intercept.modifier.response.impl.TimeDelayResponseModifierDecorator;
import io.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties({Boot.Configurations.class})
public class Boot {

    private static final Logger LOG = LoggerFactory.getLogger(HttpTrafficInterceptor.class);
    private static final ResponseModifier NON_MODIFYING_RESPONSE = new NonModifyingResponseModifier();
    private static final Configuration NON_MODIFYING_CONFIG = new Configuration("default response modifier", NON_MODIFYING_RESPONSE);

    private static final int PORT_NUMBER = 12300;

    public static void main(String[] args) {
        final ConfigurableApplicationContext appContext = SpringApplication.run(Boot.class, args);
        DefaultHttpProxyServer.bootstrap().withPort(PORT_NUMBER)
                .withFiltersSource(new HttpFiltersSourceAdapter() {
                    @Override
                    public HttpFilters filterRequest(HttpRequest originalRequest) {
                        LOG.info("Filtering request for [{}]", originalRequest.getUri());
                        final Configuration config = appContext.getBean(ConfigurationRegistry.class)
                            .findConfiguration(originalRequest.getUri())
                            .orElse(NON_MODIFYING_CONFIG);
                        return new HttpTrafficInterceptor(originalRequest, config);
                    }
                }).start();
    }

    @Autowired
    private Configurations configurations;

    @Bean
    public ConfigurationRegistry configurationRegistry() {
        final ConfigurationRegistry registry = new InMemoryConfigurationRegistry();

        for(String configuration : configurations.getConfigurations()) {

            String[] config = configuration.split(":");

            registry.addConfiguration(new Configuration(config[1],
                    new ResourceLoadingResponseModifier(config[0])));
        }

        return registry;
    }


    @ConfigurationProperties(prefix = "intercept", ignoreUnknownFields = false)
    public static class Configurations {

        private List<String> configurations = new ArrayList<String>();

        public List<String> getConfigurations() {
            return this.configurations;
        }
    }
}
