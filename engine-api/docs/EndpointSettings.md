
# EndpointSettings

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**ipAMConfig** | [**EndpointIPAMConfig**](EndpointIPAMConfig.md) |  |  [optional]
**links** | **kotlin.collections.List&lt;kotlin.String&gt;** |  |  [optional]
**aliases** | **kotlin.collections.List&lt;kotlin.String&gt;** |  |  [optional]
**networkID** | **kotlin.String** | Unique ID of the network.  |  [optional]
**endpointID** | **kotlin.String** | Unique ID for the service endpoint in a Sandbox.  |  [optional]
**gateway** | **kotlin.String** | Gateway address for this network.  |  [optional]
**ipAddress** | **kotlin.String** | IPv4 address.  |  [optional]
**ipPrefixLen** | **kotlin.Int** | Mask length of the IPv4 address.  |  [optional]
**ipv6Gateway** | **kotlin.String** | IPv6 gateway address.  |  [optional]
**globalIPv6Address** | **kotlin.String** | Global IPv6 address.  |  [optional]
**globalIPv6PrefixLen** | **kotlin.Long** | Mask length of the global IPv6 address.  |  [optional]
**macAddress** | **kotlin.String** | MAC address for the endpoint on this network.  |  [optional]
**driverOpts** | **kotlin.collections.Map&lt;kotlin.String, kotlin.String&gt;** | DriverOpts is a mapping of driver options and values. These options are passed directly to the driver and are driver specific.  |  [optional]



