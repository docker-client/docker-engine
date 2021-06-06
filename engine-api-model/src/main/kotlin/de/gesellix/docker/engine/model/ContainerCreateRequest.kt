/**
 * Docker Engine API
 * The Engine API is an HTTP API served by Docker Engine. It is the API the Docker client uses to communicate with the Engine, so everything the Docker client can do can be done with the API.  Most of the client's commands map directly to API endpoints (e.g. `docker ps` is `GET /containers/json`). The notable exception is running containers, which consists of several API calls.  # Errors  The API uses standard HTTP status codes to indicate the success or failure of the API call. The body of the response will be JSON in the following format:  ``` {   \"message\": \"page not found\" } ```  # Versioning  The API is usually changed in each release, so API calls are versioned to ensure that clients don't break. To lock to a specific version of the API, you prefix the URL with its version, for example, call `/v1.30/info` to use the v1.30 version of the `/info` endpoint. If the API version specified in the URL is not supported by the daemon, a HTTP `400 Bad Request` error message is returned.  If you omit the version-prefix, the current version of the API (v1.41) is used. For example, calling `/info` is the same as calling `/v1.41/info`. Using the API without a version-prefix is deprecated and will be removed in a future release.  Engine releases in the near future should support this version of the API, so your client will continue to work even if it is talking to a newer Engine.  The API uses an open schema model, which means server may add extra properties to responses. Likewise, the server will ignore any extra query parameters and request body properties. When you write clients, you need to ignore additional properties in responses to ensure they do not break when talking to newer daemons.   # Authentication  Authentication for registries is handled client side. The client has to send authentication details to various endpoints that need to communicate with registries, such as `POST /images/(name)/push`. These are sent as `X-Registry-Auth` header as a [base64url encoded](https://tools.ietf.org/html/rfc4648#section-5) (JSON) string with the following structure:  ``` {   \"username\": \"string\",   \"password\": \"string\",   \"email\": \"string\",   \"serveraddress\": \"string\" } ```  The `serveraddress` is a domain/IP without a protocol. Throughout this structure, double quotes are required.  If you have already got an identity token from the [`/auth` endpoint](#operation/SystemAuth), you can just pass this instead of credentials:  ``` {   \"identitytoken\": \"9cbaf023786cd7...\" } ```
 *
 * The version of the OpenAPI document: 1.41
 */
package de.gesellix.docker.engine.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Merged from:
// - ContainerConfig (embedded)
// - HostConfig
// - NetworkingConfig
@JsonClass(generateAdapter = true)
data class ContainerCreateRequest(
  /* The hostname to use for the container, as a valid RFC 1123 hostname. */
  @Json(name = "Hostname")
  val hostname: kotlin.String? = null,
  /* The domain name to use for the container. */
  @Json(name = "Domainname")
  val domainname: kotlin.String? = null,
  /* The user that commands are run as inside the container. */
  @Json(name = "User")
  val user: kotlin.String? = null,
  /* Whether to attach to `stdin`. */
  @Json(name = "AttachStdin")
  val attachStdin: kotlin.Boolean? = null,
  /* Whether to attach to `stdout`. */
  @Json(name = "AttachStdout")
  val attachStdout: kotlin.Boolean? = null,
  /* Whether to attach to `stderr`. */
  @Json(name = "AttachStderr")
  val attachStderr: kotlin.Boolean? = null,
  /* An object mapping ports to an empty object in the form:  `{\"<port>/<tcp|udp|sctp>\": {}}`  */
  @Json(name = "ExposedPorts")
  val exposedPorts: kotlin.collections.Map<kotlin.String, kotlin.Any>? = null,
  /* Attach standard streams to a TTY, including `stdin` if it is not closed.  */
  @Json(name = "Tty")
  val tty: kotlin.Boolean? = null,
  /* Open `stdin` */
  @Json(name = "OpenStdin")
  val openStdin: kotlin.Boolean? = null,
  /* Close `stdin` after one attached client disconnects */
  @Json(name = "StdinOnce")
  val stdinOnce: kotlin.Boolean? = null,
  /* A list of environment variables to set inside the container in the form `[\"VAR=value\", ...]`. A variable without `=` is removed from the environment, rather than to have an empty value.  */
  @Json(name = "Env")
  val env: kotlin.collections.List<kotlin.String>? = null,
  /* Command to run specified as a string or an array of strings.  */
  @Json(name = "Cmd")
  val cmd: kotlin.collections.List<kotlin.String>? = null,
  @Json(name = "Healthcheck")
  val healthcheck: HealthConfig? = null,
  /* Command is already escaped (Windows only) */
  @Json(name = "ArgsEscaped")
  val argsEscaped: kotlin.Boolean? = null,
  /* The name of the image to use when creating the container/  */
  @Json(name = "Image")
  val image: kotlin.String? = null,
  /* An object mapping mount point paths inside the container to empty objects.  */
  @Json(name = "Volumes")
  val volumes: kotlin.collections.Map<kotlin.String, kotlin.Any>? = null,
  /* The working directory for commands to run in. */
  @Json(name = "WorkingDir")
  val workingDir: kotlin.String? = null,
  /* The entry point for the container as a string or an array of strings.  If the array consists of exactly one empty string (`[\"\"]`) then the entry point is reset to system default (i.e., the entry point used by docker when there is no `ENTRYPOINT` instruction in the `Dockerfile`).  */
  @Json(name = "Entrypoint")
  val entrypoint: kotlin.collections.List<kotlin.String>? = null,
  /* Disable networking for the container. */
  @Json(name = "NetworkDisabled")
  val networkDisabled: kotlin.Boolean? = null,
  /* MAC address of the container. */
  @Json(name = "MacAddress")
  val macAddress: kotlin.String? = null,
  /* `ONBUILD` metadata that were defined in the image's `Dockerfile`.  */
  @Json(name = "OnBuild")
  val onBuild: kotlin.collections.List<kotlin.String>? = null,
  /* User-defined key/value metadata. */
  @Json(name = "Labels")
  val labels: kotlin.collections.Map<kotlin.String, kotlin.String>? = null,
  /* Signal to stop a container as a string or unsigned integer.  */
  @Json(name = "StopSignal")
  val stopSignal: kotlin.String? = null,
  /* Timeout to stop a container in seconds. */
  @Json(name = "StopTimeout")
  val stopTimeout: kotlin.Int? = null,
  /* Shell for when `RUN`, `CMD`, and `ENTRYPOINT` uses a shell.  */
  @Json(name = "Shell")
  val shell: kotlin.collections.List<kotlin.String>? = null,
  @Json(name = "HostConfig")
  val hostConfig: HostConfig? = null,
  @Json(name = "NetworkingConfig")
  val networkingConfig: NetworkingConfig? = null,
)

