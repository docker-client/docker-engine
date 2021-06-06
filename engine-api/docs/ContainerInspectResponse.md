
# ContainerInspectResponse

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **kotlin.String** | The ID of the container |  [optional]
**created** | **kotlin.String** | The time the container was created |  [optional]
**path** | **kotlin.String** | The path to the command being run |  [optional]
**args** | **kotlin.collections.List&lt;kotlin.String&gt;** | The arguments to the command being run |  [optional]
**state** | [**ContainerState**](ContainerState.md) |  |  [optional]
**image** | **kotlin.String** | The container&#39;s image ID |  [optional]
**resolvConfPath** | **kotlin.String** |  |  [optional]
**hostnamePath** | **kotlin.String** |  |  [optional]
**hostsPath** | **kotlin.String** |  |  [optional]
**logPath** | **kotlin.String** |  |  [optional]
**name** | **kotlin.String** |  |  [optional]
**restartCount** | **kotlin.Int** |  |  [optional]
**driver** | **kotlin.String** |  |  [optional]
**platform** | **kotlin.String** |  |  [optional]
**mountLabel** | **kotlin.String** |  |  [optional]
**processLabel** | **kotlin.String** |  |  [optional]
**appArmorProfile** | **kotlin.String** |  |  [optional]
**execIDs** | **kotlin.collections.List&lt;kotlin.String&gt;** | IDs of exec instances that are running in the container. |  [optional]
**hostConfig** | [**HostConfig**](HostConfig.md) |  |  [optional]
**graphDriver** | [**GraphDriverData**](GraphDriverData.md) |  |  [optional]
**sizeRw** | **kotlin.Long** | The size of files that have been created or changed by this container.  |  [optional]
**sizeRootFs** | **kotlin.Long** | The total size of all the files in this container. |  [optional]
**mounts** | [**kotlin.collections.List&lt;MountPoint&gt;**](MountPoint.md) |  |  [optional]
**config** | [**ContainerConfig**](ContainerConfig.md) |  |  [optional]
**networkSettings** | [**NetworkSettings**](NetworkSettings.md) |  |  [optional]



