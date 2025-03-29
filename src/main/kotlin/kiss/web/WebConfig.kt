package kiss.web

import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig() : WebMvcConfigurer {

    override fun extendMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.removeIf { it is StringHttpMessageConverter }
    }
}