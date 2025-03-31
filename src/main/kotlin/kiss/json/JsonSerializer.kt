package kiss.json

import com.fasterxml.jackson.databind.ObjectMapper
import java.lang.reflect.Type

object JsonSerializer {

    private val objectMapper = ObjectMapper().findAndRegisterModules()

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
