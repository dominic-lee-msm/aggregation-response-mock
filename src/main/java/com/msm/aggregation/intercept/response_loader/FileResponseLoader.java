package com.msm.aggregation.intercept.response_loader;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileResponseLoader implements ResponseLoader {

    private static final Logger LOG = LoggerFactory.getLogger(FileResponseLoader.class);
    private String fileName;

    public FileResponseLoader(final String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String loadResponse() {
        try {
            return Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
        } catch (Exception e) {
            LOG.error("Failed to load resource [{}]", fileName);
        }
        return null;
    }
}
