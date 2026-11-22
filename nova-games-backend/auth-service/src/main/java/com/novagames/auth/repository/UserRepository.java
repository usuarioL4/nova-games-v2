package com.novagames.auth.repository;
import com.novagames.auth.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserRepository extends JpaRepository<UserAccount,UUID> {
    Optional<UserAccount> findByEmail(String email);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByCellphone(String cellphone);
}
