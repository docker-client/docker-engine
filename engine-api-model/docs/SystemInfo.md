
# SystemInfo

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**ID** | **kotlin.String** | Unique identifier of the daemon.  &lt;p&gt;&lt;br /&gt;&lt;/p&gt;  &gt; **Note**: The format of the ID itself is not part of the API, and &gt; should not be considered stable.  |  [optional]
**containers** | **kotlin.Int** | Total number of containers on the host. |  [optional]
**containersRunning** | **kotlin.Int** | Number of containers with status &#x60;\&quot;running\&quot;&#x60;.  |  [optional]
**containersPaused** | **kotlin.Int** | Number of containers with status &#x60;\&quot;paused\&quot;&#x60;.  |  [optional]
**containersStopped** | **kotlin.Int** | Number of containers with status &#x60;\&quot;stopped\&quot;&#x60;.  |  [optional]
**images** | **kotlin.Int** | Total number of images on the host.  Both _tagged_ and _untagged_ (dangling) images are counted.  |  [optional]
**driver** | **kotlin.String** | Name of the storage driver in use. |  [optional]
**driverStatus** | **kotlin.collections.List&lt;kotlin.collections.List&lt;kotlin.String&gt;&gt;** | Information specific to the storage driver, provided as \&quot;label\&quot; / \&quot;value\&quot; pairs.  This information is provided by the storage driver, and formatted in a way consistent with the output of &#x60;docker info&#x60; on the command line.  &lt;p&gt;&lt;br /&gt;&lt;/p&gt;  &gt; **Note**: The information returned in this field, including the &gt; formatting of values and labels, should not be considered stable, &gt; and may change without notice.  |  [optional]
**dockerRootDir** | **kotlin.String** | Root directory of persistent Docker state.  Defaults to &#x60;/var/lib/docker&#x60; on Linux, and &#x60;C:\\ProgramData\\docker&#x60; on Windows.  |  [optional]
**plugins** | [**PluginsInfo**](PluginsInfo.md) |  |  [optional]
**memoryLimit** | **kotlin.Boolean** | Indicates if the host has memory limit support enabled. |  [optional]
**swapLimit** | **kotlin.Boolean** | Indicates if the host has memory swap limit support enabled. |  [optional]
**kernelMemory** | **kotlin.Boolean** | Indicates if the host has kernel memory limit support enabled.  &lt;p&gt;&lt;br /&gt;&lt;/p&gt;  &gt; **Deprecated**: This field is deprecated as the kernel 5.4 deprecated &gt; &#x60;kmem.limit_in_bytes&#x60;.  |  [optional]
**cpuCfsPeriod** | **kotlin.Boolean** | Indicates if CPU CFS(Completely Fair Scheduler) period is supported by the host.  |  [optional]
**cpuCfsQuota** | **kotlin.Boolean** | Indicates if CPU CFS(Completely Fair Scheduler) quota is supported by the host.  |  [optional]
**cpUShares** | **kotlin.Boolean** | Indicates if CPU Shares limiting is supported by the host.  |  [optional]
**cpUSet** | **kotlin.Boolean** | Indicates if CPUsets (cpuset.cpus, cpuset.mems) are supported by the host.  See [cpuset(7)](https://www.kernel.org/doc/Documentation/cgroup-v1/cpusets.txt)  |  [optional]
**pidsLimit** | **kotlin.Boolean** | Indicates if the host kernel has PID limit support enabled. |  [optional]
**oomKillDisable** | **kotlin.Boolean** | Indicates if OOM killer disable is supported on the host. |  [optional]
**ipv4Forwarding** | **kotlin.Boolean** | Indicates IPv4 forwarding is enabled. |  [optional]
**bridgeNfIptables** | **kotlin.Boolean** | Indicates if &#x60;bridge-nf-call-iptables&#x60; is available on the host. |  [optional]
**bridgeNfIp6tables** | **kotlin.Boolean** | Indicates if &#x60;bridge-nf-call-ip6tables&#x60; is available on the host. |  [optional]
**debug** | **kotlin.Boolean** | Indicates if the daemon is running in debug-mode / with debug-level logging enabled.  |  [optional]
**nfd** | **kotlin.Int** | The total number of file Descriptors in use by the daemon process.  This information is only returned if debug-mode is enabled.  |  [optional]
**ngoroutines** | **kotlin.Int** | The  number of goroutines that currently exist.  This information is only returned if debug-mode is enabled.  |  [optional]
**systemTime** | **kotlin.String** | Current system-time in [RFC 3339](https://www.ietf.org/rfc/rfc3339.txt) format with nano-seconds.  |  [optional]
**loggingDriver** | **kotlin.String** | The logging driver to use as a default for new containers.  |  [optional]
**cgroupDriver** | [**inline**](#CgroupDriverEnum) | The driver to use for managing cgroups.  |  [optional]
**cgroupVersion** | [**inline**](#CgroupVersionEnum) | The version of the cgroup.  |  [optional]
**neventsListener** | **kotlin.Int** | Number of event listeners subscribed. |  [optional]
**kernelVersion** | **kotlin.String** | Kernel version of the host.  On Linux, this information obtained from &#x60;uname&#x60;. On Windows this information is queried from the &lt;kbd&gt;HKEY_LOCAL_MACHINE\\\\SOFTWARE\\\\Microsoft\\\\Windows NT\\\\CurrentVersion\\\\&lt;/kbd&gt; registry value, for example _\&quot;10.0 14393 (14393.1198.amd64fre.rs1_release_sec.170427-1353)\&quot;_.  |  [optional]
**operatingSystem** | **kotlin.String** | Name of the host&#39;s operating system, for example: \&quot;Ubuntu 16.04.2 LTS\&quot; or \&quot;Windows Server 2016 Datacenter\&quot;  |  [optional]
**osVersion** | **kotlin.String** | Version of the host&#39;s operating system  &lt;p&gt;&lt;br /&gt;&lt;/p&gt;  &gt; **Note**: The information returned in this field, including its &gt; very existence, and the formatting of values, should not be considered &gt; stable, and may change without notice.  |  [optional]
**osType** | **kotlin.String** | Generic type of the operating system of the host, as returned by the Go runtime (&#x60;GOOS&#x60;).  Currently returned values are \&quot;linux\&quot; and \&quot;windows\&quot;. A full list of possible values can be found in the [Go documentation](https://golang.org/doc/install/source#environment).  |  [optional]
**architecture** | **kotlin.String** | Hardware architecture of the host, as returned by the Go runtime (&#x60;GOARCH&#x60;).  A full list of possible values can be found in the [Go documentation](https://golang.org/doc/install/source#environment).  |  [optional]
**NCPU** | **kotlin.Int** | The number of logical CPUs usable by the daemon.  The number of available CPUs is checked by querying the operating system when the daemon starts. Changes to operating system CPU allocation after the daemon is started are not reflected.  |  [optional]
**memTotal** | **kotlin.Long** | Total amount of physical memory available on the host, in bytes.  |  [optional]
**indexServerAddress** | **kotlin.String** | Address / URL of the index server that is used for image search, and as a default for user authentication for Docker Hub and Docker Cloud.  |  [optional]
**registryConfig** | [**RegistryServiceConfig**](RegistryServiceConfig.md) |  |  [optional]
**genericResources** | [**kotlin.collections.List&lt;kotlin.Any&gt;**](kotlin.Any.md) | User-defined resources can be either Integer resources (e.g, &#x60;SSD&#x3D;3&#x60;) or String resources (e.g, &#x60;GPU&#x3D;UUID1&#x60;).  |  [optional]
**httpProxy** | **kotlin.String** | HTTP-proxy configured for the daemon. This value is obtained from the [&#x60;HTTP_PROXY&#x60;](https://www.gnu.org/software/wget/manual/html_node/Proxies.html) environment variable. Credentials ([user info component](https://tools.ietf.org/html/rfc3986#section-3.2.1)) in the proxy URL are masked in the API response.  Containers do not automatically inherit this configuration.  |  [optional]
**httpsProxy** | **kotlin.String** | HTTPS-proxy configured for the daemon. This value is obtained from the [&#x60;HTTPS_PROXY&#x60;](https://www.gnu.org/software/wget/manual/html_node/Proxies.html) environment variable. Credentials ([user info component](https://tools.ietf.org/html/rfc3986#section-3.2.1)) in the proxy URL are masked in the API response.  Containers do not automatically inherit this configuration.  |  [optional]
**noProxy** | **kotlin.String** | Comma-separated list of domain extensions for which no proxy should be used. This value is obtained from the [&#x60;NO_PROXY&#x60;](https://www.gnu.org/software/wget/manual/html_node/Proxies.html) environment variable.  Containers do not automatically inherit this configuration.  |  [optional]
**name** | **kotlin.String** | Hostname of the host. |  [optional]
**labels** | **kotlin.collections.List&lt;kotlin.String&gt;** | User-defined labels (key/value metadata) as set on the daemon.  &lt;p&gt;&lt;br /&gt;&lt;/p&gt;  &gt; **Note**: When part of a Swarm, nodes can both have _daemon_ labels, &gt; set through the daemon configuration, and _node_ labels, set from a &gt; manager node in the Swarm. Node labels are not included in this &gt; field. Node labels can be retrieved using the &#x60;/nodes/(id)&#x60; endpoint &gt; on a manager node in the Swarm.  |  [optional]
**experimentalBuild** | **kotlin.Boolean** | Indicates if experimental features are enabled on the daemon.  |  [optional]
**serverVersion** | **kotlin.String** | Version string of the daemon.  &gt; **Note**: the [standalone Swarm API](https://docs.docker.com/swarm/swarm-api/) &gt; returns the Swarm version instead of the daemon  version, for example &gt; &#x60;swarm/1.2.8&#x60;.  |  [optional]
**clusterStore** | **kotlin.String** | URL of the distributed storage backend.   The storage backend is used for multihost networking (to store network and endpoint information) and by the node discovery mechanism.  &lt;p&gt;&lt;br /&gt;&lt;/p&gt;  &gt; **Deprecated**: This field is only propagated when using standalone Swarm &gt; mode, and overlay networking using an external k/v store. Overlay &gt; networks with Swarm mode enabled use the built-in raft store, and &gt; this field will be empty.  |  [optional]
**clusterAdvertise** | **kotlin.String** | The network endpoint that the Engine advertises for the purpose of node discovery. ClusterAdvertise is a &#x60;host:port&#x60; combination on which the daemon is reachable by other hosts.  &lt;p&gt;&lt;br /&gt;&lt;/p&gt;  &gt; **Deprecated**: This field is only propagated when using standalone Swarm &gt; mode, and overlay networking using an external k/v store. Overlay &gt; networks with Swarm mode enabled use the built-in raft store, and &gt; this field will be empty.  |  [optional]
**runtimes** | [**kotlin.collections.Map&lt;kotlin.String, Runtime&gt;**](Runtime.md) | List of [OCI compliant](https://github.com/opencontainers/runtime-spec) runtimes configured on the daemon. Keys hold the \&quot;name\&quot; used to reference the runtime.  The Docker daemon relies on an OCI compliant runtime (invoked via the &#x60;containerd&#x60; daemon) as its interface to the Linux kernel namespaces, cgroups, and SELinux.  The default runtime is &#x60;runc&#x60;, and automatically configured. Additional runtimes can be configured by the user and will be listed here.  |  [optional]
**defaultRuntime** | **kotlin.String** | Name of the default OCI runtime that is used when starting containers.  The default can be overridden per-container at create time.  |  [optional]
**swarm** | [**SwarmInfo**](SwarmInfo.md) |  |  [optional]
**liveRestoreEnabled** | **kotlin.Boolean** | Indicates if live restore is enabled.  If enabled, containers are kept running when the daemon is shutdown or upon daemon start if running containers are detected.  |  [optional]
**isolation** | [**inline**](#IsolationEnum) | Represents the isolation technology to use as a default for containers. The supported values are platform-specific.  If no isolation value is specified on daemon start, on Windows client, the default is &#x60;hyperv&#x60;, and on Windows server, the default is &#x60;process&#x60;.  This option is currently not used on other platforms.  |  [optional]
**initBinary** | **kotlin.String** | Name and, optional, path of the &#x60;docker-init&#x60; binary.  If the path is omitted, the daemon searches the host&#39;s &#x60;$PATH&#x60; for the binary and uses the first result.  |  [optional]
**containerdCommit** | [**Commit**](Commit.md) |  |  [optional]
**runcCommit** | [**Commit**](Commit.md) |  |  [optional]
**initCommit** | [**Commit**](Commit.md) |  |  [optional]
**securityOptions** | **kotlin.collections.List&lt;kotlin.String&gt;** | List of security features that are enabled on the daemon, such as apparmor, seccomp, SELinux, user-namespaces (userns), and rootless.  Additional configuration options for each security feature may be present, and are included as a comma-separated list of key/value pairs.  |  [optional]
**productLicense** | **kotlin.String** | Reports a summary of the product license on the daemon.  If a commercial license has been applied to the daemon, information such as number of nodes, and expiration are included.  |  [optional]
**defaultAddressPools** | [**kotlin.collections.List&lt;SystemInfoDefaultAddressPools&gt;**](SystemInfoDefaultAddressPools.md) | List of custom default address pools for local networks, which can be specified in the daemon.json file or dockerd option.  Example: a Base \&quot;10.10.0.0/16\&quot; with Size 24 will define the set of 256 10.10.[0-255].0/24 address pools.  |  [optional]
**warnings** | **kotlin.collections.List&lt;kotlin.String&gt;** | List of warnings / informational messages about missing features, or issues related to the daemon configuration.  These messages can be printed by the client as information to the user.  |  [optional]


<a name="CgroupDriverEnum"></a>
## Enum: CgroupDriver
Name | Value
---- | -----
cgroupDriver | cgroupfs, systemd, none


<a name="CgroupVersionEnum"></a>
## Enum: CgroupVersion
Name | Value
---- | -----
cgroupVersion | 1, 2


<a name="IsolationEnum"></a>
## Enum: Isolation
Name | Value
---- | -----
isolation | default, hyperv, process



