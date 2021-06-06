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

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * The behavior to apply when the container exits. The default is not to restart.  An ever increasing delay (double the previous delay, starting at 100ms) is added before each restart to prevent flooding the server.
 * @param name - Empty string means not to restart - `always` Always restart - `unless-stopped` Restart always except when the user has manually stopped the container - `on-failure` Restart only when the container exit code is non-zero
 * @param maximumRetryCount If `on-failure` is used, the number of times to retry before giving up.
 */
@JsonClass(generateAdapter = true)
data class RestartPolicy(
  /* - Empty string means not to restart - `always` Always restart - `unless-stopped` Restart always except when the user has manually stopped the container - `on-failure` Restart only when the container exit code is non-zero  */
  @Json(name = "Name")
  val name: RestartPolicy.Name? = null,
  /* If `on-failure` is used, the number of times to retry before giving up.  */
  @Json(name = "MaximumRetryCount")
  val maximumRetryCount: kotlin.Int? = null
) {

  /**
   * - Empty string means not to restart - `always` Always restart - `unless-stopped` Restart always except when the user has manually stopped the container - `on-failure` Restart only when the container exit code is non-zero
   * Values: eMPTY,always,unlessMinusStopped,onMinusFailure
   */
  enum class Name(val value: kotlin.String) {

    @Json(name = "")
    eMPTY(""),

    @Json(name = "always")
    always("always"),

    @Json(name = "unless-stopped")
    unlessMinusStopped("unless-stopped"),

    @Json(name = "on-failure")
    onMinusFailure("on-failure");
  }
}

