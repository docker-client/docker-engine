
# HealthConfig

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**test** | **kotlin.collections.List&lt;kotlin.String&gt;** | The test to perform. Possible values are:  - &#x60;[]&#x60; inherit healthcheck from image or parent image - &#x60;[\&quot;NONE\&quot;]&#x60; disable healthcheck - &#x60;[\&quot;CMD\&quot;, args...]&#x60; exec arguments directly - &#x60;[\&quot;CMD-SHELL\&quot;, command]&#x60; run command with system&#39;s default shell  |  [optional]
**interval** | **kotlin.Int** | The time to wait between checks in nanoseconds. It should be 0 or at least 1000000 (1 ms). 0 means inherit.  |  [optional]
**timeout** | **kotlin.Int** | The time to wait before considering the check to have hung. It should be 0 or at least 1000000 (1 ms). 0 means inherit.  |  [optional]
**retries** | **kotlin.Int** | The number of consecutive failures needed to consider a container as unhealthy. 0 means inherit.  |  [optional]
**startPeriod** | **kotlin.Int** | Start period for the container to initialize before starting health-retries countdown in nanoseconds. It should be 0 or at least 1000000 (1 ms). 0 means inherit.  |  [optional]



