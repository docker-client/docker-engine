
# SwarmSpecCAConfig

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**nodeCertExpiry** | **kotlin.Long** | The duration node certificates are issued for. |  [optional]
**externalCAs** | [**kotlin.collections.List&lt;SwarmSpecCAConfigExternalCAs&gt;**](SwarmSpecCAConfigExternalCAs.md) | Configuration for forwarding signing requests to an external certificate authority.  |  [optional]
**signingCACert** | **kotlin.String** | The desired signing CA certificate for all swarm node TLS leaf certificates, in PEM format.  |  [optional]
**signingCAKey** | **kotlin.String** | The desired signing CA key for all swarm node TLS leaf certificates, in PEM format.  |  [optional]
**forceRotate** | **kotlin.Int** | An integer whose purpose is to force swarm to generate a new signing CA certificate and key, if none have been specified in &#x60;SigningCACert&#x60; and &#x60;SigningCAKey&#x60;  |  [optional]



