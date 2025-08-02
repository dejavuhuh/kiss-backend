package kiss.infrastructure.okhttp

class HttpStatusException(code: Int, body: String?) : Exception("[$code] $body")
class ResponseBodyEmptyException() : Exception()
