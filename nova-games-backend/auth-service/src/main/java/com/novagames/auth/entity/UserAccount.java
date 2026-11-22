package com.novagames.auth.entity;
import jakarta.persistence.*;
import java.util.UUID;
@Entity @Table(name="accounts",uniqueConstraints={@UniqueConstraint(columnNames="email"),@UniqueConstraint(columnNames="username"),@UniqueConstraint(columnNames="cellphone")}) public class UserAccount {
    @Id public UUID id=UUID.randomUUID();
    @Column(nullable=false) public String username;
    @Column(nullable=false) public String email;
    @Column(nullable=false) public String passwordHash;
    public String cellphone;
    public String referenceCode;
    public boolean ageConfirmed;
    public String role="USER";
}
