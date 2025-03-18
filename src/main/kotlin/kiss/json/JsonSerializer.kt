package kiss.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.lang.reflect.Type

object JsonSerializer {

    private val objectMapper = ObjectMapper().registerKotlinModule()

    fun serialize(obj: Any?): String {
        return objectMapper.writeValueAsString(obj)
    }

    fun deserialize(json: String, type: Type): Any? {
        return objectMapper.readValue(json, objectMapper.constructType(type))
    }
}