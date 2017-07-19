package com.msm.aggregation.intercept.controller;

import com.msm.aggregation.intercept.config.Configuration;
import com.msm.aggregation.intercept.config.ConfigurationRegistry;
import com.msm.aggregation.intercept.config.InMemoryConfigurationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;


@RestController
public class InterceptController {

    private static final Logger LOG = LoggerFactory.getLogger(InterceptController.class);
    private ConfigurationRegistry configurations;

    public InterceptController(final ConfigurationRegistry configurations){
        this.configurations = configurations;
    }

    @PostMapping(path="/{configurationName}")
    public String handlePost(HttpServletRequest request, @PathVariable(value="configurationName") String configurationName) throws Exception {
        LOG.info("POST received");
        return configurations.findConfiguration(configurationName)
                .map(config -> handleRequestForConfiguration(request, config))
                .orElseThrow(() -> new Exception("Could not find configuration for name [" + configurationName + "]"));
    }

    private static String handleRequestForConfiguration(final HttpServletRequest request, final Configuration configuration) {
        LOG.info("Configuration found for [{}]", configuration.getName());
        final String requestBody = getRequestBody(request);
        final HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, createHeaders(request));
        final RestTemplate requestTemplate = new RestTemplate();
        LOG.info("Sending request");
        final String response = requestTemplate.postForObject(configuration.getUrl(), requestEntity, String.class);
        LOG.info("Response received, forwarding to modifier");
        return configuration.getResponseModifier().modifyResponse(response);
    }

    private static String getRequestBody(final HttpServletRequest request){
        final StringBuilder requestBodyBuilder = new StringBuilder();
        try(BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBodyBuilder.append(line);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return requestBodyBuilder.toString();
    }

    private static HttpHeaders createHeaders(final HttpServletRequest request) {
        final HttpHeaders headers = new HttpHeaders();
        for(String currentHeaderName : Collections.list(request.getHeaderNames())) {
            request.getHeader(currentHeaderName);
            headers.put(currentHeaderName, Collections.singletonList(request.getHeader(currentHeaderName)));
        }
        return headers;
    }

}
