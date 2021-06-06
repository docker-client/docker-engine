
# SystemVersion

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**platform** | [**SystemVersionPlatform**](SystemVersionPlatform.md) |  |  [optional]
**components** | [**kotlin.collections.List&lt;SystemVersionComponents&gt;**](SystemVersionComponents.md) | Information about system components  |  [optional]
**version** | **kotlin.String** | The version of the daemon |  [optional]
**apiVersion** | **kotlin.String** | The default (and highest) API version that is supported by the daemon  |  [optional]
**minAPIVersion** | **kotlin.String** | The minimum API version that is supported by the daemon  |  [optional]
**gitCommit** | **kotlin.String** | The Git commit of the source code that was used to build the daemon  |  [optional]
**goVersion** | **kotlin.String** | The version Go used to compile the daemon, and the version of the Go runtime in use.  |  [optional]
**os** | **kotlin.String** | The operating system that the daemon is running on (\&quot;linux\&quot; or \&quot;windows\&quot;)  |  [optional]
**arch** | **kotlin.String** | The architecture that the daemon is running on  |  [optional]
**kernelVersion** | **kotlin.String** | The kernel version (&#x60;uname -r&#x60;) that the daemon is running on.  This field is omitted when empty.  |  [optional]
**experimental** | **kotlin.Boolean** | Indicates if the daemon is started with experimental features enabled.  This field is omitted when empty / false.  |  [optional]
**buildTime** | **kotlin.String** | The date and time that the daemon was compiled.  |  [optional]



