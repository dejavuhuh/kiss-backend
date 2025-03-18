package kiss

import kiss.cache.Cacheable
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KissBackendApplication

fun main(args: Array<String>) {
    runApplication<KissBackendApplication>(*args)
    val demo = Demo()
    demo.doSomething()
}

class Demo {
    @Cacheable
    fun doSomething() = "Hello"
}
