package kiss.json

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.lang.reflect.Type

object JsonSerializer {

    val objectMapper: ObjectMapper = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .findAndRegisterModules()

    fun serialize(obj: Any?, pretty: Boolean = false): String {
        if (pretty) {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
        }
        return objectMapper.writeValueAsString(obj)
    }

    fun deserialize(json: String, type: Type): Any? {
        return objectMapper.readValue(json, objectMapper.constructType(type))
    }
}

inline fun <reified T> JsonSerializer.deserialize(json: String): T {
    return objectMapper.readValue<T>(json)
}
