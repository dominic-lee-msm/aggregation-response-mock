package com.msm.aggregation.intercept.modifier.impl;

import com.msm.aggregation.intercept.modifier.ResponseModifier;

public class NonModifyingResponseModifier implements ResponseModifier {

    @Override
    public String modifyResponse(String actualResponse) {
        return actualResponse;
    }
}
