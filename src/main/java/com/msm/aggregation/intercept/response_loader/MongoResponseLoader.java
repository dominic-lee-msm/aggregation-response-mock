package com.msm.aggregation.intercept.response_loader;

import com.msm.aggregation.intercept.MongoDbConnector;
import org.bson.types.ObjectId;

public class MongoResponseLoader implements ResponseLoader {

    private final String responseId;
    private final MongoDbConnector connector;

    public MongoResponseLoader(final String responseId, final MongoDbConnector connector) {
        this.responseId = responseId;
        this.connector = connector;
    }

    @Override
    public String loadResponse() {
        final String query = "{_id:#}";
        return connector.getCollection("mock_response").find(query, new ObjectId(responseId)).as(StoredResponse.class).next().response;
    }

    private static final class StoredResponse {

        private ObjectId id;

        private String response;

    }
}
