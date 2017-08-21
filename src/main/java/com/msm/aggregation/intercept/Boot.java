package com.msm.aggregation.intercept;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.common.io.Resources;
import com.msm.aggregation.intercept.config.ConfigurationRegistry;
import com.msm.aggregation.intercept.config.InMemoryConfigurationBuilder;
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Charsets.UTF_8;

public class Boot {

    private static final int PORT_NUMBER = 12300;

    public static void main(String[] args) throws Exception {
        DefaultHttpProxyServer.bootstrap().withPort(PORT_NUMBER)
                .withFiltersSource(new HttpsHandlingFiltersSource(yamlConfiguredConfigurationRegistry()))
                .withManInTheMiddle(ImpersonatingMitmManager.builder().trustAllServers(true).build())
                .start();
    }

    private static ConfigurationRegistry yamlConfiguredConfigurationRegistry() throws IOException {
        final String yamlString = Resources.toString(Resources.getResource("config.yml"), UTF_8);
        final YamlReader reader = new YamlReader(yamlString);
        final Map<String, Object> map = (Map) reader.read();
        return InMemoryConfigurationBuilder.build(getYamlConfigs(map));
    }

    private static List<Map<String, Object>> getYamlConfigs(Map<String, Object> map) {
        return Optional.ofNullable(map)
                .map(m -> (Map) m.get("in-memory"))
                .map(m -> (List<Map<String, Object>>) m.get("intercept-configurations"))
                .orElse(Collections.emptyList());
    }

}
