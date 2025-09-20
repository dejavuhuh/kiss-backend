package kiss

import com.fasterxml.jackson.databind.ObjectMapper
import kiss.infrastructure.cache.LocalCacheAspect
import kiss.web.trace.LoggingAspect
import org.aspectj.lang.Aspects
import org.babyfish.jimmer.client.EnableImplicitApi
import org.springframework.beans.factory.getBean
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableImplicitApi
@SpringBootApplication
class KissApplication

fun main(args: Array<String>) {
    val ctx = runApplication<KissApplication>(*args)

    // 初始化 Aspectj
    val objectMapper = ctx.getBean<ObjectMapper>()
    Aspects.aspectOf(LocalCacheAspect::class.java).configure(objectMapper)
    Aspects.aspectOf(LoggingAspect::class.java).configure(objectMapper)
}
