package com.msm.aggregation.intercept;

import com.msm.aggregation.intercept.config.Configuration;
import com.msm.aggregation.intercept.config.ConfigurationRegistry;
import com.msm.aggregation.intercept.config.InMemoryConfigurationRegistry;
import com.msm.aggregation.intercept.modifier.impl.ResourceLoadingResponseModifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Boot {

    private static final int PORT_NUMBER = 12300;

    public static void main(String[] args) {
        final ConfigurableApplicationContext appContext = SpringApplication.run(Boot.class, args);
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return container -> container.setPort(PORT_NUMBER);
    }

    @Bean
    public ConfigurationRegistry configurationRegistry() {
        final ConfigurationRegistry registry = new InMemoryConfigurationRegistry();
        registry.addConfiguration(sspConfiguration());
        return registry;
    }

    @Bean
    public Configuration sspConfiguration(){
        return new Configuration("ssp",
                "http://sspendpoint.com",
                new ResourceLoadingResponseModifier("response.xml"));
//                new NonModifyingResponseModifier());
    }

}
