package kiss.error

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/system-errors")
class SystemErrorService {

    @PostMapping
    fun report() {

    }
}