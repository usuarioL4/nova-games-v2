package com.novagames.catalog.repository;
import com.novagames.catalog.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface ReservationRepository extends JpaRepository<Reservation,UUID> {
}
