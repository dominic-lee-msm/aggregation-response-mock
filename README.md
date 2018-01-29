# aggregation-response-mock

Acts as a proxy server the private api and external endpoints (e.g. providers)

## Mocking a providers response

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

Run `run_server` or `run_server.bat` in the aggregation-response-mock.

Navigate to `localhost:12301` and create a new profile. Enter some details and save.

Then perform your enquiry with the private api.
