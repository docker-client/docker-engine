
# SwarmSpecCAConfigExternalCAs

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**protocol** | [**inline**](#ProtocolEnum) | Protocol for communication with the external CA (currently only &#x60;cfssl&#x60; is supported).  |  [optional]
**URL** | **kotlin.String** | URL where certificate signing requests should be sent.  |  [optional]
**options** | **kotlin.collections.Map&lt;kotlin.String, kotlin.String&gt;** | An object with key/value pairs that are interpreted as protocol-specific options for the external CA driver.  |  [optional]
**caCert** | **kotlin.String** | The root CA certificate (in PEM format) this external CA uses to issue TLS certificates (assumed to be to the current swarm root CA certificate if not provided).  |  [optional]


<a name="ProtocolEnum"></a>
## Enum: Protocol
Name | Value
---- | -----
protocol | cfssl



