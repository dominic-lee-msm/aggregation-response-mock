package com.msm.aggregation.intercept;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Splitter;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.marshall.jackson.JacksonMapper;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Enables interaction with MongoDB instances
 */
public class MongoDbConnector {

    private final Jongo jongo;
    private MongoClient mongoClient = null;

    /**
     * Create a new Connector with the supplied required config and
     */
    public MongoDbConnector(String hosts, final String dbName, final String user,
            final String password, final boolean authRequired, int port) {
        try {
            List<ServerAddress> serverAddresses = buildServerAddressList(hosts, port);
            if (null == serverAddresses || serverAddresses.isEmpty()) {
                throw new IllegalArgumentException("No hosts found in Config!");
            }

            if(authRequired) {
                List<MongoCredential> credentials = Arrays.asList(MongoCredential.createCredential(user, dbName, password.toCharArray()));

                mongoClient = (serverAddresses.size() == 1)
                        ? new MongoClient(serverAddresses.get(0), credentials)
                        : new MongoClient(serverAddresses, credentials);

            } else {

                mongoClient = (serverAddresses.size() == 1)
                        ? new MongoClient(serverAddresses.get(0))
                        : new MongoClient(serverAddresses);
            }


        } catch (UnknownHostException e) {
            throw new MongoException("Could not create MongoClient:" + e.getMessage(), e);
        }

        this.jongo = new Jongo(
                mongoClient.getDB(dbName),
                new JacksonMapper.Builder()
                        .registerModule(new JavaTimeModule())
                        .registerModule(new JodaModule())
                        .addModifier(mapper -> mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS))
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .build());
    }

    /**
     * Gets a collection from the current MongoDB datasource
     *
     * @param collectionName The name of the collection to get
     * @return The requested collection
     */
    public MongoCollection getCollection(String collectionName) {
        return this.jongo.getCollection(collectionName);
    }

    private List<ServerAddress> buildServerAddressList(String hosts, int port) throws UnknownHostException {
        List<ServerAddress> serverAddresses = new ArrayList<>();
        for (String host : Splitter.on(',').omitEmptyStrings().trimResults().split(hosts)) {
            serverAddresses.add(new ServerAddress(host, port));
        }
        return serverAddresses;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }
}
