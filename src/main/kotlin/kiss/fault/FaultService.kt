package kiss.fault

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

@RestController
@RequestMapping("/fault")
class FaultService {

    /**
     * 模拟高CPU使用率
     */
    @PostMapping("/high-cpu")
    fun highCpu() {
        val threadsToUse = Runtime.getRuntime().availableProcessors()
        val executor = Executors.newFixedThreadPool(threadsToUse)
        val startTime = System.currentTimeMillis()
        val endTime = startTime + 1000 * 10 // 10秒
        repeat(threadsToUse) {
            executor.submit {
                while (System.currentTimeMillis() < endTime) {
                    sqrt(Math.random())
                }
            }
        }
        executor.awaitTermination(10, TimeUnit.SECONDS)
    }

    /**
     * CPU密集型请求
     */
    @GetMapping("/cpu-intensive")
    fun cpuIntensive() {
        repeat(100_0000) {
            sqrt(Math.random())
        }
    }

    /**
     * 服务端异常
     */
    @GetMapping("/server-error")
    fun serverError() {
        throw RuntimeException("服务端异常")
    }
}
