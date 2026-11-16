package com.novagames.support.repository;
import com.novagames.support.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ContactRepository extends JpaRepository<ContactMessage,Long> {
    List<ContactMessage> findAllByOrderByFechaDesc();
}
