package kiss.llm

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.util.concurrent.Executors

@Component
class LlmAbilityClient(val objectMapper: ObjectMapper) {

    private val executor = Executors.newVirtualThreadPerTaskExecutor()

}