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
package de.gesellix.docker.engine.api

import de.gesellix.docker.engine.model.ErrorResponse
import de.gesellix.docker.engine.model.InlineObject
import de.gesellix.docker.engine.model.Plugin
import de.gesellix.docker.engine.model.PluginPrivilegeItem

import org.openapitools.client.infrastructure.ApiClient
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.infrastructure.ClientError
import org.openapitools.client.infrastructure.ServerException
import org.openapitools.client.infrastructure.ServerError
import org.openapitools.client.infrastructure.MultiValueMap
import org.openapitools.client.infrastructure.RequestConfig
import org.openapitools.client.infrastructure.RequestMethod
import org.openapitools.client.infrastructure.ResponseType
import org.openapitools.client.infrastructure.Success
import org.openapitools.client.infrastructure.toMultiValue

class PluginApi(basePath: kotlin.String = defaultBasePath) : ApiClient(basePath) {
    companion object {
        @JvmStatic
        val defaultBasePath: String by lazy {
            System.getProperties().getProperty("org.openapitools.client.baseUrl", "http://localhost/v1.41")
        }
    }

    /**
    * Get plugin privileges
    * 
    * @param remote The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @return kotlin.collections.List<PluginPrivilegeItem>
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun getPluginPrivileges(remote: kotlin.String) : kotlin.collections.List<PluginPrivilegeItem> {
        val localVariableConfig = getPluginPrivilegesRequestConfig(remote = remote)

        val localVarResponse = request<kotlin.collections.List<PluginPrivilegeItem>>(
            localVariableConfig
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as kotlin.collections.List<PluginPrivilegeItem>
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * To obtain the request config of the operation getPluginPrivileges
    *
    * @param remote The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @return RequestConfig
    */
    fun getPluginPrivilegesRequestConfig(remote: kotlin.String) : RequestConfig {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mutableMapOf<kotlin.String, List<kotlin.String>>()
            .apply {
                put("remote", listOf(remote.toString()))
            }
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        
        val localVariableConfig = RequestConfig(
            method = RequestMethod.GET,
            path = "/plugins/privileges",
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )

        return localVariableConfig
    }

    /**
    * Create a plugin
    * 
    * @param name The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @param tarContext Path to tar containing plugin rootfs and manifest (optional)
    * @return void
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun pluginCreate(name: kotlin.String, tarContext: java.io.File?) : Unit {
        val localVariableConfig = pluginCreateRequestConfig(name = name, tarContext = tarContext)

        val localVarResponse = request<Any?>(
            localVariableConfig
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> Unit
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * To obtain the request config of the operation pluginCreate
    *
    * @param name The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @param tarContext Path to tar containing plugin rootfs and manifest (optional)
    * @return RequestConfig
    */
    fun pluginCreateRequestConfig(name: kotlin.String, tarContext: java.io.File?) : RequestConfig {
        val localVariableBody: kotlin.Any? = tarContext
        val localVariableQuery: MultiValueMap = mutableMapOf<kotlin.String, List<kotlin.String>>()
            .apply {
                put("name", listOf(name.toString()))
            }
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        
        val localVariableConfig = RequestConfig(
            method = RequestMethod.POST,
            path = "/plugins/create",
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )

        return localVariableConfig
    }

    /**
    * Remove a plugin
    * 
    * @param name The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @param force Disable the plugin before removing. This may result in issues if the plugin is in use by a container.  (optional, default to false)
    * @return Plugin
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun pluginDelete(name: kotlin.String, force: kotlin.Boolean?) : Plugin {
        val localVariableConfig = pluginDeleteRequestConfig(name = name, force = force)

        val localVarResponse = request<Plugin>(
            localVariableConfig
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as Plugin
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * To obtain the request config of the operation pluginDelete
    *
    * @param name The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @param force Disable the plugin before removing. This may result in issues if the plugin is in use by a container.  (optional, default to false)
    * @return RequestConfig
    */
    fun pluginDeleteRequestConfig(name: kotlin.String, force: kotlin.Boolean?) : RequestConfig {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mutableMapOf<kotlin.String, List<kotlin.String>>()
            .apply {
                if (force != null) {
                    put("force", listOf(force.toString()))
                }
            }
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        
        val localVariableConfig = RequestConfig(
            method = RequestMethod.DELETE,
            path = "/plugins/{name}".replace("{"+"name"+"}", "$name"),
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )

        return localVariableConfig
    }

    /**
    * Disable a plugin
    * 
    * @param name The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @return void
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun pluginDisable(name: kotlin.String) : Unit {
        val localVariableConfig = pluginDisableRequestConfig(name = name)

        val localVarResponse = request<Any?>(
            localVariableConfig
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> Unit
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * To obtain the request config of the operation pluginDisable
    *
    * @param name The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @return RequestConfig
    */
    fun pluginDisableRequestConfig(name: kotlin.String) : RequestConfig {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        
        val localVariableConfig = RequestConfig(
            method = RequestMethod.POST,
            path = "/plugins/{name}/disable".replace("{"+"name"+"}", "$name"),
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )

        return localVariableConfig
    }

    /**
    * Enable a plugin
    * 
    * @param name The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @param timeout Set the HTTP client timeout (in seconds) (optional, default to 0)
    * @return void
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun pluginEnable(name: kotlin.String, timeout: kotlin.Int?) : Unit {
        val localVariableConfig = pluginEnableRequestConfig(name = name, timeout = timeout)

        val localVarResponse = request<Any?>(
            localVariableConfig
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> Unit
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * To obtain the request config of the operation pluginEnable
    *
    * @param name The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @param timeout Set the HTTP client timeout (in seconds) (optional, default to 0)
    * @return RequestConfig
    */
    fun pluginEnableRequestConfig(name: kotlin.String, timeout: kotlin.Int?) : RequestConfig {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mutableMapOf<kotlin.String, List<kotlin.String>>()
            .apply {
                if (timeout != null) {
                    put("timeout", listOf(timeout.toString()))
                }
            }
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        
        val localVariableConfig = RequestConfig(
            method = RequestMethod.POST,
            path = "/plugins/{name}/enable".replace("{"+"name"+"}", "$name"),
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )

        return localVariableConfig
    }

    /**
    * Inspect a plugin
    * 
    * @param name The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @return Plugin
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun pluginInspect(name: kotlin.String) : Plugin {
        val localVariableConfig = pluginInspectRequestConfig(name = name)

        val localVarResponse = request<Plugin>(
            localVariableConfig
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as Plugin
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * To obtain the request config of the operation pluginInspect
    *
    * @param name The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @return RequestConfig
    */
    fun pluginInspectRequestConfig(name: kotlin.String) : RequestConfig {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        
        val localVariableConfig = RequestConfig(
            method = RequestMethod.GET,
            path = "/plugins/{name}/json".replace("{"+"name"+"}", "$name"),
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )

        return localVariableConfig
    }

    /**
    * List plugins
    * Returns information about installed plugins.
    * @param filters A JSON encoded value of the filters (a &#x60;map[string][]string&#x60;) to process on the plugin list.  Available filters:  - &#x60;capability&#x3D;&lt;capability name&gt;&#x60; - &#x60;enable&#x3D;&lt;true&gt;|&lt;false&gt;&#x60;  (optional)
    * @return kotlin.collections.List<Plugin>
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun pluginList(filters: kotlin.String?) : kotlin.collections.List<Plugin> {
        val localVariableConfig = pluginListRequestConfig(filters = filters)

        val localVarResponse = request<kotlin.collections.List<Plugin>>(
            localVariableConfig
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> (localVarResponse as Success<*>).data as kotlin.collections.List<Plugin>
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * To obtain the request config of the operation pluginList
    *
    * @param filters A JSON encoded value of the filters (a &#x60;map[string][]string&#x60;) to process on the plugin list.  Available filters:  - &#x60;capability&#x3D;&lt;capability name&gt;&#x60; - &#x60;enable&#x3D;&lt;true&gt;|&lt;false&gt;&#x60;  (optional)
    * @return RequestConfig
    */
    fun pluginListRequestConfig(filters: kotlin.String?) : RequestConfig {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mutableMapOf<kotlin.String, List<kotlin.String>>()
            .apply {
                if (filters != null) {
                    put("filters", listOf(filters.toString()))
                }
            }
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        
        val localVariableConfig = RequestConfig(
            method = RequestMethod.GET,
            path = "/plugins",
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )

        return localVariableConfig
    }

    /**
    * Install a plugin
    * Pulls and installs a plugin. After the plugin is installed, it can be enabled using the [&#x60;POST /plugins/{name}/enable&#x60; endpoint](#operation/PostPluginsEnable). 
    * @param remote Remote reference for plugin to install.  The &#x60;:latest&#x60; tag is optional, and is used as the default if omitted.  
    * @param name Local name for the pulled plugin.  The &#x60;:latest&#x60; tag is optional, and is used as the default if omitted.  (optional)
    * @param xRegistryAuth A base64url-encoded auth configuration to use when pulling a plugin from a registry.  Refer to the [authentication section](#section/Authentication) for details.  (optional)
    * @param body  (optional)
    * @return void
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun pluginPull(remote: kotlin.String, name: kotlin.String?, xRegistryAuth: kotlin.String?, body: kotlin.collections.List<InlineObject>?) : Unit {
        val localVariableConfig = pluginPullRequestConfig(remote = remote, name = name, xRegistryAuth = xRegistryAuth, body = body)

        val localVarResponse = request<Any?>(
            localVariableConfig
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> Unit
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * To obtain the request config of the operation pluginPull
    *
    * @param remote Remote reference for plugin to install.  The &#x60;:latest&#x60; tag is optional, and is used as the default if omitted.  
    * @param name Local name for the pulled plugin.  The &#x60;:latest&#x60; tag is optional, and is used as the default if omitted.  (optional)
    * @param xRegistryAuth A base64url-encoded auth configuration to use when pulling a plugin from a registry.  Refer to the [authentication section](#section/Authentication) for details.  (optional)
    * @param body  (optional)
    * @return RequestConfig
    */
    fun pluginPullRequestConfig(remote: kotlin.String, name: kotlin.String?, xRegistryAuth: kotlin.String?, body: kotlin.collections.List<InlineObject>?) : RequestConfig {
        val localVariableBody: kotlin.Any? = body
        val localVariableQuery: MultiValueMap = mutableMapOf<kotlin.String, List<kotlin.String>>()
            .apply {
                put("remote", listOf(remote.toString()))
                if (name != null) {
                    put("name", listOf(name.toString()))
                }
            }
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        xRegistryAuth?.apply { localVariableHeaders["X-Registry-Auth"] = this.toString() }
        
        val localVariableConfig = RequestConfig(
            method = RequestMethod.POST,
            path = "/plugins/pull",
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )

        return localVariableConfig
    }

    /**
    * Push a plugin
    * Push a plugin to the registry. 
    * @param name The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @return void
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun pluginPush(name: kotlin.String) : Unit {
        val localVariableConfig = pluginPushRequestConfig(name = name)

        val localVarResponse = request<Any?>(
            localVariableConfig
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> Unit
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * To obtain the request config of the operation pluginPush
    *
    * @param name The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @return RequestConfig
    */
    fun pluginPushRequestConfig(name: kotlin.String) : RequestConfig {
        val localVariableBody: kotlin.Any? = null
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        
        val localVariableConfig = RequestConfig(
            method = RequestMethod.POST,
            path = "/plugins/{name}/push".replace("{"+"name"+"}", "$name"),
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )

        return localVariableConfig
    }

    /**
    * Configure a plugin
    * 
    * @param name The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @param body  (optional)
    * @return void
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun pluginSet(name: kotlin.String, body: kotlin.collections.List<kotlin.String>?) : Unit {
        val localVariableConfig = pluginSetRequestConfig(name = name, body = body)

        val localVarResponse = request<Any?>(
            localVariableConfig
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> Unit
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * To obtain the request config of the operation pluginSet
    *
    * @param name The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @param body  (optional)
    * @return RequestConfig
    */
    fun pluginSetRequestConfig(name: kotlin.String, body: kotlin.collections.List<kotlin.String>?) : RequestConfig {
        val localVariableBody: kotlin.Any? = body
        val localVariableQuery: MultiValueMap = mutableMapOf()
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        
        val localVariableConfig = RequestConfig(
            method = RequestMethod.POST,
            path = "/plugins/{name}/set".replace("{"+"name"+"}", "$name"),
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )

        return localVariableConfig
    }

    /**
    * Upgrade a plugin
    * 
    * @param name The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @param remote Remote reference to upgrade to.  The &#x60;:latest&#x60; tag is optional, and is used as the default if omitted.  
    * @param xRegistryAuth A base64url-encoded auth configuration to use when pulling a plugin from a registry.  Refer to the [authentication section](#section/Authentication) for details.  (optional)
    * @param body  (optional)
    * @return void
    * @throws UnsupportedOperationException If the API returns an informational or redirection response
    * @throws ClientException If the API returns a client error response
    * @throws ServerException If the API returns a server error response
    */
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    fun pluginUpgrade(name: kotlin.String, remote: kotlin.String, xRegistryAuth: kotlin.String?, body: kotlin.collections.List<InlineObject>?) : Unit {
        val localVariableConfig = pluginUpgradeRequestConfig(name = name, remote = remote, xRegistryAuth = xRegistryAuth, body = body)

        val localVarResponse = request<Any?>(
            localVariableConfig
        )

        return when (localVarResponse.responseType) {
            ResponseType.Success -> Unit
            ResponseType.Informational -> throw UnsupportedOperationException("Client does not support Informational responses.")
            ResponseType.Redirection -> throw UnsupportedOperationException("Client does not support Redirection responses.")
            ResponseType.ClientError -> {
                val localVarError = localVarResponse as ClientError<*>
                throw ClientException("Client error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
            ResponseType.ServerError -> {
                val localVarError = localVarResponse as ServerError<*>
                throw ServerException("Server error : ${localVarError.statusCode} ${localVarError.message.orEmpty()}", localVarError.statusCode, localVarResponse)
            }
        }
    }

    /**
    * To obtain the request config of the operation pluginUpgrade
    *
    * @param name The name of the plugin. The &#x60;:latest&#x60; tag is optional, and is the default if omitted.  
    * @param remote Remote reference to upgrade to.  The &#x60;:latest&#x60; tag is optional, and is used as the default if omitted.  
    * @param xRegistryAuth A base64url-encoded auth configuration to use when pulling a plugin from a registry.  Refer to the [authentication section](#section/Authentication) for details.  (optional)
    * @param body  (optional)
    * @return RequestConfig
    */
    fun pluginUpgradeRequestConfig(name: kotlin.String, remote: kotlin.String, xRegistryAuth: kotlin.String?, body: kotlin.collections.List<InlineObject>?) : RequestConfig {
        val localVariableBody: kotlin.Any? = body
        val localVariableQuery: MultiValueMap = mutableMapOf<kotlin.String, List<kotlin.String>>()
            .apply {
                put("remote", listOf(remote.toString()))
            }
        val localVariableHeaders: MutableMap<String, String> = mutableMapOf()
        xRegistryAuth?.apply { localVariableHeaders["X-Registry-Auth"] = this.toString() }
        
        val localVariableConfig = RequestConfig(
            method = RequestMethod.POST,
            path = "/plugins/{name}/upgrade".replace("{"+"name"+"}", "$name"),
            query = localVariableQuery,
            headers = localVariableHeaders,
            body = localVariableBody
        )

        return localVariableConfig
    }

}
