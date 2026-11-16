package com.novagames.commerce.repository;
import com.novagames.commerce.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PurchaseRepository extends JpaRepository<Purchase,UUID> {
    List<Purchase> findByOwnerOrderByFechaDesc(String owner);
    List<Purchase> findAllByOrderByFechaDesc();
}
