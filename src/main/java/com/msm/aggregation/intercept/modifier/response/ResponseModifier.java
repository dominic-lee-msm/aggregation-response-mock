package com.msm.aggregation.intercept.modifier.response;

import io.netty.handler.codec.http.HttpObject;

public interface ResponseModifier {

    public HttpObject modifyResponse(final HttpObject actualResponse);

}
