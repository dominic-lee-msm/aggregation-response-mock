package com.msm.aggregation.intercept;

import com.google.common.io.Resources;

import java.io.IOException;
import java.util.Map;

import static com.google.common.base.Charsets.UTF_8;

public class YamlReader {

    private YamlReader(){throw new IllegalArgumentException("Don't construct me");}

    public static Map<String, Object> readYaml(final String name) throws IOException {
        final String yamlString = Resources.toString(Resources.getResource(name), UTF_8);
        final com.esotericsoftware.yamlbeans.YamlReader reader = new com.esotericsoftware.yamlbeans.YamlReader(yamlString);
        return (Map) reader.read();
    }

}
