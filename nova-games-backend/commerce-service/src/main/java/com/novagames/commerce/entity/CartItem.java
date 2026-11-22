package com.novagames.commerce.entity;
import jakarta.persistence.*;
@Entity @Table(name="cart_items",uniqueConstraints=@UniqueConstraint(columnNames={"owner_id","product_id"})) public class CartItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(name="owner_id",nullable=false) public String owner;
    @Column(name="product_id",nullable=false) public Long productId;
    public int cantidad;
}
