
# HealthcheckResult

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**start** | [**java.time.OffsetDateTime**](java.time.OffsetDateTime.md) | Date and time at which this check started in [RFC 3339](https://www.ietf.org/rfc/rfc3339.txt) format with nano-seconds.  |  [optional]
**end** | **kotlin.String** | Date and time at which this check ended in [RFC 3339](https://www.ietf.org/rfc/rfc3339.txt) format with nano-seconds.  |  [optional]
**exitCode** | **kotlin.Int** | ExitCode meanings:  - &#x60;0&#x60; healthy - &#x60;1&#x60; unhealthy - &#x60;2&#x60; reserved (considered unhealthy) - other values: error running probe  |  [optional]
**output** | **kotlin.String** | Output from last check |  [optional]



