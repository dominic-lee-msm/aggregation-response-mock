# aggregation-response-mock

Acts as a proxy server between external endpoints and the private api

## To run

`./run_server.bat`

## To route traffic through it

In `aggregation-core/api/conf`, change the contents of `http.conf` to contain:

```
http {
  client {
    allowAnyCertificate = true
    proxy {
      enabled = true
      address = "localhost"
      port = 12300
    }
    ssl {
      enabled = false
    }
  }
}
```

## Configuration

To proxy requests targeted at a certain external endpoint (e.g a providers system), add an element to `in-memory.intercept-configurations` in `config.yml`

## Mongo

When the application is configured to use Mongo as it's source of mocks, Mongo must contain a database called `aggregation-response-mock`. This database must contain 2 collections: `configuration` and `mock_response`.

`configuration` objects look like:

```
{
    "_id" : ObjectId("59aec62641c20c027d0d50f3"),
    "target" : "http://hostname.com/endpoint",
    "enabled" : "Y",
    "responseId" : "59aec5ea41c20c027d0d50f2"
}
```

`mock_response` objects look like:

```
{
    "_id" : ObjectId("59aec5ea41c20c027d0d50f2"),
    "response" : "the mock response body"
}
```

## Runtime configuration

Mongo-provided mocks are cached. Go to `/refresh-configurations` after making changes to your mocks in mongo