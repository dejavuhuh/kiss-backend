package kiss.infrastructure.lock

import org.redisson.api.RedissonClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DistributedLockConfiguration {

    @Bean
    fun distributedLockTemplate(redissonClient: RedissonClient): DistributedLockTemplate {
        val template = DistributedLockTemplate(redissonClient)
        DistributedLockAspect.template = template
        return template
    }
}
