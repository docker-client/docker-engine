
# TaskSpecRestartPolicy

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**condition** | [**inline**](#ConditionEnum) | Condition for restart. |  [optional]
**delay** | **kotlin.Long** | Delay between restart attempts. |  [optional]
**maxAttempts** | **kotlin.Long** | Maximum attempts to restart a given container before giving up (default value is 0, which is ignored).  |  [optional]
**window** | **kotlin.Long** | Windows is the time window used to evaluate the restart policy (default value is 0, which is unbounded).  |  [optional]


<a name="ConditionEnum"></a>
## Enum: Condition
Name | Value
---- | -----
condition | none, on-failure, any



