package com.msm.aggregation.intercept.modifier.impl;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.msm.aggregation.intercept.modifier.ResponseModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

public class ResourceLoadingResponseModifier implements ResponseModifier {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceLoadingResponseModifier.class);
    private String resourceName;

    public ResourceLoadingResponseModifier(final String resourceName) {
        this.resourceName = requireNonNull(resourceName);
    }

    @Override
    public String modifyResponse(String actualResponse) {
        try {
            return Resources.toString(Resources.getResource(resourceName), Charsets.UTF_8);
        } catch (Exception e) {
            LOG.error("Failed to load resource [{}]", resourceName);
        }
        return null;
    }
}
