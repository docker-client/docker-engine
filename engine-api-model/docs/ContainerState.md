
# ContainerState

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**status** | [**inline**](#StatusEnum) | String representation of the container state. Can be one of \&quot;created\&quot;, \&quot;running\&quot;, \&quot;paused\&quot;, \&quot;restarting\&quot;, \&quot;removing\&quot;, \&quot;exited\&quot;, or \&quot;dead\&quot;.  |  [optional]
**running** | **kotlin.Boolean** | Whether this container is running.  Note that a running container can be _paused_. The &#x60;Running&#x60; and &#x60;Paused&#x60; booleans are not mutually exclusive:  When pausing a container (on Linux), the freezer cgroup is used to suspend all processes in the container. Freezing the process requires the process to be running. As a result, paused containers are both &#x60;Running&#x60; _and_ &#x60;Paused&#x60;.  Use the &#x60;Status&#x60; field instead to determine if a container&#39;s state is \&quot;running\&quot;.  |  [optional]
**paused** | **kotlin.Boolean** | Whether this container is paused. |  [optional]
**restarting** | **kotlin.Boolean** | Whether this container is restarting. |  [optional]
**ooMKilled** | **kotlin.Boolean** | Whether this container has been killed because it ran out of memory.  |  [optional]
**dead** | **kotlin.Boolean** |  |  [optional]
**pid** | **kotlin.Int** | The process ID of this container |  [optional]
**exitCode** | **kotlin.Int** | The last exit code of this container |  [optional]
**error** | **kotlin.String** |  |  [optional]
**startedAt** | **kotlin.String** | The time when this container was last started. |  [optional]
**finishedAt** | **kotlin.String** | The time when this container last exited. |  [optional]
**health** | [**Health**](Health.md) |  |  [optional]


<a name="StatusEnum"></a>
## Enum: Status
Name | Value
---- | -----
status | created, running, paused, restarting, removing, exited, dead



