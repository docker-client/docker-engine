
# ServiceSpec

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **kotlin.String** | Name of the service. |  [optional]
**labels** | **kotlin.collections.Map&lt;kotlin.String, kotlin.String&gt;** | User-defined key/value metadata. |  [optional]
**taskTemplate** | [**TaskSpec**](TaskSpec.md) |  |  [optional]
**mode** | [**ServiceSpecMode**](ServiceSpecMode.md) |  |  [optional]
**updateConfig** | [**ServiceSpecUpdateConfig**](ServiceSpecUpdateConfig.md) |  |  [optional]
**rollbackConfig** | [**ServiceSpecRollbackConfig**](ServiceSpecRollbackConfig.md) |  |  [optional]
**networks** | [**kotlin.collections.List&lt;NetworkAttachmentConfig&gt;**](NetworkAttachmentConfig.md) | Specifies which networks the service should attach to. |  [optional]
**endpointSpec** | [**EndpointSpec**](EndpointSpec.md) |  |  [optional]



