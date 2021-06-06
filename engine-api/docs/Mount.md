
# Mount

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**target** | **kotlin.String** | Container path. |  [optional]
**source** | **kotlin.String** | Mount source (e.g. a volume name, a host path). |  [optional]
**type** | [**inline**](#TypeEnum) | The mount type. Available types:  - &#x60;bind&#x60; Mounts a file or directory from the host into the container. Must exist prior to creating the container. - &#x60;volume&#x60; Creates a volume with the given name and options (or uses a pre-existing volume with the same name and options). These are **not** removed when the container is removed. - &#x60;tmpfs&#x60; Create a tmpfs with the given options. The mount source cannot be specified for tmpfs. - &#x60;npipe&#x60; Mounts a named pipe from the host into the container. Must exist prior to creating the container.  |  [optional]
**readOnly** | **kotlin.Boolean** | Whether the mount should be read-only. |  [optional]
**consistency** | **kotlin.String** | The consistency requirement for the mount: &#x60;default&#x60;, &#x60;consistent&#x60;, &#x60;cached&#x60;, or &#x60;delegated&#x60;. |  [optional]
**bindOptions** | [**MountBindOptions**](MountBindOptions.md) |  |  [optional]
**volumeOptions** | [**MountVolumeOptions**](MountVolumeOptions.md) |  |  [optional]
**tmpfsOptions** | [**MountTmpfsOptions**](MountTmpfsOptions.md) |  |  [optional]


<a name="TypeEnum"></a>
## Enum: Type
Name | Value
---- | -----
type | bind, volume, tmpfs, npipe



