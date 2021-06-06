
# TaskSpecContainerSpecConfigs

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**file** | [**TaskSpecContainerSpecFile1**](TaskSpecContainerSpecFile1.md) |  |  [optional]
**runtime** | [**kotlin.Any**](.md) | Runtime represents a target that is not mounted into the container but is used by the task  &lt;p&gt;&lt;br /&gt;&lt;p&gt;  &gt; **Note**: &#x60;Configs.File&#x60; and &#x60;Configs.Runtime&#x60; are mutually &gt; exclusive  |  [optional]
**configID** | **kotlin.String** | ConfigID represents the ID of the specific config that we&#39;re referencing.  |  [optional]
**configName** | **kotlin.String** | ConfigName is the name of the config that this references, but this is just provided for lookup/display purposes. The config in the reference will be identified by its ID.  |  [optional]



