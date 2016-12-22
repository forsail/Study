# 动态 permission

## 权限分类

- 正常权限
正常权限涵盖应用需要访问其沙盒外部数据或资源，但对用户隐私或其他应用操作风险很小的区域。例如，设置时区的权限就是正常权限。如果应用声明其需要正常权限，系统会自动向应用授予该权限。

- 危险权限
危险权限涵盖应用需要涉及用户隐私信息的数据或资源，或者可能对用户存储的数据或其他应用的操作产生影响的区域。例如，能够读取用户的联系人属于危险权限。如果应用声明其需要危险权限，则用户必须明确向应用授予该权限.


- 特殊权限
有许多权限其行为方式与正常权限及危险权限都不同。SYSTEM_ALERT_WINDOW 和 WRITE_SETTINGS 特别敏感，因此大多数应用不应该使用它们。如果某应用需要其中一种权限，必须在清单中声明该权限，并且发送请求用户授权的 intent。系统将向用户显示详细管理屏幕，以响应该 intent。 


### 权限组

所有危险的 Android 系统权限都属于权限组。如果设备运行的是 Android 6.0（API 级别 23），并且应用的 targetSdkVersion 是 23 或更高版本，则当用户请求危险权限时系统会发生以下行为：
- 如果应用请求其清单中列出的危险权限，而应用目前在权限组中没有任何权限，则系统会向用户显示一个对话框，描述应用要访问的权限组。对话框不描述该组内的具体权限。例如，如果应用请求 READ_CONTACTS 权限，系统对话框只说明该应用需要访问设备的联系信息。如果用户批准，系统将向应用授予其请求的权限。
- 如果应用请求其清单中列出的危险权限，而应用在同一权限组中已有另一项危险权限，则系统会立即授予该权限，而无需与用户进行任何交互。例如，如果某应用已经请求并且被授予了 READ_CONTACTS 权限，然后它又请求 WRITE_CONTACTS，系统将立即授予该权限。


| 权限       | 权限组  |
| --------   | -----:  |

|  CALENDAR    |  READ_CALENDAR       |
|      |    WRITE_CALENDAR     |
|   CAMERA   |    CAMERA     |
|   CONTACTS   |    READ_CONTACTS     |
|      |    WRITE_CONTACTS     |
|      |    GET_ACCOUNTS     |
|   LOCATION   |   ACCESS_FINE_LOCATION      |
|      |    ACCESS_COARSE_LOCATION     |
|    MICROPHONE  |  RECORD_AUDIO       |
|   PHONE   |     READ_PHONE_STATE    |
|      |      CALL_PHONE   |
|      |   READ_CALL_LOG      |
|      |    WRITE_CALL_LOG     |
|      |     ADD_VOICEMAIL    |   
|      |     USE_SIP    |
|      |     PROCESS_OUTGOING_CALLS    |
|  SENSORS    |    BODY_SENSORS     |
|   SMS   |    SEND_SMS     |
|      |    RECEIVE_SMS     |
|      |      READ_SMS   |
|      |      RECEIVE_WAP_PUSH   |
|      |      RECEIVE_MMS   |
|   STORAGE   |   READ_EXTERNAL_STORAGE      |
 |      |     WRITE_EXTERNAL_STORAGE    |



## 危险权限在不用版本中的表现差异
- 如果设备运行的是 Android 6.0（API 级别 23）或更高版本，并且应用的 targetSdkVersion 是 23 或更高版本，则应用在运行时向用户请求权限。用户可随时调用权限，因此应用在每次运行时均需检查自身是否具备所需的权限。

- 如果设备运行的是 Android 5.1（API 级别 22）或更低版本，并且应用的 targetSdkVersion 是 22 或更低版本，则系统会在用户安装应用时要求用户授予权限。如果将新权限添加到更新的应用版本，系统会在用户更新应用时要求授予该权限。用户一旦安装应用，他们撤销权限的唯一方式是卸载应用。


任何权限都可属于一个权限组，包括正常权限和应用定义的权限。但权限组仅当权限危险时才影响用户体验。可以忽略正常权限的权限组。

如果设备运行的是 Android 5.1（API 级别 22）或更低版本，并且应用的 targetSdkVersion 是 22 或更低版本，则系统会在安装时要求用户授予权限。再次强调，系统只告诉用户应用需要的权限组，而不告知具体权限。 







## 自动权限调整

随着时间的推移，平台中可能会加入新的限制，要想使用特定 API ，可能必须请求之前不需要的权限。假设当前的应用可随意调用这些功能(API),而没有任何限制(之前没有任何限制)，Android 可能会将新增的这个请求权限应用到应用清单，以免在新平台版本上 crash。Android 将根据为 targetSdkVersion 属性提供的值决定应用是否需要权限。如果该值低于在其中添加权限的版本，则 Android 会添加该权限。

> 例如，API 级别 4 中加入了 WRITE_EXTERNAL_STORAGE 权限，用以限制访问共享存储空间。如果您的 targetSdkVersion 为 3 或更低版本，则会向更新 Android 版本设备上的应用添加此权限。

> 注意：如果某权限自动添加到应用，则即使您的应用可能实际并不需要这些附加权限，Google Play 上的应用列表也会列出它们。
为避免这种情况，并且删除您不需要的默认权限，请始终将 targetSdkVersion 更新至最高版本。可在 Build.VERSION_CODES 文档中查看各版本添加的权限。
正

概括：就是低版本上没有加以限制的功能，高版本会自动为应用加上。

