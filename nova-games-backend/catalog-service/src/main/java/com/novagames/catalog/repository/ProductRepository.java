package com.novagames.catalog.repository;
import com.novagames.catalog.entity.Product;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByActivoTrueOrderById();
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select p from Product p where p.id=:id") Optional<Product> lock(@Param("id") Long id);
}
