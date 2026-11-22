package com.novagames.catalog.entity;
import jakarta.persistence.*;
import java.util.UUID;
@Entity @Table(name="reservations") public class Reservation {
    @Id public UUID id;
    @Column(nullable=false) public String owner;
    @Lob @Column(nullable=false) public String resultJson;
    @Lob @Column(nullable=false) public String requestJson;
}
