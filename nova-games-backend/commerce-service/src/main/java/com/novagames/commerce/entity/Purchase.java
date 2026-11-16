package com.novagames.commerce.entity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="purchases") public class Purchase {
    @Id public UUID id;
    @Column(nullable=false) public String owner;
    @Column(nullable=false) public String estado="PENDING";
    @Lob @Column(nullable=false) public String requestJson;
    @Lob public String resultJson;
    public Instant fecha=Instant.now();
}
