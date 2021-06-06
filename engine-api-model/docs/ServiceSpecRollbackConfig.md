
# ServiceSpecRollbackConfig

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**parallelism** | **kotlin.Long** | Maximum number of tasks to be rolled back in one iteration (0 means unlimited parallelism).  |  [optional]
**delay** | **kotlin.Long** | Amount of time between rollback iterations, in nanoseconds.  |  [optional]
**failureAction** | [**inline**](#FailureActionEnum) | Action to take if an rolled back task fails to run, or stops running during the rollback.  |  [optional]
**monitor** | **kotlin.Long** | Amount of time to monitor each rolled back task for failures, in nanoseconds.  |  [optional]
**maxFailureRatio** | [**java.math.BigDecimal**](java.math.BigDecimal.md) | The fraction of tasks that may fail during a rollback before the failure action is invoked, specified as a floating point number between 0 and 1.  |  [optional]
**order** | [**inline**](#OrderEnum) | The order of operations when rolling back a task. Either the old task is shut down before the new task is started, or the new task is started before the old task is shut down.  |  [optional]


<a name="FailureActionEnum"></a>
## Enum: FailureAction
Name | Value
---- | -----
failureAction | continue, pause


<a name="OrderEnum"></a>
## Enum: Order
Name | Value
---- | -----
order | stop-first, start-first



