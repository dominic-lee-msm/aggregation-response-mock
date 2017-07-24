package com.msm.aggregation.intercept.modifier.response.impl;

import com.msm.aggregation.intercept.modifier.response.ResponseModifier;
import io.netty.handler.codec.http.HttpObject;

public class NonModifyingResponseModifier implements ResponseModifier {

    @Override
    public HttpObject modifyResponse(HttpObject actualResponse) {
        return actualResponse;
    }
}
