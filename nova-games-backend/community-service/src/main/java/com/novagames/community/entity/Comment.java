package com.novagames.community.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="community_comments") public class Comment {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false) public String owner;
    @Column(nullable=false) public String username;
    @Column(nullable=false,length=1000) public String texto;
    public Instant fecha=Instant.now();
}
