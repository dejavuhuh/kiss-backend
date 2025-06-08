package kiss.okhttp

import kiss.json.JsonSerializer
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

val APPLICATION_JSON = "application/json; charset=utf-8".toMediaTypeOrNull()

fun <T> Request.Builder.json(body: T) = apply {
    val json = JsonSerializer.serialize(body)
    post(json.toRequestBody(APPLICATION_JSON))
}

inline fun <reified T> Call.json(): T = execute().use {
    if (!it.isSuccessful) throw HttpStatusException(it.code, it.body?.string())
    if (it.body == null) throw ResponseBodyEmptyException()
    return JsonSerializer.deserialize<T>(it.body!!.string())
}
