package com.msm.aggregation.intercept.modifier.response.impl;

import com.msm.aggregation.intercept.modifier.response.ResponseModifier;
import io.netty.handler.codec.http.HttpObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeDelayResponseModifierDecorator implements ResponseModifier {

    private static final Logger LOG = LoggerFactory.getLogger(TimeDelayResponseModifierDecorator.class);
    private Long delayInMillis;
    private ResponseModifier wrapped;

    public TimeDelayResponseModifierDecorator(final Long delayInMillis, final ResponseModifier wrapped) {
        this.delayInMillis = delayInMillis;
        this.wrapped = wrapped;
    }

    @Override
    public HttpObject modifyResponse(HttpObject actualResponse) {
        try {
            Thread.sleep(delayInMillis);
            return wrapped.modifyResponse(actualResponse);
        } catch (Exception e) {
            LOG.error("ResponseModifier failed to sleep");
        }
        return null;
    }
}
