package kiss.feishu

import com.lark.oapi.core.response.BaseResponse
import kiss.okhttp.json
import okhttp3.OkHttpClient
import okhttp3.Request

class FeishuHttpClient {
    val client = OkHttpClient()
}

inline fun <reified T> FeishuHttpClient.execute(request: Request): T {
    val response = executeRaw<Response<T>>(request)
    if (response.code != OK) {
        throw FeishuApiHttpException(response.code, response.msg)
    }
    return response.data
}

inline fun <reified T> FeishuHttpClient.executeRaw(request: Request): T {
    return client.newCall(request).json<T>()
}

class FeishuApiHttpException(code: Long, msg: String) : Exception("[${code}] $msg")
class FeishuAccessTokenException(code: Long, error: String?, errorDescription: String?) :
    Exception("[${code}] $error: $errorDescription")

class FeishuApiException(response: BaseResponse<*>) : Exception(
    """飞书 API 调用失败
    |code: ${response.code}
    |msg: ${response.msg}
    |requestId: ${response.requestId}
    |rawResponse: ${String(response.rawResponse.body, Charsets.UTF_8)}
""".trimMargin()
)

const

val OK = 0L

data class Response<T>(
    val code: Long,
    val msg: String,
    val data: T,
)
