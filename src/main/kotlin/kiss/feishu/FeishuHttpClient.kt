package kiss.feishu

import kiss.okhttp.json
import okhttp3.OkHttpClient
import okhttp3.Request

class FeishuHttpClient {
    val client = OkHttpClient()
}

inline fun <reified T> FeishuHttpClient.execute(request: Request): T {
    val response = executeRaw<Response<T>>(request)
    if (response.code != OK) {
        throw FeishuApiException(response.code, response.msg)
    }
    return response.data
}

inline fun <reified T> FeishuHttpClient.executeRaw(request: Request): T {
    return client.newCall(request).json<T>()
}

class FeishuApiException(code: Long, msg: String) : Exception("[${code}] $msg")

const val OK = 0L

data class Response<T>(
    val code: Long,
    val msg: String,
    val data: T,
)
