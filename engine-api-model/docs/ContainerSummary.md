
# ContainerSummary

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **kotlin.String** | The ID of this container |  [optional]
**names** | **kotlin.collections.List&lt;kotlin.String&gt;** | The names that this container has been given |  [optional]
**image** | **kotlin.String** | The name of the image used when creating this container |  [optional]
**imageID** | **kotlin.String** | The ID of the image that this container was created from |  [optional]
**command** | **kotlin.String** | Command to run when starting the container |  [optional]
**created** | **kotlin.Long** | When the container was created |  [optional]
**ports** | [**kotlin.collections.List&lt;Port&gt;**](Port.md) | The ports exposed by this container |  [optional]
**sizeRw** | **kotlin.Long** | The size of files that have been created or changed by this container |  [optional]
**sizeRootFs** | **kotlin.Long** | The total size of all the files in this container |  [optional]
**labels** | **kotlin.collections.Map&lt;kotlin.String, kotlin.String&gt;** | User-defined key/value metadata. |  [optional]
**state** | **kotlin.String** | The state of this container (e.g. &#x60;Exited&#x60;) |  [optional]
**status** | **kotlin.String** | Additional human-readable status of this container (e.g. &#x60;Exit 0&#x60;) |  [optional]
**hostConfig** | [**ContainerSummaryHostConfig**](ContainerSummaryHostConfig.md) |  |  [optional]
**networkSettings** | [**ContainerSummaryNetworkSettings**](ContainerSummaryNetworkSettings.md) |  |  [optional]
**mounts** | [**kotlin.collections.List&lt;Mount&gt;**](Mount.md) |  |  [optional]



