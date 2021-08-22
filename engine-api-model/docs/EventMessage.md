
# EventMessage

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**type** | [**inline**](#TypeEnum) | The type of object emitting the event |  [optional]
**action** | **kotlin.String** | The type of event |  [optional]
**actor** | [**EventActor**](EventActor.md) |  |  [optional]
**scope** | [**inline**](#ScopeEnum) | Scope of the event. Engine events are &#x60;local&#x60; scope. Cluster (Swarm) events are &#x60;swarm&#x60; scope.  |  [optional]
**time** | **kotlin.Long** | Timestamp of event |  [optional]
**timeNano** | **kotlin.Long** | Timestamp of event, with nanosecond accuracy |  [optional]


<a name="TypeEnum"></a>
## Enum: Type
Name | Value
---- | -----
type | builder, config, container, daemon, image, network, node, plugin, secret, service, volume


<a name="ScopeEnum"></a>
## Enum: scope
Name | Value
---- | -----
scope | local, swarm



