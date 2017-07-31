package com.msm.aggregation.intercept;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.msm.aggregation.intercept.config.Configuration;
import com.msm.aggregation.intercept.config.ConfigurationRegistry;
import com.msm.aggregation.intercept.config.InMemoryConfigurationBuilder;
import com.msm.aggregation.intercept.modifier.response.ResponseModifier;
import com.msm.aggregation.intercept.modifier.response.impl.NonModifyingResponseModifier;
import io.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Map;


@SpringBootApplication
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

                    @Override
                    public int getMaximumResponseBufferSizeInBytes() {
                        return 10 * 1024 * 1024;
                    }
                }).start();
    }

    @Autowired
    private Environment environment;

    @Bean
    public ConfigurationRegistry configurationRegistry() {
        LOG.info("Building configuration registry");
        return InMemoryConfigurationBuilder.build(ImmutableList.of(createEndpointConfiguration()));
    }

    private Map<String, Object> createEndpointConfiguration(){
        return ImmutableMap.of("target", "http://prelive.euiwebservice.co.uk/webservice/QuoteLaunch?ddanc=true",
                "filename", "response.xml");
    }

}
