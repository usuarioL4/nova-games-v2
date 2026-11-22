package com.novagames.catalog.entity;
import jakarta.persistence.*;
import java.util.*;
import java.math.BigDecimal;
@Entity @Table(name="products") public class Product {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false) public String nombre;
    @Column(nullable=false) public String categoria;
    @Column(nullable=false,precision=12,scale=0) public BigDecimal precio;
    public int stock;
    public String imagen;
    @Column(length=2000) public String descripcion;
    @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="product_platforms") public Set<String> plataformas=new LinkedHashSet<>();
    @Version public long version;
    public boolean activo=true;
}
