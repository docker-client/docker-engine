
# SwarmInfo

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**nodeID** | **kotlin.String** | Unique identifier of for this node in the swarm. |  [optional]
**nodeAddr** | **kotlin.String** | IP address at which this node can be reached by other nodes in the swarm.  |  [optional]
**localNodeState** | [**LocalNodeState**](LocalNodeState.md) |  |  [optional]
**controlAvailable** | **kotlin.Boolean** |  |  [optional]
**error** | **kotlin.String** |  |  [optional]
**remoteManagers** | [**kotlin.collections.List&lt;PeerNode&gt;**](PeerNode.md) | List of ID&#39;s and addresses of other managers in the swarm.  |  [optional]
**nodes** | **kotlin.Int** | Total number of nodes in the swarm. |  [optional]
**managers** | **kotlin.Int** | Total number of managers in the swarm. |  [optional]
**cluster** | [**ClusterInfo**](ClusterInfo.md) |  |  [optional]



