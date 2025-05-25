package kiss.web

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig() : WebMvcConfigurer {

    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.removeIf { it is StringHttpMessageConverter }
    }

    @Bean
    fun objectMapperCustomizer() = object : Jackson2ObjectMapperBuilderCustomizer {
        override fun customize(builder: Jackson2ObjectMapperBuilder) {
            builder.serializationInclusion(JsonInclude.Include.NON_NULL)
        }
    }
}