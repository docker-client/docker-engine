package de.gesellix.docker.engine.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Merged from:
// - Resources (embedded)
// - RestartPolicy
@JsonClass(generateAdapter = true)
data class ContainerUpdateRequest(
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
  @Json(name = "RestartPolicy")
  val restartPolicy: RestartPolicy? = null,
)

