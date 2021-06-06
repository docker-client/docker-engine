
# NodeSpec

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **kotlin.String** | Name for the node. |  [optional]
**labels** | **kotlin.collections.Map&lt;kotlin.String, kotlin.String&gt;** | User-defined key/value metadata. |  [optional]
**role** | [**inline**](#RoleEnum) | Role of the node. |  [optional]
**availability** | [**inline**](#AvailabilityEnum) | Availability of the node. |  [optional]


<a name="RoleEnum"></a>
## Enum: Role
Name | Value
---- | -----
role | worker, manager


<a name="AvailabilityEnum"></a>
## Enum: Availability
Name | Value
---- | -----
availability | active, pause, drain



