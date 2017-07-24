# aggregation-response-mock

## To run

`./run_server.bat`

## To route traffic through it

In `aggregation-core/api/conf`, change the contents of `http.conf` to contain:

```
http {
  client {
    proxy {
      enabled = true
      address = "localhost"
      port = 12300
    }
  }
}
```
