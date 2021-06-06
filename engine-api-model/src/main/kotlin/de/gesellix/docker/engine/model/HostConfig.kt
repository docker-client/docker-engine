/**
* Docker Engine API
* The Engine API is an HTTP API served by Docker Engine. It is the API the Docker client uses to communicate with the Engine, so everything the Docker client can do can be done with the API.  Most of the client's commands map directly to API endpoints (e.g. `docker ps` is `GET /containers/json`). The notable exception is running containers, which consists of several API calls.  # Errors  The API uses standard HTTP status codes to indicate the success or failure of the API call. The body of the response will be JSON in the following format:  ``` {   \"message\": \"page not found\" } ```  # Versioning  The API is usually changed in each release, so API calls are versioned to ensure that clients don't break. To lock to a specific version of the API, you prefix the URL with its version, for example, call `/v1.30/info` to use the v1.30 version of the `/info` endpoint. If the API version specified in the URL is not supported by the daemon, a HTTP `400 Bad Request` error message is returned.  If you omit the version-prefix, the current version of the API (v1.41) is used. For example, calling `/info` is the same as calling `/v1.41/info`. Using the API without a version-prefix is deprecated and will be removed in a future release.  Engine releases in the near future should support this version of the API, so your client will continue to work even if it is talking to a newer Engine.  The API uses an open schema model, which means server may add extra properties to responses. Likewise, the server will ignore any extra query parameters and request body properties. When you write clients, you need to ignore additional properties in responses to ensure they do not break when talking to newer daemons.   # Authentication  Authentication for registries is handled client side. The client has to send authentication details to various endpoints that need to communicate with registries, such as `POST /images/(name)/push`. These are sent as `X-Registry-Auth` header as a [base64url encoded](https://tools.ietf.org/html/rfc4648#section-5) (JSON) string with the following structure:  ``` {   \"username\": \"string\",   \"password\": \"string\",   \"email\": \"string\",   \"serveraddress\": \"string\" } ```  The `serveraddress` is a domain/IP without a protocol. Throughout this structure, double quotes are required.  If you have already got an identity token from the [`/auth` endpoint](#operation/SystemAuth), you can just pass this instead of credentials:  ``` {   \"identitytoken\": \"9cbaf023786cd7...\" } ```
*
* The version of the OpenAPI document: 1.41
*
*
* NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
* https://openapi-generator.tech
* Do not edit the class manually.
*/
package de.gesellix.docker.engine.model

import de.gesellix.docker.engine.model.DeviceMapping
import de.gesellix.docker.engine.model.DeviceRequest
import de.gesellix.docker.engine.model.HostConfigAllOf
import de.gesellix.docker.engine.model.HostConfigAllOfLogConfig
import de.gesellix.docker.engine.model.Mount
import de.gesellix.docker.engine.model.PortBinding
import de.gesellix.docker.engine.model.Resources
import de.gesellix.docker.engine.model.ResourcesBlkioWeightDevice
import de.gesellix.docker.engine.model.ResourcesUlimits
import de.gesellix.docker.engine.model.RestartPolicy
import de.gesellix.docker.engine.model.ThrottleDevice

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Container configuration that depends on the host we are running on
 * @param cpuShares An integer value representing this container's relative CPU weight versus other containers.
 * @param memory Memory limit in bytes.
 * @param cgroupParent Path to `cgroups` under which the container's `cgroup` is created. If the path is not absolute, the path is considered to be relative to the `cgroups` path of the init process. Cgroups are created if they do not already exist.
 * @param blkioWeight Block IO weight (relative weight).
 * @param blkioWeightDevice Block IO weight (relative device weight) in the form:  ``` [{\"Path\": \"device_path\", \"Weight\": weight}] ```
 * @param blkioDeviceReadBps Limit read rate (bytes per second) from a device, in the form:  ``` [{\"Path\": \"device_path\", \"Rate\": rate}] ```
 * @param blkioDeviceWriteBps Limit write rate (bytes per second) to a device, in the form:  ``` [{\"Path\": \"device_path\", \"Rate\": rate}] ```
 * @param blkioDeviceReadIOps Limit read rate (IO per second) from a device, in the form:  ``` [{\"Path\": \"device_path\", \"Rate\": rate}] ```
 * @param blkioDeviceWriteIOps Limit write rate (IO per second) to a device, in the form:  ``` [{\"Path\": \"device_path\", \"Rate\": rate}] ```
 * @param cpuPeriod The length of a CPU period in microseconds.
 * @param cpuQuota Microseconds of CPU time that the container can get in a CPU period.
 * @param cpuRealtimePeriod The length of a CPU real-time period in microseconds. Set to 0 to allocate no time allocated to real-time tasks.
 * @param cpuRealtimeRuntime The length of a CPU real-time runtime in microseconds. Set to 0 to allocate no time allocated to real-time tasks.
 * @param cpusetCpus CPUs in which to allow execution (e.g., `0-3`, `0,1`).
 * @param cpusetMems Memory nodes (MEMs) in which to allow execution (0-3, 0,1). Only effective on NUMA systems.
 * @param devices A list of devices to add to the container.
 * @param deviceCgroupRules a list of cgroup rules to apply to the container
 * @param deviceRequests A list of requests for devices to be sent to device drivers.
 * @param kernelMemory Kernel memory limit in bytes.  <p><br /></p>  > **Deprecated**: This field is deprecated as the kernel 5.4 deprecated > `kmem.limit_in_bytes`.
 * @param kernelMemoryTCP Hard limit for kernel TCP buffer memory (in bytes).
 * @param memoryReservation Memory soft limit in bytes.
 * @param memorySwap Total memory limit (memory + swap). Set as `-1` to enable unlimited swap.
 * @param memorySwappiness Tune a container's memory swappiness behavior. Accepts an integer between 0 and 100.
 * @param nanoCpus CPU quota in units of 10<sup>-9</sup> CPUs.
 * @param oomKillDisable Disable OOM Killer for the container.
 * @param init Run an init inside the container that forwards signals and reaps processes. This field is omitted if empty, and the default (as configured on the daemon) is used.
 * @param pidsLimit Tune a container's PIDs limit. Set `0` or `-1` for unlimited, or `null` to not change.
 * @param ulimits A list of resource limits to set in the container. For example:  ``` {\"Name\": \"nofile\", \"Soft\": 1024, \"Hard\": 2048} ```
 * @param cpuCount The number of usable CPUs (Windows only).  On Windows Server containers, the processor resource controls are mutually exclusive. The order of precedence is `CPUCount` first, then `CPUShares`, and `CPUPercent` last.
 * @param cpuPercent The usable percentage of the available CPUs (Windows only).  On Windows Server containers, the processor resource controls are mutually exclusive. The order of precedence is `CPUCount` first, then `CPUShares`, and `CPUPercent` last.
 * @param ioMaximumIOps Maximum IOps for the container system drive (Windows only)
 * @param ioMaximumBandwidth Maximum IO in bytes per second for the container system drive (Windows only).
 * @param binds A list of volume bindings for this container. Each volume binding is a string in one of these forms:  - `host-src:container-dest[:options]` to bind-mount a host path   into the container. Both `host-src`, and `container-dest` must   be an _absolute_ path. - `volume-name:container-dest[:options]` to bind-mount a volume   managed by a volume driver into the container. `container-dest`   must be an _absolute_ path.  `options` is an optional, comma-delimited list of:  - `nocopy` disables automatic copying of data from the container   path to the volume. The `nocopy` flag only applies to named volumes. - `[ro|rw]` mounts a volume read-only or read-write, respectively.   If omitted or set to `rw`, volumes are mounted read-write. - `[z|Z]` applies SELinux labels to allow or deny multiple containers   to read and write to the same volume.     - `z`: a _shared_ content label is applied to the content. This       label indicates that multiple containers can share the volume       content, for both reading and writing.     - `Z`: a _private unshared_ label is applied to the content.       This label indicates that only the current container can use       a private volume. Labeling systems such as SELinux require       proper labels to be placed on volume content that is mounted       into a container. Without a label, the security system can       prevent a container's processes from using the content. By       default, the labels set by the host operating system are not       modified. - `[[r]shared|[r]slave|[r]private]` specifies mount   [propagation behavior](https://www.kernel.org/doc/Documentation/filesystems/sharedsubtree.txt).   This only applies to bind-mounted volumes, not internal volumes   or named volumes. Mount propagation requires the source mount   point (the location where the source directory is mounted in the   host operating system) to have the correct propagation properties.   For shared volumes, the source mount point must be set to `shared`.   For slave volumes, the mount must be set to either `shared` or   `slave`.
 * @param containerIDFile Path to a file where the container ID is written
 * @param logConfig
 * @param networkMode Network mode to use for this container. Supported standard values are: `bridge`, `host`, `none`, and `container:<name|id>`. Any other value is taken as a custom network's name to which this container should connect to.
 * @param portBindings PortMap describes the mapping of container ports to host ports, using the container's port-number and protocol as key in the format `<port>/<protocol>`, for example, `80/udp`.  If a container's port is mapped for multiple protocols, separate entries are added to the mapping table.
 * @param restartPolicy
 * @param autoRemove Automatically remove the container when the container's process exits. This has no effect if `RestartPolicy` is set.
 * @param volumeDriver Driver that this container uses to mount volumes.
 * @param volumesFrom A list of volumes to inherit from another container, specified in the form `<container name>[:<ro|rw>]`.
 * @param mounts Specification for mounts to be added to the container.
 * @param capAdd A list of kernel capabilities to add to the container. Conflicts with option 'Capabilities'.
 * @param capDrop A list of kernel capabilities to drop from the container. Conflicts with option 'Capabilities'.
 * @param cgroupnsMode cgroup namespace mode for the container. Possible values are:  - `\"private\"`: the container runs in its own private cgroup namespace - `\"host\"`: use the host system's cgroup namespace  If not specified, the daemon default is used, which can either be `\"private\"` or `\"host\"`, depending on daemon version, kernel support and configuration.
 * @param dns A list of DNS servers for the container to use.
 * @param dnsOptions A list of DNS options.
 * @param dnsSearch A list of DNS search domains.
 * @param extraHosts A list of hostnames/IP mappings to add to the container's `/etc/hosts` file. Specified in the form `[\"hostname:IP\"]`.
 * @param groupAdd A list of additional groups that the container process will run as.
 * @param ipcMode IPC sharing mode for the container. Possible values are:  - `\"none\"`: own private IPC namespace, with /dev/shm not mounted - `\"private\"`: own private IPC namespace - `\"shareable\"`: own private IPC namespace, with a possibility to share it with other containers - `\"container:<name|id>\"`: join another (shareable) container's IPC namespace - `\"host\"`: use the host system's IPC namespace  If not specified, daemon default is used, which can either be `\"private\"` or `\"shareable\"`, depending on daemon version and configuration.
 * @param cgroup Cgroup to use for the container.
 * @param links A list of links for the container in the form `container_name:alias`.
 * @param oomScoreAdj An integer value containing the score given to the container in order to tune OOM killer preferences.
 * @param pidMode Set the PID (Process) Namespace mode for the container. It can be either:  - `\"container:<name|id>\"`: joins another container's PID namespace - `\"host\"`: use the host's PID namespace inside the container
 * @param privileged Gives the container full access to the host.
 * @param publishAllPorts Allocates an ephemeral host port for all of a container's exposed ports.  Ports are de-allocated when the container stops and allocated when the container starts. The allocated port might be changed when restarting the container.  The port is selected from the ephemeral port range that depends on the kernel. For example, on Linux the range is defined by `/proc/sys/net/ipv4/ip_local_port_range`.
 * @param readonlyRootfs Mount the container's root filesystem as read only.
 * @param securityOpt A list of string values to customize labels for MLS systems, such as SELinux.
 * @param storageOpt Storage driver options for this container, in the form `{\"size\": \"120G\"}`.
 * @param tmpfs A map of container directories which should be replaced by tmpfs mounts, and their corresponding mount options. For example:  ``` { \"/run\": \"rw,noexec,nosuid,size=65536k\" } ```
 * @param utSMode UTS namespace to use for the container.
 * @param usernsMode Sets the usernamespace mode for the container when usernamespace remapping option is enabled.
 * @param shmSize Size of `/dev/shm` in bytes. If omitted, the system uses 64MB.
 * @param sysctls A list of kernel parameters (sysctls) to set in the container. For example:  ``` {\"net.ipv4.ip_forward\": \"1\"} ```
 * @param runtime Runtime to use with this container.
 * @param consoleSize Initial console size, as an `[height, width]` array. (Windows only)
 * @param isolation Isolation technology of the container. (Windows only)
 * @param maskedPaths The list of paths to be masked inside the container (this overrides the default set of paths).
 * @param readonlyPaths The list of paths to be set as read-only inside the container (this overrides the default set of paths).
 */
@JsonClass(generateAdapter = true)
data class HostConfig (
    /* An integer value representing this container's relative CPU weight versus other containers.  */
    @Json(name = "CpuShares")
    val cpuShares: kotlin.Int? = null,
    /* Memory limit in bytes. */
    @Json(name = "Memory")
    val memory: kotlin.Long? = null,
    /* Path to `cgroups` under which the container's `cgroup` is created. If the path is not absolute, the path is considered to be relative to the `cgroups` path of the init process. Cgroups are created if they do not already exist.  */
    @Json(name = "CgroupParent")
    val cgroupParent: kotlin.String? = null,
    /* Block IO weight (relative weight). */
    @Json(name = "BlkioWeight")
    val blkioWeight: kotlin.Int? = null,
    /* Block IO weight (relative device weight) in the form:  ``` [{\"Path\": \"device_path\", \"Weight\": weight}] ```  */
    @Json(name = "BlkioWeightDevice")
    val blkioWeightDevice: kotlin.collections.List<ResourcesBlkioWeightDevice>? = null,
    /* Limit read rate (bytes per second) from a device, in the form:  ``` [{\"Path\": \"device_path\", \"Rate\": rate}] ```  */
    @Json(name = "BlkioDeviceReadBps")
    val blkioDeviceReadBps: kotlin.collections.List<ThrottleDevice>? = null,
    /* Limit write rate (bytes per second) to a device, in the form:  ``` [{\"Path\": \"device_path\", \"Rate\": rate}] ```  */
    @Json(name = "BlkioDeviceWriteBps")
    val blkioDeviceWriteBps: kotlin.collections.List<ThrottleDevice>? = null,
    /* Limit read rate (IO per second) from a device, in the form:  ``` [{\"Path\": \"device_path\", \"Rate\": rate}] ```  */
    @Json(name = "BlkioDeviceReadIOps")
    val blkioDeviceReadIOps: kotlin.collections.List<ThrottleDevice>? = null,
    /* Limit write rate (IO per second) to a device, in the form:  ``` [{\"Path\": \"device_path\", \"Rate\": rate}] ```  */
    @Json(name = "BlkioDeviceWriteIOps")
    val blkioDeviceWriteIOps: kotlin.collections.List<ThrottleDevice>? = null,
    /* The length of a CPU period in microseconds. */
    @Json(name = "CpuPeriod")
    val cpuPeriod: kotlin.Long? = null,
    /* Microseconds of CPU time that the container can get in a CPU period.  */
    @Json(name = "CpuQuota")
    val cpuQuota: kotlin.Long? = null,
    /* The length of a CPU real-time period in microseconds. Set to 0 to allocate no time allocated to real-time tasks.  */
    @Json(name = "CpuRealtimePeriod")
    val cpuRealtimePeriod: kotlin.Long? = null,
    /* The length of a CPU real-time runtime in microseconds. Set to 0 to allocate no time allocated to real-time tasks.  */
    @Json(name = "CpuRealtimeRuntime")
    val cpuRealtimeRuntime: kotlin.Long? = null,
    /* CPUs in which to allow execution (e.g., `0-3`, `0,1`).  */
    @Json(name = "CpusetCpus")
    val cpusetCpus: kotlin.String? = null,
    /* Memory nodes (MEMs) in which to allow execution (0-3, 0,1). Only effective on NUMA systems.  */
    @Json(name = "CpusetMems")
    val cpusetMems: kotlin.String? = null,
    /* A list of devices to add to the container. */
    @Json(name = "Devices")
    val devices: kotlin.collections.List<DeviceMapping>? = null,
    /* a list of cgroup rules to apply to the container */
    @Json(name = "DeviceCgroupRules")
    val deviceCgroupRules: kotlin.collections.List<kotlin.String>? = null,
    /* A list of requests for devices to be sent to device drivers.  */
    @Json(name = "DeviceRequests")
    val deviceRequests: kotlin.collections.List<DeviceRequest>? = null,
    /* Kernel memory limit in bytes.  <p><br /></p>  > **Deprecated**: This field is deprecated as the kernel 5.4 deprecated > `kmem.limit_in_bytes`.  */
    @Json(name = "KernelMemory")
    val kernelMemory: kotlin.Long? = null,
    /* Hard limit for kernel TCP buffer memory (in bytes). */
    @Json(name = "KernelMemoryTCP")
    val kernelMemoryTCP: kotlin.Long? = null,
    /* Memory soft limit in bytes. */
    @Json(name = "MemoryReservation")
    val memoryReservation: kotlin.Long? = null,
    /* Total memory limit (memory + swap). Set as `-1` to enable unlimited swap.  */
    @Json(name = "MemorySwap")
    val memorySwap: kotlin.Long? = null,
    /* Tune a container's memory swappiness behavior. Accepts an integer between 0 and 100.  */
    @Json(name = "MemorySwappiness")
    val memorySwappiness: kotlin.Long? = null,
    /* CPU quota in units of 10<sup>-9</sup> CPUs. */
    @Json(name = "NanoCpus")
    val nanoCpus: kotlin.Long? = null,
    /* Disable OOM Killer for the container. */
    @Json(name = "OomKillDisable")
    val oomKillDisable: kotlin.Boolean? = null,
    /* Run an init inside the container that forwards signals and reaps processes. This field is omitted if empty, and the default (as configured on the daemon) is used.  */
    @Json(name = "Init")
    val init: kotlin.Boolean? = null,
    /* Tune a container's PIDs limit. Set `0` or `-1` for unlimited, or `null` to not change.  */
    @Json(name = "PidsLimit")
    val pidsLimit: kotlin.Long? = null,
    /* A list of resource limits to set in the container. For example:  ``` {\"Name\": \"nofile\", \"Soft\": 1024, \"Hard\": 2048} ```  */
    @Json(name = "Ulimits")
    val ulimits: kotlin.collections.List<ResourcesUlimits>? = null,
    /* The number of usable CPUs (Windows only).  On Windows Server containers, the processor resource controls are mutually exclusive. The order of precedence is `CPUCount` first, then `CPUShares`, and `CPUPercent` last.  */
    @Json(name = "CpuCount")
    val cpuCount: kotlin.Long? = null,
    /* The usable percentage of the available CPUs (Windows only).  On Windows Server containers, the processor resource controls are mutually exclusive. The order of precedence is `CPUCount` first, then `CPUShares`, and `CPUPercent` last.  */
    @Json(name = "CpuPercent")
    val cpuPercent: kotlin.Long? = null,
    /* Maximum IOps for the container system drive (Windows only) */
    @Json(name = "IOMaximumIOps")
    val ioMaximumIOps: kotlin.Long? = null,
    /* Maximum IO in bytes per second for the container system drive (Windows only).  */
    @Json(name = "IOMaximumBandwidth")
    val ioMaximumBandwidth: kotlin.Long? = null,
    /* A list of volume bindings for this container. Each volume binding is a string in one of these forms:  - `host-src:container-dest[:options]` to bind-mount a host path   into the container. Both `host-src`, and `container-dest` must   be an _absolute_ path. - `volume-name:container-dest[:options]` to bind-mount a volume   managed by a volume driver into the container. `container-dest`   must be an _absolute_ path.  `options` is an optional, comma-delimited list of:  - `nocopy` disables automatic copying of data from the container   path to the volume. The `nocopy` flag only applies to named volumes. - `[ro|rw]` mounts a volume read-only or read-write, respectively.   If omitted or set to `rw`, volumes are mounted read-write. - `[z|Z]` applies SELinux labels to allow or deny multiple containers   to read and write to the same volume.     - `z`: a _shared_ content label is applied to the content. This       label indicates that multiple containers can share the volume       content, for both reading and writing.     - `Z`: a _private unshared_ label is applied to the content.       This label indicates that only the current container can use       a private volume. Labeling systems such as SELinux require       proper labels to be placed on volume content that is mounted       into a container. Without a label, the security system can       prevent a container's processes from using the content. By       default, the labels set by the host operating system are not       modified. - `[[r]shared|[r]slave|[r]private]` specifies mount   [propagation behavior](https://www.kernel.org/doc/Documentation/filesystems/sharedsubtree.txt).   This only applies to bind-mounted volumes, not internal volumes   or named volumes. Mount propagation requires the source mount   point (the location where the source directory is mounted in the   host operating system) to have the correct propagation properties.   For shared volumes, the source mount point must be set to `shared`.   For slave volumes, the mount must be set to either `shared` or   `slave`.  */
    @Json(name = "Binds")
    val binds: kotlin.collections.List<kotlin.String>? = null,
    /* Path to a file where the container ID is written */
    @Json(name = "ContainerIDFile")
    val containerIDFile: kotlin.String? = null,
    @Json(name = "LogConfig")
    val logConfig: HostConfigAllOfLogConfig? = null,
    /* Network mode to use for this container. Supported standard values are: `bridge`, `host`, `none`, and `container:<name|id>`. Any other value is taken as a custom network's name to which this container should connect to.  */
    @Json(name = "NetworkMode")
    val networkMode: kotlin.String? = null,
    /* PortMap describes the mapping of container ports to host ports, using the container's port-number and protocol as key in the format `<port>/<protocol>`, for example, `80/udp`.  If a container's port is mapped for multiple protocols, separate entries are added to the mapping table.  */
    @Json(name = "PortBindings")
    val portBindings: kotlin.collections.Map<kotlin.String, kotlin.collections.List<PortBinding>>? = null,
    @Json(name = "RestartPolicy")
    val restartPolicy: RestartPolicy? = null,
    /* Automatically remove the container when the container's process exits. This has no effect if `RestartPolicy` is set.  */
    @Json(name = "AutoRemove")
    val autoRemove: kotlin.Boolean? = null,
    /* Driver that this container uses to mount volumes. */
    @Json(name = "VolumeDriver")
    val volumeDriver: kotlin.String? = null,
    /* A list of volumes to inherit from another container, specified in the form `<container name>[:<ro|rw>]`.  */
    @Json(name = "VolumesFrom")
    val volumesFrom: kotlin.collections.List<kotlin.String>? = null,
    /* Specification for mounts to be added to the container.  */
    @Json(name = "Mounts")
    val mounts: kotlin.collections.List<Mount>? = null,
    /* A list of kernel capabilities to add to the container. Conflicts with option 'Capabilities'.  */
    @Json(name = "CapAdd")
    val capAdd: kotlin.collections.List<kotlin.String>? = null,
    /* A list of kernel capabilities to drop from the container. Conflicts with option 'Capabilities'.  */
    @Json(name = "CapDrop")
    val capDrop: kotlin.collections.List<kotlin.String>? = null,
    /* cgroup namespace mode for the container. Possible values are:  - `\"private\"`: the container runs in its own private cgroup namespace - `\"host\"`: use the host system's cgroup namespace  If not specified, the daemon default is used, which can either be `\"private\"` or `\"host\"`, depending on daemon version, kernel support and configuration.  */
    @Json(name = "CgroupnsMode")
    val cgroupnsMode: HostConfig.CgroupnsMode? = null,
    /* A list of DNS servers for the container to use. */
    @Json(name = "Dns")
    val dns: kotlin.collections.List<kotlin.String>? = null,
    /* A list of DNS options. */
    @Json(name = "DnsOptions")
    val dnsOptions: kotlin.collections.List<kotlin.String>? = null,
    /* A list of DNS search domains. */
    @Json(name = "DnsSearch")
    val dnsSearch: kotlin.collections.List<kotlin.String>? = null,
    /* A list of hostnames/IP mappings to add to the container's `/etc/hosts` file. Specified in the form `[\"hostname:IP\"]`.  */
    @Json(name = "ExtraHosts")
    val extraHosts: kotlin.collections.List<kotlin.String>? = null,
    /* A list of additional groups that the container process will run as.  */
    @Json(name = "GroupAdd")
    val groupAdd: kotlin.collections.List<kotlin.String>? = null,
    /* IPC sharing mode for the container. Possible values are:  - `\"none\"`: own private IPC namespace, with /dev/shm not mounted - `\"private\"`: own private IPC namespace - `\"shareable\"`: own private IPC namespace, with a possibility to share it with other containers - `\"container:<name|id>\"`: join another (shareable) container's IPC namespace - `\"host\"`: use the host system's IPC namespace  If not specified, daemon default is used, which can either be `\"private\"` or `\"shareable\"`, depending on daemon version and configuration.  */
    @Json(name = "IpcMode")
    val ipcMode: kotlin.String? = null,
    /* Cgroup to use for the container. */
    @Json(name = "Cgroup")
    val cgroup: kotlin.String? = null,
    /* A list of links for the container in the form `container_name:alias`.  */
    @Json(name = "Links")
    val links: kotlin.collections.List<kotlin.String>? = null,
    /* An integer value containing the score given to the container in order to tune OOM killer preferences.  */
    @Json(name = "OomScoreAdj")
    val oomScoreAdj: kotlin.Int? = null,
    /* Set the PID (Process) Namespace mode for the container. It can be either:  - `\"container:<name|id>\"`: joins another container's PID namespace - `\"host\"`: use the host's PID namespace inside the container  */
    @Json(name = "PidMode")
    val pidMode: kotlin.String? = null,
    /* Gives the container full access to the host. */
    @Json(name = "Privileged")
    val privileged: kotlin.Boolean? = null,
    /* Allocates an ephemeral host port for all of a container's exposed ports.  Ports are de-allocated when the container stops and allocated when the container starts. The allocated port might be changed when restarting the container.  The port is selected from the ephemeral port range that depends on the kernel. For example, on Linux the range is defined by `/proc/sys/net/ipv4/ip_local_port_range`.  */
    @Json(name = "PublishAllPorts")
    val publishAllPorts: kotlin.Boolean? = null,
    /* Mount the container's root filesystem as read only. */
    @Json(name = "ReadonlyRootfs")
    val readonlyRootfs: kotlin.Boolean? = null,
    /* A list of string values to customize labels for MLS systems, such as SELinux. */
    @Json(name = "SecurityOpt")
    val securityOpt: kotlin.collections.List<kotlin.String>? = null,
    /* Storage driver options for this container, in the form `{\"size\": \"120G\"}`.  */
    @Json(name = "StorageOpt")
    val storageOpt: kotlin.collections.Map<kotlin.String, kotlin.String>? = null,
    /* A map of container directories which should be replaced by tmpfs mounts, and their corresponding mount options. For example:  ``` { \"/run\": \"rw,noexec,nosuid,size=65536k\" } ```  */
    @Json(name = "Tmpfs")
    val tmpfs: kotlin.collections.Map<kotlin.String, kotlin.String>? = null,
    /* UTS namespace to use for the container. */
    @Json(name = "UTSMode")
    val utSMode: kotlin.String? = null,
    /* Sets the usernamespace mode for the container when usernamespace remapping option is enabled.  */
    @Json(name = "UsernsMode")
    val usernsMode: kotlin.String? = null,
    /* Size of `/dev/shm` in bytes. If omitted, the system uses 64MB.  */
    @Json(name = "ShmSize")
    val shmSize: kotlin.Int? = null,
    /* A list of kernel parameters (sysctls) to set in the container. For example:  ``` {\"net.ipv4.ip_forward\": \"1\"} ```  */
    @Json(name = "Sysctls")
    val sysctls: kotlin.collections.Map<kotlin.String, kotlin.String>? = null,
    /* Runtime to use with this container. */
    @Json(name = "Runtime")
    val runtime: kotlin.String? = null,
    /* Initial console size, as an `[height, width]` array. (Windows only)  */
    @Json(name = "ConsoleSize")
    val consoleSize: kotlin.collections.List<kotlin.Int>? = null,
    /* Isolation technology of the container. (Windows only)  */
    @Json(name = "Isolation")
    val isolation: HostConfig.Isolation? = null,
    /* The list of paths to be masked inside the container (this overrides the default set of paths).  */
    @Json(name = "MaskedPaths")
    val maskedPaths: kotlin.collections.List<kotlin.String>? = null,
    /* The list of paths to be set as read-only inside the container (this overrides the default set of paths).  */
    @Json(name = "ReadonlyPaths")
    val readonlyPaths: kotlin.collections.List<kotlin.String>? = null
) {

    /**
     * cgroup namespace mode for the container. Possible values are:  - `\"private\"`: the container runs in its own private cgroup namespace - `\"host\"`: use the host system's cgroup namespace  If not specified, the daemon default is used, which can either be `\"private\"` or `\"host\"`, depending on daemon version, kernel support and configuration.
     * Values: private,host
     */
    enum class CgroupnsMode(val value: kotlin.String) {
        @Json(name = "private") private("private"),
        @Json(name = "host") host("host");
    }
    /**
     * Isolation technology of the container. (Windows only)
     * Values: default,process,hyperv
     */
    enum class Isolation(val value: kotlin.String) {
        @Json(name = "default") default("default"),
        @Json(name = "process") process("process"),
        @Json(name = "hyperv") hyperv("hyperv");
    }
}

