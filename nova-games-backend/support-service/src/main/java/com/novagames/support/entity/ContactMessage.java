package com.novagames.support.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="contact_messages") public class ContactMessage {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false,length=100) public String nombre;
    @Column(nullable=false,length=100) public String email;
    @Column(nullable=false,length=100) public String asunto;
    @Column(nullable=false,length=500) public String mensaje;
    public boolean resuelto;
    public Instant fecha=Instant.now();
}
