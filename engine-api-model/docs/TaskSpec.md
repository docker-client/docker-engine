
# TaskSpec

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**pluginSpec** | [**TaskSpecPluginSpec**](TaskSpecPluginSpec.md) |  |  [optional]
**containerSpec** | [**TaskSpecContainerSpec**](TaskSpecContainerSpec.md) |  |  [optional]
**networkAttachmentSpec** | [**TaskSpecNetworkAttachmentSpec**](TaskSpecNetworkAttachmentSpec.md) |  |  [optional]
**resources** | [**TaskSpecResources**](TaskSpecResources.md) |  |  [optional]
**restartPolicy** | [**TaskSpecRestartPolicy**](TaskSpecRestartPolicy.md) |  |  [optional]
**placement** | [**TaskSpecPlacement**](TaskSpecPlacement.md) |  |  [optional]
**forceUpdate** | **kotlin.Int** | A counter that triggers an update even if no relevant parameters have been changed.  |  [optional]
**runtime** | **kotlin.String** | Runtime is the type of runtime specified for the task executor.  |  [optional]
**networks** | [**kotlin.collections.List&lt;NetworkAttachmentConfig&gt;**](NetworkAttachmentConfig.md) | Specifies which networks the service should attach to. |  [optional]
**logDriver** | [**TaskSpecLogDriver**](TaskSpecLogDriver.md) |  |  [optional]



