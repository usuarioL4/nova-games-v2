package com.novagames.commerce.repository;
import com.novagames.commerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CartRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findByOwner(String owner);
    Optional<CartItem> findByOwnerAndProductId(String owner,Long productId);
    void deleteByOwnerAndProductId(String owner,Long productId);
}
