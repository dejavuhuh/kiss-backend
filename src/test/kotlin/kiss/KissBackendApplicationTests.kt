package kiss

import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
//@SpringBootTest
class KissBackendApplicationTests {

    //    @Test
    fun contextLoads() {
    }

}
