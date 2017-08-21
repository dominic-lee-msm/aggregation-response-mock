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