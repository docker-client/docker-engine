
# Task

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**ID** | **kotlin.String** | The ID of the task. |  [optional]
**version** | [**ObjectVersion**](ObjectVersion.md) |  |  [optional]
**createdAt** | **kotlin.String** |  |  [optional]
**updatedAt** | **kotlin.String** |  |  [optional]
**name** | **kotlin.String** | Name of the task. |  [optional]
**labels** | **kotlin.collections.Map&lt;kotlin.String, kotlin.String&gt;** | User-defined key/value metadata. |  [optional]
**spec** | [**TaskSpec**](TaskSpec.md) |  |  [optional]
**serviceID** | **kotlin.String** | The ID of the service this task is part of. |  [optional]
**slot** | **kotlin.Int** |  |  [optional]
**nodeID** | **kotlin.String** | The ID of the node that this task is on. |  [optional]
**assignedGenericResources** | [**kotlin.collections.List&lt;kotlin.Any&gt;**](kotlin.Any.md) | User-defined resources can be either Integer resources (e.g, &#x60;SSD&#x3D;3&#x60;) or String resources (e.g, &#x60;GPU&#x3D;UUID1&#x60;).  |  [optional]
**status** | [**TaskStatus**](TaskStatus.md) |  |  [optional]
**desiredState** | [**TaskState**](TaskState.md) |  |  [optional]
**jobIteration** | [**ObjectVersion**](ObjectVersion.md) |  |  [optional]



