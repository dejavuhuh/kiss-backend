package kiss.e_commerce.product.es

import org.springframework.data.repository.ListCrudRepository

interface SpuRepository : ListCrudRepository<Spu, Int>
